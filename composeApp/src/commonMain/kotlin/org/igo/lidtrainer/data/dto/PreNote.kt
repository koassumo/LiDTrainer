package org.igo.lidtrainer.data.dto

data class PreNote(
    val id: Int,
    val questionNumber: Int,
    val language: String,
    val questionText: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val answer4: String,
    val correctAnswerIndex: Int,
    val category: String,
    val imageUrl: String?
)
