package com.kkek.assistant.modules

import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.InCallService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CallService : InCallService() {

    private var currentCall: Call? = null

    // State management for UI reactivity
    private val _isMuted = MutableStateFlow(false)
    val isMuted = _isMuted.asStateFlow()

    private val _audioRoute = MutableStateFlow(CallAudioState.ROUTE_EARPIECE)
    val audioRoute = _audioRoute.asStateFlow()

    private var callAudioState: CallAudioState? = null

    companion object {
        var instance: CallService? = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        instance = this
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        currentCall = null
        return super.onUnbind(intent)
    }

    override fun onCallAdded(call: Call) {
        currentCall = call
    }

    override fun onCallRemoved(call: Call) {
        currentCall = null
    }

    // This is the correct callback for audio state changes.
    override fun onCallAudioStateChanged(audioState: CallAudioState) {
        callAudioState = audioState
        _isMuted.value = audioState.isMuted
        _audioRoute.value = audioState.route
    }

    fun answer() {
        currentCall?.answer(0)
    }

    fun hangup() {
        currentCall?.disconnect()
    }

    fun toggleMute() {
        if (currentCall?.details?.can(Call.Details.CAPABILITY_MUTE) == true) {
            val currentlyMuted = _isMuted.value
            setMuted(!currentlyMuted)
        }
    }

    /**
     * Cycles through the available audio routes in a predictable order:
     * Earpiece -> Speaker -> Bluetooth (if available) -> Earpiece
     */
    fun cycleAudioRoute() {
        val supportedRoutes = callAudioState?.supportedRouteMask ?: return
        val currentRoute = _audioRoute.value

        when (currentRoute) {
            CallAudioState.ROUTE_EARPIECE -> {
                // Earpiece -> Speaker
                setAudioRoute(CallAudioState.ROUTE_SPEAKER)
            }
            CallAudioState.ROUTE_SPEAKER -> {
                // Speaker -> Bluetooth (if available), else -> Earpiece
                if (supportedRoutes and CallAudioState.ROUTE_BLUETOOTH != 0) {
                    setAudioRoute(CallAudioState.ROUTE_BLUETOOTH)
                } else {
                    setAudioRoute(CallAudioState.ROUTE_EARPIECE)
                }
            }
            CallAudioState.ROUTE_BLUETOOTH -> {
                // Bluetooth -> Earpiece
                setAudioRoute(CallAudioState.ROUTE_EARPIECE)
            }
        }
    }
}