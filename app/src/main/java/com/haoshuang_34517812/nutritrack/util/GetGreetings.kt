package com.haoshuang_34517812.nutritrack.util


fun getGreetings(hourOfDay: Int): String {
    return when {
        hourOfDay < 12 -> "Good morning"
        hourOfDay < 18 -> "Good afternoon"
        else -> "Good evening"
    }
}
