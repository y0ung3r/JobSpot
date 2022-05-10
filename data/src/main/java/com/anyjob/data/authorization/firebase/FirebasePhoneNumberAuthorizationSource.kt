package com.anyjob.data.authorization.firebase

import com.anyjob.data.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.data.authorization.exceptions.AuthorizationException
import com.anyjob.data.authorization.exceptions.AuthorizationServerException
import com.anyjob.data.authorization.exceptions.InvalidCredentialsException
import com.anyjob.data.authorization.interfaces.PhoneNumberAuthorizationSource
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class FirebasePhoneNumberAuthorizationSource(private val firebaseProvider: FirebaseAuth) : PhoneNumberAuthorizationSource {
    private var _verificationId: String? = null
    private var _forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var _onCodeSendingStateListener: PhoneNumberAuthorizationSource.OnCodeSendingStateListener? = null

    private val _phoneNumberAuthenticationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
            // Авторизация будет происходить только при вызове метода verifyCode()
        }

        override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            _verificationId = verificationId
            _forceResendingToken = forceResendingToken

            _onCodeSendingStateListener?.onSuccess()
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            val authorizationException = when (exception) {
                is FirebaseAuthInvalidCredentialsException -> InvalidCredentialsException("Неправильно указан номер телефона")
                is FirebaseTooManyRequestsException -> AuthorizationServerException()
                else -> AuthorizationException("Произошла непредвиденная ошибка при авторизации")
            }

            _onCodeSendingStateListener?.onFailed(authorizationException as AuthorizationException)
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
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                throw InvalidCredentialsException("Неправильно указан проверочный код")
                            }
                        }
    }

    override fun verifyCode(code: String) {
        if (_verificationId == null) {
            throw AuthorizationException("Невозможно выполнить авторизацию. Попробуйте снова")
        }

        if (code.isBlank()) {
            throw IllegalArgumentException("Проверочный код не может быть пустым или содержать символы-разделители")
        }

        signIn(
            PhoneAuthProvider.getCredential(_verificationId!!, code)
        )
    }

    override fun sendCode(authorizationParameters: PhoneNumberAuthorizationParameters) {
        if (authorizationParameters !is FirebasePhoneNumberAuthorizationParameters) {
            throw IllegalArgumentException("Для авторизации через Firebase необходимо использовать ${FirebasePhoneNumberAuthorizationParameters::class.simpleName}")
        }

        PhoneAuthProvider.verifyPhoneNumber(
            createPhoneNumberAuthenticationOptions(
                authorizationParameters,
                _forceResendingToken
            )
        )
    }

    override fun setOnCodeSendingStateListener(listener: PhoneNumberAuthorizationSource.OnCodeSendingStateListener): PhoneNumberAuthorizationSource {
        _onCodeSendingStateListener = listener

        return this@FirebasePhoneNumberAuthorizationSource
    }
}