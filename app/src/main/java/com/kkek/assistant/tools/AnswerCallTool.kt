package com.kkek.assistant.tools

import android.content.Context
import com.kkek.assistant.data.AssistantRepository
import com.kkek.assistant.domain.model.AiTool
import com.kkek.assistant.domain.model.ToolParameter
import com.kkek.assistant.domain.model.ToolResult
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class AnswerCallTool @Inject constructor(private val repository: AssistantRepository) : AiTool {
    override val name = "answerCall"
    override val description = "Answers an incoming phone call."
    override val parameters: List<ToolParameter> = emptyList()

    override suspend fun execute(
        context: Context,
        params: Map<String, Any>,
        scope: CoroutineScope
    ): ToolResult {
        repository.acceptCall()
        return ToolResult.Success(mapOf("status" to "Call answered"))
    }
}
