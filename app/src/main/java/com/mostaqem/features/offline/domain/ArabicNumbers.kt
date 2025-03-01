package com.mostaqem.features.offline.domain

fun Int.toArabicNumbers(): String {
    return this.toString().map { digit ->
        when (digit) {
            '0' -> '٠'
            '1' -> '١'
            '2' -> '٢'
            '3' -> '٣'
            '4' -> '٤'
            '5' -> '٥'
            '6' -> '٦'
            '7' -> '٧'
            '8' -> '٨'
            '9' -> '٩'
            else -> digit
        }
    }.joinToString("")
}
