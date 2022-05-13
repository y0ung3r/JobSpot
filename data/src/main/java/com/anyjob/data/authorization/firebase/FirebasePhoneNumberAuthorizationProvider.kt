package com.anyjob.data.authorization.firebase

import com.anyjob.data.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.data.authorization.exceptions.AuthorizationException
import com.anyjob.data.authorization.exceptions.AuthorizationServerException
import com.anyjob.data.authorization.exceptions.InvalidCredentialsException
import com.anyjob.data.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class FirebasePhoneNumberAuthorizationProvider(private val firebaseProvider: FirebaseAuth) : PhoneNumberAuthorizationProvider {
    private var _verificationId: String? = null
    private var _forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var _onCodeSent: (Result<Boolean>) -> Unit
    private lateinit var _onCodeVerified: (Result<Unit>) -> Unit

    private val _phoneNumberAuthenticationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
            // Авторизация будет происходить только при вызове метода verifyCode()
        }

        override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            val isResent = _forceResendingToken != null

            _verificationId = verificationId
            _forceResendingToken = forceResendingToken

            _onCodeSent.invoke(
                Result.success(isResent)
            )
        }

        override fun onVerificationFailed(firebaseException: FirebaseException) {
            val authorizationException = when (firebaseException) {
                is FirebaseAuthInvalidCredentialsException -> InvalidCredentialsException("Передан некорректный номер телефона")
                is FirebaseTooManyRequestsException -> AuthorizationServerException()
                else -> AuthorizationException("Произошла непредвиденная ошибка при авторизации")
            }

            _onCodeSent.invoke(
                Result.failure(authorizationException)
            )
        }
    }

    private fun createPhoneNumberAuthenticationOptions(authorizationParameters: FirebasePhoneNumberAuthorizationParameters, forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null): PhoneAuthOptions {
        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseProvider)

        optionsBuilder.setActivity(authorizationParameters.activity)
                      .setPhoneNumber(authorizationParameters.phoneNumber)
                      .setTimeout(authorizationParameters.timeout, TimeUnit.SECONDS)
                      .setCallbacks(_phoneNumberAuthenticationCallbacks)

        if (forceResendingToken != null) {
            optionsBuilder.setForceResendingToken(forceResendingToken)
        }

        return optionsBuilder.build()
    }

    private fun signIn(credentials: PhoneAuthCredential) {
        firebaseProvider.signInWithCredential(credentials)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                _onCodeVerified.invoke(
                                    Result.success(Unit)
                                )
                            }
                            else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                _onCodeVerified.invoke(
                                    Result.failure(
                                        InvalidCredentialsException("Передан некорректный проверочный код")
                                    )
                                )
                            }
                        }
    }

    override fun verifyCode(code: String, onCodeVerified: (Result<Unit>) -> Unit) {
        _onCodeVerified = onCodeVerified

        if (_verificationId == null) {
            throw AuthorizationException("Невозможно выполнить авторизацию")
        }

        if (code.isBlank()) {
            throw IllegalArgumentException("Проверочный код не может быть пустым или содержать символы-разделители")
        }

        signIn(
            PhoneAuthProvider.getCredential(_verificationId!!, code)
        )
    }

    override fun sendCode(authorizationParameters: PhoneNumberAuthorizationParameters, onCodeSent: (Result<Boolean>) -> Unit) {
        if (authorizationParameters !is FirebasePhoneNumberAuthorizationParameters) {
            throw IllegalArgumentException("Для авторизации через Firebase необходимо использовать ${FirebasePhoneNumberAuthorizationParameters::class.simpleName}")
        }
        
        _onCodeSent = onCodeSent

        PhoneAuthProvider.verifyPhoneNumber(
            createPhoneNumberAuthenticationOptions(
                authorizationParameters,
                _forceResendingToken
            )
        )
    }
}