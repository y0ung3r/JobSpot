package com.jobspot.domain.profile.useCases

import com.jobspot.domain.profile.interfaces.VerificationListener
import com.jobspot.domain.profile.models.User

class StartVerificationListenerUseCase(private val verificationListener: VerificationListener) {
    fun execute(onVerified: (User) -> Unit) {
        verificationListener.start(onVerified)
    }
}