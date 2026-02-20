package org.igo.lidtrainer.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.igo.lidtrainer.data.dto.VersionInfo

class FirebaseStorageApi(
    private val client: HttpClient,
    private val bucketName: String
) {
    private val json = Json { ignoreUnknownKeys = true }

    private fun downloadUrl(path: String): String {
        val encodedPath = path.replace("/", "%2F")
        return "https://firebasestorage.googleapis.com/v0/b/$bucketName/o/$encodedPath?alt=media"
    }

    suspend fun fetchVersionInfo(): VersionInfo {
        val responseText = client.get(downloadUrl("version.json")).bodyAsText()
        return json.decodeFromString<VersionInfo>(responseText)
    }

    suspend fun fetchPackJson(fileName: String): String {
        return client.get(downloadUrl(fileName)).bodyAsText()
    }
}
