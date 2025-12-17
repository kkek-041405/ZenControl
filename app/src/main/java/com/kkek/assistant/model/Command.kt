package com.kkek.assistant.model

import kotlinx.serialization.Serializable

@Serializable
data class Command(
    val id: String = "",
    val action: String = "",
    val payload: Map<String, String> = emptyMap(),
    val timestamp: Long = 0L
)
