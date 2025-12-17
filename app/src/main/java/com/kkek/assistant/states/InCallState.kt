package com.kkek.assistant.states

import com.kkek.assistant.model.Kind
import com.kkek.assistant.model.ListItem
import com.kkek.assistant.model.ToolAction

object InCallState {
    fun build(): List<ListItem> = listOf(
        ListItem(
            kind = Kind.SIMPLE,
            text = "End Call",
            shortNext = listOf(ToolAction("endCall")),
            longNext = listOf(ToolAction("cycleAudioRoute")),
            longPrevious = listOf(ToolAction("toggleMute"))
        ),
        ListItem(
            kind = Kind.SIMPLE,
            text = "volume down to end call /n volume up to mute"
        )
    )
}

object IncomingCallState {
    fun build(): List<ListItem> = listOf(
        ListItem(
            kind = Kind.SIMPLE,
            text = "Answer/Reject Call",
            shortNext = listOf(ToolAction("answerCall")),
            shortPrevious = listOf(ToolAction("endCall")),
        ),
    )
}


object DialingState {
    fun build(): List<ListItem> = listOf(
        ListItem(
            kind = Kind.SIMPLE,
            text = "End Call",
            shortNext = listOf(ToolAction("endCall")),
            longNext = listOf(ToolAction("endCall")),
        )
    )
}