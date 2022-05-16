package com.anyjob.data.authorization

import androidx.core.os.OperationCanceledException
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.data.profile.interfaces.UserDataSource
import com.anyjob.domain.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.domain.authorization.exceptions.AuthorizationCanceledException
import com.anyjob.domain.authorization.exceptions.AuthorizationException
import com.anyjob.domain.authorization.exceptions.AuthorizationServerException
import com.anyjob.domain.authorization.exceptions.InvalidCredentialsException
import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

internal class FirebasePhoneNumberAuthorizationProvider(
    private val firebaseProvider: FirebaseAuth,
    private val userDataSource: UserDataSource
) : PhoneNumberAuthorizationProvider {
    private lateinit var _authorizationParameters: PhoneNumberAuthorizationParameters
    private var _verificationId: String? = null
    private var _forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var _onCodeSent: (Result<Unit>) -> Unit

    private val _phoneNumberAuthenticationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
            // Авторизация будет происходить только при вызове метода verifyCode()
        }

        override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            _verificationId = verificationId
            _forceResendingToken = forceResendingToken

            _onCodeSent.invoke(
                Result.success(Unit)
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

    private suspend fun ensureUserCreated() {
        val firebaseUser = firebaseProvider.currentUser!!
        val userId = firebaseUser.uid
        val foundUser = userDataSource.getUser(userId)

        if (foundUser == null) {
            val userEntity = UserEntity().apply {
                id = userId
                phoneNumber = firebaseUser.phoneNumber!!
            }

            userDataSource.addUser(userEntity)
        }
    }

    private suspend fun signIn(credentials: PhoneAuthCredential) {
        try {
            firebaseProvider.signInWithCredential(credentials).await()
            ensureUserCreated()
        }

        catch (exception: Exception) {
            val authorizationException = when (exception) {
                is FirebaseAuthInvalidCredentialsException -> InvalidCredentialsException("Передан некорректный проверочный код")
                is OperationCanceledException -> AuthorizationCanceledException()
                else -> AuthorizationException("Произошла непредвиденная ошибка при авторизации")
            }

            throw authorizationException
        }
    }

    override suspend fun verifyCode(code: String) {
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

    override fun sendCode(authorizationParameters: PhoneNumberAuthorizationParameters, onCodeSent: (Result<Unit>) -> Unit) {
        if (authorizationParameters !is FirebasePhoneNumberAuthorizationParameters) {
            throw IllegalArgumentException("Для авторизации через Firebase необходимо использовать ${FirebasePhoneNumberAuthorizationParameters::class.simpleName}")
        }

        _authorizationParameters = authorizationParameters
        _onCodeSent = onCodeSent

        PhoneAuthProvider.verifyPhoneNumber(
            createPhoneNumberAuthenticationOptions(
                _authorizationParameters as FirebasePhoneNumberAuthorizationParameters,
                _forceResendingToken
            )
        )
    }

    override fun resendCode(onCodeResent: (Result<Unit>) -> Unit) {
        if (_forceResendingToken == null) {
            throw IllegalAccessException("Невозможно выполнить процедуру повторной отправки проверочного кода")
        }

        sendCode(
            _authorizationParameters,
            onCodeResent
        )
    }

    override fun signOut() {
        firebaseProvider.signOut()
    }
}