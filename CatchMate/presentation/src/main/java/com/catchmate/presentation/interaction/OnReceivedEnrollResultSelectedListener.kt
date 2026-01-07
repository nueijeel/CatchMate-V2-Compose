package com.catchmate.presentation.interaction

interface OnReceivedEnrollResultSelectedListener {
    fun onReceivedEnrollRejected(enrollId: Long)

    fun onReceivedEnrollAccepted(enrollId: Long)
}
