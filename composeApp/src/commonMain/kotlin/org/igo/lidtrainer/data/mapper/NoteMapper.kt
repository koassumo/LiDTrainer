package org.igo.lidtrainer.data.mapper

import org.igo.lidtrainer.data.dto.JNote
import org.igo.lidtrainer.data.dto.PreNote
import org.igo.lidtrainer.db.NoteEntity
import org.igo.lidtrainer.domain.model.Note

fun JNote.mapToPreNote(language: String): PreNote {
    return PreNote(
        id = this.id,
        questionNumber = this.questionNumber,
        language = language,
        questionText = this.questionText,
        answer1 = this.answer1,
        answer2 = this.answer2,
        answer3 = this.answer3,
        answer4 = this.answer4,
        correctAnswerIndex = this.correctAnswerIndex,
        category = this.category,
        imageUrl = this.imageUrl,
        imageAttribution = this.imageAttribution
    )
}

fun mergePreNotes(dePreNote: PreNote, nativePreNote: PreNote?): Note {
    return Note(
        id = dePreNote.id.toLong(),
        questionNumber = dePreNote.questionNumber,
        questionTextDe = dePreNote.questionText,
        answer1De = dePreNote.answer1,
        answer2De = dePreNote.answer2,
        answer3De = dePreNote.answer3,
        answer4De = dePreNote.answer4,
        langNative = nativePreNote?.language ?: "",
        questionTextNative = nativePreNote?.questionText ?: "",
        answer1Native = nativePreNote?.answer1 ?: "",
        answer2Native = nativePreNote?.answer2 ?: "",
        answer3Native = nativePreNote?.answer3 ?: "",
        answer4Native = nativePreNote?.answer4 ?: "",
        correctAnswerIndex = dePreNote.correctAnswerIndex,
        category = dePreNote.category,
        imageUrl = dePreNote.imageUrl,
        imageAttribution = dePreNote.imageAttribution,
        userAnswerIndex = null,
        isAnsweredCorrectly = null,
        lastAnsweredAt = null,
        isFavorite = false
    )
}

fun NoteEntity.mapToNote(): Note {
    return Note(
        id = this.id,
        questionNumber = this.questionNumber.toInt(),
        questionTextDe = this.questionTextDe,
        answer1De = this.answer1De,
        answer2De = this.answer2De,
        answer3De = this.answer3De,
        answer4De = this.answer4De,
        langNative = this.langNative,
        questionTextNative = this.questionTextNative,
        answer1Native = this.answer1Native,
        answer2Native = this.answer2Native,
        answer3Native = this.answer3Native,
        answer4Native = this.answer4Native,
        correctAnswerIndex = this.correctAnswerIndex.toInt(),
        category = this.category,
        imageUrl = this.imageUrl,
        imageAttribution = this.imageAttribution,
        userAnswerIndex = this.userAnswerIndex?.toInt(),
        isAnsweredCorrectly = this.isAnsweredCorrectly?.let { it == 1L },
        lastAnsweredAt = this.lastAnsweredAt,
        isFavorite = this.isFavorite == 1L
    )
}
