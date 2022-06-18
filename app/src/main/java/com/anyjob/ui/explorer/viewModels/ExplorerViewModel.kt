package com.anyjob.ui.explorer.viewModels

import android.location.Address
import androidx.lifecycle.*
import com.anyjob.domain.profile.models.User
import com.anyjob.domain.profile.useCases.GetAuthorizedUserUseCase
import com.anyjob.ui.explorer.profile.models.AuthorizedUser
import kotlinx.coroutines.launch

class ExplorerViewModel(
    private val getAuthorizedUserUseCase: GetAuthorizedUserUseCase
) : ViewModel() {
    private val _currentAddress = MutableLiveData<Address>()
    val currentAddress: LiveData<Address> = _currentAddress

    private val _worker = MutableLiveData<User>()
    val worker: LiveData<User> = _worker

    fun setWorker(foundWorker: User) {
        _worker.postValue(foundWorker)
    }

    fun updateCurrentAddress(address: Address) {
        _currentAddress.postValue(address)
    }

    fun getAuthorizedUser(): LiveData<AuthorizedUser?> = liveData {
        val userSource = getAuthorizedUserUseCase.execute()
        var authorizedUser: AuthorizedUser? = null

        if (userSource != null) {
            authorizedUser = AuthorizedUser(
                userSource.id,
                userSource.lastname,
                userSource.firstname,
                userSource.middlename,
                userSource.phoneNumber
            )
        }

        emit(authorizedUser)
    }
}