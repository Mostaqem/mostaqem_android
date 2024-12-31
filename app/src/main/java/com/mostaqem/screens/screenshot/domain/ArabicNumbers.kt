package com.mostaqem.screens.screenshot.domain

fun String.toArabicNumbers(): String {
    val englishToArabicDigits = mapOf(
        '0' to '٠',
        '1' to '١',
        '2' to '٢',
        '3' to '٣',
        '4' to '٤',
        '5' to '٥',
        '6' to '٦',
        '7' to '٧',
        '8' to '٨',
        '9' to '٩'
    )
    return this.map { englishToArabicDigits[it] ?: it }.joinToString("")
}

