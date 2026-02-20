package org.igo.lidtrainer.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class JNote(
    val id: Int,
    val questionNumber: Int,
    val questionText: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val answer4: String,
    val correctAnswerIndex: Int,
    val category: String,
    val imageUrl: String? = null,
    val imageAttribution: String? = null
)

@Serializable
data class JPack(
    val version: Int,
    val language: String,
    val lastUpdated: String,
    val jNotes: List<JNote>
)
