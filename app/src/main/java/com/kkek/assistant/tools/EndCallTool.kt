package com.kkek.assistant.tools

import android.content.Context
import com.kkek.assistant.data.AssistantRepository
import com.kkek.assistant.domain.model.AiTool
import com.kkek.assistant.domain.model.ToolParameter
import com.kkek.assistant.domain.model.ToolResult
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class EndCallTool @Inject constructor(private val repository: AssistantRepository) : AiTool {
    override val name = "endCall"
    override val description = "Ends the current phone call."
    override val parameters: List<ToolParameter> = emptyList()

    override suspend fun execute(
        context: Context,
        params: Map<String, Any>,
        scope: CoroutineScope
    ): ToolResult {
        repository.rejectCall()
        return ToolResult.Success(mapOf("status" to "Call ended"))
    }
}
