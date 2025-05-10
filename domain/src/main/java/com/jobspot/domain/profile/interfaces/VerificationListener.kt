package com.jobspot.domain.profile.interfaces

import com.jobspot.domain.profile.models.User

interface VerificationListener {
    fun start(onVerified: (User) -> Unit)
}