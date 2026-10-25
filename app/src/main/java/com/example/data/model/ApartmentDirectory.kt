package com.example.data.model

import com.example.data.local.UserEntity

data class FlatCredential(
    val floorNumber: Int,
    val flatNumber: String,
    val residentName: String,
    val defaultPassword: String,
    val contactNumber: String
)

object ApartmentDirectory {

    const val AUTHORITY_USERNAME = "authority"
    const val AUTHORITY_PASSWORD = "6383898769"
    const val AUTHORITY_DISPLAY_NAME = "SRM Facility Authority Office"
    const val AUTHORITY_CONTACT = "+91 63838 98769"

    val DEFAULT_FLATS: List<FlatCredential> = listOf(
        // Floor 1
        FlatCredential(1, "Flat 101", "R. SHAM SUDHAN", "7708994065", "+91 77089 94065"),
        FlatCredential(1, "Flat 102", "P. MOHAN", "6383898769", "+91 63838 98769"),
        FlatCredential(1, "Flat 105", "M. JAI MUGESH", "9361077508", "+91 93610 77508"),
        FlatCredential(1, "Flat 108", "N. MADHUVARSHAN", "8838449468", "+91 88384 49468"),

        // Floor 2
        FlatCredential(2, "Flat 201", "K. MANI MARAN", "7845968905", "+91 78459 68905"),
        FlatCredential(2, "Flat 204", "T S. ABINAYA", "8825742523", "+91 88257 42523"),
        FlatCredential(2, "Flat 206", "NARAYANAN MOORTHI", "SRM@F2-206", "+91 88384 49468"),
        FlatCredential(2, "Flat 208", "SIVANESAN", "SRM@F2-208", "+91 88257 42523"),

        // Floor 3
        FlatCredential(3, "Flat 302", "N. Geetha", "SRM@F3-302", "+91 98401 33302"),
        FlatCredential(3, "Flat 305", "V. Suresh", "SRM@F3-305", "+91 98401 33305"),
        FlatCredential(3, "Flat 308", "T. Dinesh", "SRM@F3-308", "+91 98401 33308"),
        FlatCredential(3, "Flat 310", "K. Priya", "SRM@F3-310", "+91 98401 33310"),

        // Floor 4
        FlatCredential(4, "Flat 401", "S. Ramesh", "SRM@F4-401", "+91 98401 44401"),
        FlatCredential(4, "Flat 402", "Mohan Palaniappan", "SRM@F4-402", "+91 6380244185"),
        FlatCredential(4, "Flat 405", "Dr. E. Saravanan", "SRM@F4-405", "+91 98401 44405"),
        FlatCredential(4, "Flat 408", "M. Vani", "SRM@F4-408", "+91 98401 44408")
    )

    fun getInitialUserEntities(): List<UserEntity> {
        val users = mutableListOf<UserEntity>()

        // 1. Authority Account
        users.add(
            UserEntity(
                username = AUTHORITY_USERNAME,
                password = AUTHORITY_PASSWORD,
                role = UserRole.AUTHORITY,
                floorNumber = 0,
                flatNumber = "ADMIN-HQ",
                residentName = AUTHORITY_DISPLAY_NAME,
                contactNumber = AUTHORITY_CONTACT
            )
        )

        // Also alias "admin"
        users.add(
            UserEntity(
                username = "admin",
                password = AUTHORITY_PASSWORD,
                role = UserRole.AUTHORITY,
                floorNumber = 0,
                flatNumber = "ADMIN-HQ",
                residentName = AUTHORITY_DISPLAY_NAME,
                contactNumber = AUTHORITY_CONTACT
            )
        )

        // 2. Pre-seeded Resident Flats
        DEFAULT_FLATS.forEach { flat ->
            // Normalize flat identifier e.g. "flat_101", "101", "Flat 101"
            val baseNumber = flat.flatNumber.replace(Regex("[^0-9]"), "")
            users.add(
                UserEntity(
                    username = "flat_$baseNumber",
                    password = flat.defaultPassword,
                    role = UserRole.RESIDENT,
                    floorNumber = flat.floorNumber,
                    flatNumber = flat.flatNumber,
                    residentName = flat.residentName,
                    contactNumber = flat.contactNumber
                )
            )
        }

        return users
    }
}
