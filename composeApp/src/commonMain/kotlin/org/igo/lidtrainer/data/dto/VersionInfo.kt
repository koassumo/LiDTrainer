package org.igo.lidtrainer.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VersionInfo(
    val packVersion: Int,
    val lastUpdated: String
)
