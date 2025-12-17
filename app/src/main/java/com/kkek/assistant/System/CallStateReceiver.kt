package com.kkek.assistant.System

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallStateReceiver : BroadcastReceiver() {

    companion object {
        // Keep track of the last state to differentiate between dialing and answering
        private var previousState: String? = TelephonyManager.EXTRA_STATE_IDLE
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            return
        }

        val currentState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        Log.d("CallStateReceiver", "Current State: $currentState, Previous State: $previousState")

        val serviceIntent = Intent(context, CallStateService::class.java).apply {
            putExtra(CallStateService.EXTRA_CURRENT_STATE, currentState)
            putExtra(CallStateService.EXTRA_PREVIOUS_STATE, previousState)
            // Pass the phone number along if it exists
            if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
                putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))
            }
        }
        context.startService(serviceIntent)

        // Update the previous state after processing.
        // We only update the state when it changes to prevent misinterpretation on redial scenarios.
        if (currentState != null && currentState != previousState) {
            previousState = currentState
        }
    }
}
