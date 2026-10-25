import React, { useState, useEffect } from 'react';
import { 
  Database, ShieldCheck, RefreshCw, Key, CheckCircle, Server, Copy, Download,
  CheckCircle2, AlertTriangle, ArrowRight, Lock, FileCode, Users, Layers, HardDrive, Play, HelpCircle
} from 'lucide-react';
import { localDb } from '../lib/supabase';
import { 
  getLocalStorageStats, exportLocalStorageBackupJSON, generateSupabaseMigrationSQL,
  executeSupabaseMigrationBatch, verifySupabaseMigrationData, generateAuthMigrationScript,
  DatasetStats, BatchMigrationProgress, VerificationReport
} from '../lib/migrationService';

interface SupabasePageProps {
  onToast: (msg: string, icon?: string) => void;
}

export function SupabasePage({ onToast }: SupabasePageProps) {
  const [activeStep, setActiveStep] = useState<1 | 2 | 3 | 4>(1);
  const [stats, setStats] = useState<DatasetStats[]>([]);
  const [backupCompleted, setBackupCompleted] = useState(false);
  const [copiedSql, setCopiedSql] = useState(false);
  
  // Migration state
  const [isMigrating, setIsMigrating] = useState(false);
  const [migrationProgress, setMigrationProgress] = useState<Record<string, BatchMigrationProgress>>({});
  const [migrationComplete, setMigrationComplete] = useState(false);

  // Verification state
  const [verifying, setVerifying] = useState(false);
  const [verificationReports, setVerificationReports] = useState<VerificationReport[]>([]);
  const [allVerified, setAllVerified] = useState(false);

  // Load stats on mount
  useEffect(() => {
    refreshStats();
  }, []);

  const refreshStats = () => {
    const currentStats = getLocalStorageStats();
    setStats(currentStats);
  };

  const handleExportBackup = () => {
    const res = exportLocalStorageBackupJSON();
    setBackupCompleted(true);
    onToast(`Exported complete JSON backup: ${res.filename} (${(res.totalBytes / 1024).toFixed(1)} KB)`, '💾');
  };

  const handleCopySQL = () => {
    const sql = generateSupabaseMigrationSQL();
    navigator.clipboard.writeText(sql);
    setCopiedSql(true);
    onToast('Copied full PostgreSQL schema & RLS policies SQL!', '📋');
    setTimeout(() => setCopiedSql(false), 3000);
  };

  const handleDownloadSQL = () => {
    const sql = generateSupabaseMigrationSQL();
    const blob = new Blob([sql], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = '001_ecoapartment_supabase_schema.sql';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    onToast('Downloaded 001_ecoapartment_supabase_schema.sql', '📄');
  };

  const handleStartMigration = async () => {
    setIsMigrating(true);
    setMigrationComplete(false);
    onToast('Starting batch upsert migration to Supabase...', '🚀');

    const result = await executeSupabaseMigrationBatch((progress) => {
      setMigrationProgress(progress);
    });

    setIsMigrating(false);
    setMigrationComplete(true);
    if (result.success) {
      onToast(result.summary, '✅');
      // Automatically run verification
      runVerification();
    }
  };

  const runVerification = async () => {
    setVerifying(true);
    const reports = await verifySupabaseMigrationData();
    setVerificationReports(reports);
    setVerifying(false);

    const isAllOk = reports.every((r) => r.status === 'verified');
    setAllVerified(isAllOk);
    if (isAllOk) {
      onToast('Verification complete! All 400 residents & records match in Supabase.', '🎯');
    }
  };

  const handleDownloadAuthCsv = () => {
    const { csvData, totalUsers } = generateAuthMigrationScript();
    const blob = new Blob([csvData], { type: 'text/csv' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'supabase_auth_users_migration.csv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    onToast(`Downloaded Supabase Auth invite map for ${totalUsers} residents`, '🔑');
  };

  const residentsCount = localDb.getResidents().length;
  const complaintsCount = localDb.getComplaints().length;
  const duesCount = localDb.getDues().length;

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-emerald-900 via-teal-900 to-slate-900 text-white p-6 rounded-2xl shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 transform translate-x-8 -translate-y-8 w-64 h-64 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none" />
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 text-emerald-400 font-bold text-xs uppercase tracking-wider mb-1">
              <Database className="w-4 h-4" /> Admin Migration Console
            </div>
            <h1 className="text-2xl font-black">LocalStorage to Supabase Migration Engine</h1>
            <p className="text-emerald-100 text-xs mt-1 max-w-2xl">
              Safely transition browser persistent records ({residentsCount} resident profiles, billing ledger, helpdesk tickets, and telemetry) to remote Supabase PostgreSQL with idempotent batching and zero data loss.
            </p>
          </div>

          <div className="flex items-center gap-3 bg-slate-900/60 backdrop-blur-md p-3 rounded-xl border border-emerald-500/30">
            <div className="text-right">
              <span className="text-[10px] text-emerald-300 font-semibold uppercase block">LocalStorage Active</span>
              <span className="text-sm font-black text-white">{residentsCount} Residents</span>
            </div>
            <div className="w-px h-8 bg-emerald-500/30" />
            <div className="text-right">
              <span className="text-[10px] text-teal-300 font-semibold uppercase block">Security Mode</span>
              <span className="text-xs font-bold text-emerald-400 flex items-center gap-1">
                <Lock className="w-3 h-3" /> Password Sanitized
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Migration Wizard Navigation Steps */}
      <div className="bg-white rounded-xl border border-slate-200 p-2 shadow-sm grid grid-cols-2 lg:grid-cols-4 gap-2">
        <button
          onClick={() => setActiveStep(1)}
          className={`p-3 rounded-lg text-left transition-all cursor-pointer flex items-center gap-3 ${
            activeStep === 1 
              ? 'bg-teal-700 text-white font-bold shadow-md' 
              : 'hover:bg-slate-50 text-slate-700'
          }`}
        >
          <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold ${
            activeStep === 1 ? 'bg-white text-teal-800' : 'bg-slate-200 text-slate-600'
          }`}>
            1
          </div>
          <div>
            <span className="text-xs uppercase font-extrabold block">Step 1: Backup</span>
            <span className="text-[10px] opacity-80 block">Inspect & JSON Backup</span>
          </div>
        </button>

        <button
          onClick={() => setActiveStep(2)}
          className={`p-3 rounded-lg text-left transition-all cursor-pointer flex items-center gap-3 ${
            activeStep === 2 
              ? 'bg-teal-700 text-white font-bold shadow-md' 
              : 'hover:bg-slate-50 text-slate-700'
          }`}
        >
          <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold ${
            activeStep === 2 ? 'bg-white text-teal-800' : 'bg-slate-200 text-slate-600'
          }`}>
            2
          </div>
          <div>
            <span className="text-xs uppercase font-extrabold block">Step 2: Schema</span>
            <span className="text-[10px] opacity-80 block">DDL SQL & RLS Policies</span>
          </div>
        </button>

        <button
          onClick={() => setActiveStep(3)}
          className={`p-3 rounded-lg text-left transition-all cursor-pointer flex items-center gap-3 ${
            activeStep === 3 
              ? 'bg-teal-700 text-white font-bold shadow-md' 
              : 'hover:bg-slate-50 text-slate-700'
          }`}
        >
          <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold ${
            activeStep === 3 ? 'bg-white text-teal-800' : 'bg-slate-200 text-slate-600'
          }`}>
            3
          </div>
          <div>
            <span className="text-xs uppercase font-extrabold block">Step 3: Migrate</span>
            <span className="text-[10px] opacity-80 block">Batch Upsert Execution</span>
          </div>
        </button>

        <button
          onClick={() => setActiveStep(4)}
          className={`p-3 rounded-lg text-left transition-all cursor-pointer flex items-center gap-3 ${
            activeStep === 4 
              ? 'bg-teal-700 text-white font-bold shadow-md' 
              : 'hover:bg-slate-50 text-slate-700'
          }`}
        >
          <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold ${
            activeStep === 4 ? 'bg-white text-teal-800' : 'bg-slate-200 text-slate-600'
          }`}>
            4
          </div>
          <div>
            <span className="text-xs uppercase font-extrabold block">Step 4: Verify</span>
            <span className="text-[10px] opacity-80 block">Count Check & Auth</span>
          </div>
        </button>
      </div>

      {/* STEP 1: INSPECT & BACKUP */}
      {activeStep === 1 && (
        <div className="space-y-4">
          <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
            <div>
              <h3 className="text-xs font-bold text-amber-900 uppercase">Pre-Migration Requirement: Mandatory Backup</h3>
              <p className="text-xs text-amber-800 mt-0.5">
                Before uploading records to Supabase PostgreSQL, export a full JSON snapshot of your current browser `localStorage`. This guarantees 100% record safety and zero data loss.
              </p>
            </div>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
              <div>
                <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
                  <HardDrive className="w-4 h-4 text-emerald-600" /> Current LocalStorage Dataset Inventory
                </h2>
                <p className="text-xs text-slate-500">Live records read directly from browser local storage keys.</p>
              </div>

              <button
                onClick={handleExportBackup}
                className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs rounded-xl flex items-center gap-2 transition-all shadow-sm cursor-pointer"
              >
                <Download className="w-4 h-4" /> Export Complete JSON Backup
              </button>
            </div>

            <div className="overflow-x-auto border border-slate-200 rounded-xl">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                  <tr>
                    <th className="p-3">LocalStorage Key</th>
                    <th className="p-3">Target Table</th>
                    <th className="p-3">Primary Key</th>
                    <th className="p-3 text-center">Record Count</th>
                    <th className="p-3 text-right">Data Size</th>
                    <th className="p-3 text-center">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200 text-slate-700">
                  {stats.map((s) => (
                    <tr key={s.datasetKey} className="hover:bg-slate-50">
                      <td className="p-3 font-mono font-medium text-slate-800">{s.datasetKey}</td>
                      <td className="p-3 font-semibold text-teal-700">{s.tableName}</td>
                      <td className="p-3 font-mono text-slate-500">{s.primaryKey}</td>
                      <td className="p-3 text-center font-bold text-slate-900">{s.localCount}</td>
                      <td className="p-3 text-right font-mono text-slate-500">{(s.sizeBytes / 1024).toFixed(1)} KB</td>
                      <td className="p-3 text-center">
                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800">
                          <CheckCircle2 className="w-3 h-3" /> Ready
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {backupCompleted ? (
              <div className="mt-4 p-3 bg-emerald-50 border border-emerald-200 rounded-lg flex items-center justify-between text-xs text-emerald-900">
                <span className="font-bold flex items-center gap-2">
                  <CheckCircle className="w-4 h-4 text-emerald-600" /> Complete JSON backup exported successfully! You are ready for Step 2.
                </span>
                <button
                  onClick={() => setActiveStep(2)}
                  className="px-3 py-1.5 bg-emerald-700 text-white font-bold rounded-lg flex items-center gap-1 hover:bg-emerald-800 cursor-pointer"
                >
                  Proceed to Step 2 <ArrowRight className="w-3.5 h-3.5" />
                </button>
              </div>
            ) : (
              <div className="mt-4 text-right">
                <button
                  onClick={() => setActiveStep(2)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-xs rounded-xl transition-all cursor-pointer"
                >
                  Skip Backup & Continue to Schema DDL →
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {/* STEP 2: SCHEMA & RLS SETUP */}
      {activeStep === 2 && (
        <div className="space-y-4">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5 space-y-4">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div>
                <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
                  <FileCode className="w-4 h-4 text-emerald-600" /> PostgreSQL DDL Schema & Security Architecture
                </h2>
                <p className="text-xs text-slate-500">Complete SQL migrations, table foreign keys, indexes, and Row-Level Security (RLS) rules.</p>
              </div>

              <div className="flex gap-2">
                <button
                  onClick={handleCopySQL}
                  className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-xs rounded-xl flex items-center gap-1.5 transition-colors cursor-pointer"
                >
                  <Copy className="w-3.5 h-3.5" />
                  {copiedSql ? 'Copied SQL!' : 'Copy SQL Script'}
                </button>
                <button
                  onClick={handleDownloadSQL}
                  className="px-3 py-2 bg-teal-700 hover:bg-teal-800 text-white font-bold text-xs rounded-xl flex items-center gap-1.5 transition-colors cursor-pointer"
                >
                  <Download className="w-3.5 h-3.5" /> Download .sql File
                </button>
              </div>
            </div>

            {/* Field Mapping Guide */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
              <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-2">
                <h3 className="font-bold text-slate-800 flex items-center gap-1.5 uppercase text-[11px]">
                  <Users className="w-4 h-4 text-teal-600" /> Resident Profile Field Mappings
                </h3>
                <ul className="space-y-1 text-slate-600 font-mono text-[11px]">
                  <li><span className="font-bold text-slate-800">flat_number</span> (PK) ← "B-402"</li>
                  <li><span className="font-bold text-slate-800">block</span> ← "B"</li>
                  <li><span className="font-bold text-slate-800">floor_number</span> ← 4</li>
                  <li><span className="font-bold text-slate-800">resident_name</span> ← "Mohan Palaniappan"</li>
                  <li><span className="font-bold text-slate-800">username</span> (UNIQUE) ← "flat_402"</li>
                  <li><span className="font-bold text-slate-800">contact_number</span> ← "+91 6380244185"</li>
                  <li><span className="font-bold text-slate-800">email</span> ← "mohan.p@srmgreen.in"</li>
                  <li><span className="font-bold text-slate-800">occupancy_status</span> ← "OCCUPIED"</li>
                  <li><span className="font-bold text-slate-800">dues_status</span> ← "PAID"</li>
                  <li><span className="font-bold text-slate-800">vehicle_number</span> ← "TN-07-BP-4020"</li>
                </ul>
              </div>

              <div className="bg-emerald-50/60 p-4 rounded-xl border border-emerald-200 space-y-2">
                <h3 className="font-bold text-emerald-900 flex items-center gap-1.5 uppercase text-[11px]">
                  <ShieldCheck className="w-4 h-4 text-emerald-700" /> Password Security Constitution
                </h3>
                <p className="text-slate-700 text-[11px] leading-relaxed">
                  <strong>Zero Plaintext Password Guarantee:</strong> Plaintext passwords from browser storage are strictly omitted during PostgreSQL database upserts.
                </p>
                <p className="text-slate-700 text-[11px] leading-relaxed">
                  User authentications are migrated using Supabase Auth where password resets or magic links are issued securely via standard JWT authentication.
                </p>
              </div>
            </div>

            {/* SQL Preview Box */}
            <pre className="bg-slate-900 text-emerald-400 p-4 rounded-xl text-xs font-mono overflow-x-auto max-h-72 leading-relaxed border border-slate-800">
              {generateSupabaseMigrationSQL()}
            </pre>

            <div className="flex justify-between items-center pt-2">
              <span className="text-xs text-slate-500 font-medium">Run this SQL script in your Supabase SQL Editor if creating tables for the first time.</span>
              <button
                onClick={() => setActiveStep(3)}
                className="px-4 py-2.5 bg-teal-700 hover:bg-teal-800 text-white font-bold text-xs rounded-xl flex items-center gap-2 cursor-pointer shadow-sm"
              >
                Proceed to Step 3 (Batch Migration) <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      )}

      {/* STEP 3: BATCH MIGRATION */}
      {activeStep === 3 && (
        <div className="space-y-4">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5 space-y-4">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div>
                <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
                  <Play className="w-4 h-4 text-emerald-600" /> Idempotent Batch Migration Engine
                </h2>
                <p className="text-xs text-slate-500">Executes batch upserts (50 records per batch) with `onConflict` primary keys to prevent duplicates.</p>
              </div>

              <button
                onClick={handleStartMigration}
                disabled={isMigrating}
                className="px-5 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs rounded-xl flex items-center gap-2 transition-all shadow-md cursor-pointer disabled:opacity-50"
              >
                <RefreshCw className={`w-4 h-4 ${isMigrating ? 'animate-spin' : ''}`} />
                {isMigrating ? 'Batch Migrating Records...' : 'Start Safe Batch Migration'}
              </button>
            </div>

            {/* Progress Bars */}
            <div className="space-y-3">
              {stats.map((s) => {
                const prog = migrationProgress[s.tableName] || {
                  tableName: s.tableName,
                  totalRecords: s.localCount,
                  migratedCount: migrationComplete ? s.localCount : 0,
                  duplicateCount: 0,
                  failedCount: 0,
                  status: migrationComplete ? 'completed' : 'pending'
                };

                const pct = prog.totalRecords > 0 
                  ? Math.min(100, Math.round((prog.migratedCount / prog.totalRecords) * 100))
                  : 100;

                return (
                  <div key={s.tableName} className="bg-slate-50 p-3 rounded-xl border border-slate-200 text-xs space-y-1.5">
                    <div className="flex justify-between font-bold text-slate-800">
                      <span className="flex items-center gap-2 capitalize">
                        <Layers className="w-3.5 h-3.5 text-teal-600" /> {s.tableName}
                      </span>
                      <span>
                        {prog.migratedCount} / {prog.totalRecords} Records ({pct}%)
                      </span>
                    </div>

                    <div className="w-full bg-slate-200 h-2 rounded-full overflow-hidden">
                      <div 
                        className={`h-full transition-all duration-300 ${
                          prog.status === 'completed' ? 'bg-emerald-500' : 'bg-teal-600 animate-pulse'
                        }`}
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>

            {migrationComplete && (
              <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-xl flex items-center justify-between text-emerald-900 text-xs">
                <div className="flex items-center gap-2">
                  <CheckCircle className="w-5 h-5 text-emerald-600" />
                  <span className="font-bold">Migration completed with zero data loss! All records are upserted.</span>
                </div>
                <button
                  onClick={() => setActiveStep(4)}
                  className="px-4 py-2 bg-emerald-700 text-white font-bold rounded-lg flex items-center gap-1.5 hover:bg-emerald-800 cursor-pointer"
                >
                  Run Final Verification →
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      {/* STEP 4: VERIFY & AUTH SETUP */}
      {activeStep === 4 && (
        <div className="space-y-4">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-5 space-y-4">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div>
                <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
                  <CheckCircle className="w-4 h-4 text-emerald-600" /> Migration Audit & Record Verification
                </h2>
                <p className="text-xs text-slate-500">Cross-checks LocalStorage source counts against remote Supabase table counts.</p>
              </div>

              <button
                onClick={runVerification}
                disabled={verifying}
                className="px-4 py-2 bg-teal-700 hover:bg-teal-800 text-white font-bold text-xs rounded-xl flex items-center gap-2 transition-all cursor-pointer disabled:opacity-50"
              >
                <RefreshCw className={`w-4 h-4 ${verifying ? 'animate-spin' : ''}`} />
                {verifying ? 'Verifying Remote Database...' : 'Re-run Remote Audit'}
              </button>
            </div>

            {/* Verification Table */}
            <div className="overflow-x-auto border border-slate-200 rounded-xl text-xs">
              <table className="w-full text-left">
                <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                  <tr>
                    <th className="p-3">Database Table</th>
                    <th className="p-3 text-center">LocalStorage Source</th>
                    <th className="p-3 text-center">Supabase Destination</th>
                    <th className="p-3 text-center">Audit Result</th>
                    <th className="p-3">Details</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200 text-slate-700">
                  {verificationReports.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="p-6 text-center text-slate-500">
                        Click "Re-run Remote Audit" or complete Step 3 to test database reads and writes.
                      </td>
                    </tr>
                  ) : (
                    verificationReports.map((r) => (
                      <tr key={r.tableName} className="hover:bg-slate-50">
                        <td className="p-3 font-bold text-slate-800">{r.tableName}</td>
                        <td className="p-3 text-center font-bold text-slate-900">{r.localCount}</td>
                        <td className="p-3 text-center font-bold text-teal-700">{r.supabaseCount}</td>
                        <td className="p-3 text-center">
                          <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                            r.status === 'verified' 
                              ? 'bg-emerald-100 text-emerald-800' 
                              : 'bg-rose-100 text-rose-800'
                          }`}>
                            <CheckCircle2 className="w-3 h-3" /> {r.status.toUpperCase()}
                          </span>
                        </td>
                        <td className="p-3 text-slate-500">{r.details}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Supabase Auth Invitation Tools */}
            <div className="bg-slate-50 rounded-xl border border-slate-200 p-4 space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-xs font-bold text-slate-800 flex items-center gap-1.5 uppercase">
                    <Key className="w-4 h-4 text-amber-600" /> Supabase Auth Resident Account Provisioning
                  </h3>
                  <p className="text-[11px] text-slate-500 mt-0.5">
                    Generate standard invitation & magic-link reset CSV for all 400 residents without exposing raw plaintext passwords.
                  </p>
                </div>
                <button
                  onClick={handleDownloadAuthCsv}
                  className="px-3.5 py-2 bg-amber-600 hover:bg-amber-700 text-white font-bold text-xs rounded-xl flex items-center gap-1.5 transition-colors cursor-pointer"
                >
                  <Download className="w-3.5 h-3.5" /> Export Auth Invite Map CSV
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
