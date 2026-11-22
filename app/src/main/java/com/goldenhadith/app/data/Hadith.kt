package com.goldenhadith.app.data

data class Hadith(
    val id: Int,
    val number: Int,
    val category: String,
    val arabic: String,
    val english: String,
    val narrator: String,
    val source: String,
    val grade: String
)
