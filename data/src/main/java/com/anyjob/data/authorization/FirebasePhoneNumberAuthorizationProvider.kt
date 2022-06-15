package com.anyjob.data.authorization

import androidx.core.os.OperationCanceledException
import com.anyjob.data.FirebaseContext
import com.anyjob.data.extensions.get
import com.anyjob.data.extensions.save
import com.anyjob.data.profile.entities.UserEntity
import com.anyjob.domain.authorization.PhoneNumberAuthorizationParameters
import com.anyjob.domain.authorization.exceptions.AuthorizationCanceledException
import com.anyjob.domain.authorization.exceptions.AuthorizationException
import com.anyjob.domain.authorization.exceptions.AuthorizationServerException
import com.anyjob.domain.authorization.exceptions.InvalidCredentialsException
import com.anyjob.domain.authorization.interfaces.PhoneNumberAuthorizationProvider
import com.anyjob.domain.profile.models.User
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

internal class FirebasePhoneNumberAuthorizationProvider(
    private val firebaseProvider: FirebaseAuth,
    private val context: FirebaseContext
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

    private fun createPhoneNumberAuthorizationOptions(authorizationParameters: FirebasePhoneNumberAuthorizationParameters, forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null): PhoneAuthOptions {
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
        val authorizedUser = firebaseProvider.currentUser!!
        val userId = authorizedUser.uid
        val storeUser = context.users.get<UserEntity>(userId)

        if (storeUser == null) {
            val userEntity = UserEntity().apply {
                id = userId
                phoneNumber = authorizedUser.phoneNumber!!
            }

            context.users.save(userId, userEntity)
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
            val exception = IllegalArgumentException("Для авторизации через Firebase необходимо использовать ${FirebasePhoneNumberAuthorizationParameters::class.simpleName}")

            onCodeSent.invoke(
                Result.failure(exception)
            )

            return
        }

        _authorizationParameters = authorizationParameters
        _onCodeSent = onCodeSent

        PhoneAuthProvider.verifyPhoneNumber(
            createPhoneNumberAuthorizationOptions(
                _authorizationParameters as FirebasePhoneNumberAuthorizationParameters,
                _forceResendingToken
            )
        )
    }

    override fun resendCode(onCodeResent: (Result<Unit>) -> Unit) {
        if (_forceResendingToken == null) {
            val exception = IllegalAccessException("Невозможно выполнить процедуру повторной отправки проверочного кода")

            onCodeResent.invoke(
                Result.failure(exception)
            )

            return
        }

        sendCode(
            _authorizationParameters,
            onCodeResent
        )
    }

    override suspend fun getAuthorizedUser(): User? {
        val authorizedUser = firebaseProvider.currentUser ?: return null
        val userId = authorizedUser.uid
        val storeUser = context.users.get<UserEntity>(userId) ?: return null

        return User(
            id = userId,
            phoneNumber = storeUser.phoneNumber!!,
            lastname = storeUser.lastname,
            firstname = storeUser.firstname,
            middlename = storeUser.middlename,
            isWorker = storeUser.isWorker,
            address = storeUser.address
        )
    }

    override fun signOut() {
        firebaseProvider.signOut()
    }
}