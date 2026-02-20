package org.igo.lidtrainer.domain.model

data class Note(
    val id: Long,
    val questionNumber: Int,

    // German (original exam language — always present)
    val questionTextDe: String,
    val answer1De: String,
    val answer2De: String,
    val answer3De: String,
    val answer4De: String,

    // Native language (user's chosen translation)
    val langNative: String,
    val questionTextNative: String,
    val answer1Native: String,
    val answer2Native: String,
    val answer3Native: String,
    val answer4Native: String,

    // Question metadata
    val correctAnswerIndex: Int,
    val category: String,
    val imageUrl: String?,
    val imageAttribution: String?,

    // User progress
    val userAnswerIndex: Int?,
    val isAnsweredCorrectly: Boolean?,
    val lastAnsweredAt: Long?,
    val isFavorite: Boolean = false
)
