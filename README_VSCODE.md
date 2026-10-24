# 🚀 How to Run EcoApartment OS in VS Code (Localhost Guide)

Follow these simple, step-by-step instructions to run the **EcoApartment OS** website locally in Visual Studio Code (VS Code) with **100% full features, offline readiness, and zero errors**.

---

## 📋 Prerequisites
Make sure you have Node.js installed on your computer:
- **Node.js** (v18.x, v20.x, or newer): Download from [nodejs.org](https://nodejs.org)
- *(Optional)* **Python 3**: For regenerating the 11-page PDF manual locally.

---

## 🛠️ Step-by-Step Instructions

### Step 1: Open the Project in VS Code
1. Download or extract the project ZIP folder onto your computer.
2. Open **VS Code**.
3. Go to **File ➔ Open Folder...** and select the extracted project workspace root directory.

### Step 2: Open VS Code Integrated Terminal
1. In VS Code, open the terminal by pressing `Ctrl + ~` (or `Cmd + ~` on Mac) or clicking **Terminal ➔ New Terminal** from the top menu.

### Step 3: Start the Web Server
In the terminal, run either of the following commands:

```bash
npm start
```

*OR directly with Node.js:*

```bash
node server.js
```

### Step 4: Open in Web Browser
Once started, you will see the terminal output:
```text
[EcoApartment OS] Initializing web server on port 3000...
[EcoApartment OS] Web server listening on http://0.0.0.0:3000
```

Open your web browser (Chrome, Edge, Firefox, Safari, or Brave) and navigate to:
👉 **`http://localhost:3000`**

---

## 🔑 Login Credentials

### 👤 Primary Resident Login (Mohan Palaniappan)
* **Username**: `6383898769` or `flat_402`
* **Password**: `6383898769` or `mohan123`
* **Profile Details**: Mohan Palaniappan • Flat B-402 • Phone: `+91 63838 98769` • Email: `mohanpalaniappan007@gmail.com`

### 🏛️ SRM Facility Authority Admin
* **Username**: `admin` or `authority`
* **Password**: `6383898769`
* **Profile Details**: SRM Facility Authority Desk • Phone: `+91 63838 98769` • Email: `admin@srmgreen.in`

---

## 📑 How to Regenerate the 11-Page PDF Credentials Manual Locally

To regenerate `EcoApartment_Credentials_Directory.pdf` locally in VS Code:
In the VS Code terminal, run:

```bash
python3 generate_pdf.py
```

The PDF will immediately generate in `public/EcoApartment_Credentials_Directory.pdf`.

---

## 🛠️ Troubleshooting & VS Code Tips

1. **Port 3000 in use?**
   If port 3000 is busy, specify a custom port in VS Code terminal:
   ```bash
   PORT=8080 node server.js
   ```
   Then open `http://localhost:8080`.

2. **Offline Functionality**:
   The entire application runs 100% offline without needing internet connections or cloud dependencies.

3. **Zero Configuration**:
   No database setup or `.env` configuration is required — everything is pre-built and self-contained!
