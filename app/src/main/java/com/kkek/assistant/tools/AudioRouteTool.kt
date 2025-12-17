package com.kkek.assistant.tools

import android.content.Context
import com.kkek.assistant.data.AssistantRepository
import com.kkek.assistant.domain.model.AiTool
import com.kkek.assistant.domain.model.ToolParameter
import com.kkek.assistant.domain.model.ToolResult
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class AudioRouteTool @Inject constructor(private val repository: AssistantRepository) : AiTool {
    override val name = "cycleAudioRoute"
    override val description = "Cycles through the available audio output routes (e.g., speaker, earpiece)."
    override val parameters: List<ToolParameter> = emptyList()

    override suspend fun execute(
        context: Context,
        params: Map<String, Any>,
        scope: CoroutineScope
    ): ToolResult {
        repository.cycleAudioRoute()
        return ToolResult.Success(mapOf("status" to "Audio route cycled"))
    }
}
