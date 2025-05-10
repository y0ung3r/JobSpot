package com.jobspot.data.profile

import com.jobspot.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.jobspot.domain.profile.interfaces.VerificationListener
import com.jobspot.domain.profile.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DefaultVerificationListener(private val authorizationProvider: PhoneNumberAuthorizationProvider) : VerificationListener {
    private suspend fun internalStart(onVerified: (User) -> Unit) {
        while (true) {
            val worker = authorizationProvider.getAuthorizedUser() ?: return

            if (worker.isDocumentsVerified) {
                withContext(Dispatchers.Main) {
                    onVerified(worker)
                }

                break
            }
        }
    }

    override fun start(onVerified: (User) -> Unit) {
        GlobalScope.async {
            internalStart(onVerified)
        }
    }
}