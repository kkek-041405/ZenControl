package com.kkek.assistant.System

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import com.kkek.assistant.data.AssistantRepository
import com.kkek.assistant.data.model.CallState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallStateService : Service() {

    @Inject
    lateinit var repository: AssistantRepository

    companion object {
        const val EXTRA_CURRENT_STATE = "EXTRA_CURRENT_STATE"
        const val EXTRA_PREVIOUS_STATE = "EXTRA_PREVIOUS_STATE"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val currentState = intent?.getStringExtra(EXTRA_CURRENT_STATE)
        val previousState = intent?.getStringExtra(EXTRA_PREVIOUS_STATE)
        val phoneNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        Log.d("CallStateService", "Processing state. Current: $currentState, Previous: $previousState")

        val newCallState = determineCallState(currentState, previousState, phoneNumber)
        repository.updateCallState(newCallState)

        stopSelf()
        return START_NOT_STICKY
    }

    private fun determineCallState(currentState: String?, previousState: String?, phoneNumber: String?): CallState {
        return when (currentState) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                Log.d("CallStateService", "State determined: RINGING")
                CallState(CallState.State.RINGING, phoneNumber)
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                if (previousState == TelephonyManager.EXTRA_STATE_IDLE) {
                    Log.d("CallStateService", "State determined: DIALING (from OFFHOOK transition)")
                    CallState(CallState.State.DIALING, phoneNumber)
                } else {
                    Log.d("CallStateService", "State determined: OFFHOOK")
                    CallState(CallState.State.OFFHOOK, phoneNumber)
                }
            }
            else -> {
                Log.d("CallStateService", "State determined: IDLE")
                CallState(CallState.State.IDLE)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
