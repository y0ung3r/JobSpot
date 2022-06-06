package com.anyjob.ui.explorer.viewModels

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anyjob.domain.profile.useCases.GetAuthorizedUserUseCase
import com.anyjob.ui.explorer.profile.models.AuthorizedUser
import kotlinx.coroutines.launch

class ExplorerViewModel(
    private val getAuthorizedUserUseCase: GetAuthorizedUserUseCase
) : ViewModel() {
    private val _currentAddress = MutableLiveData<Address>()
    val currentAddress: LiveData<Address> = _currentAddress

    private val _authorizedUser = MutableLiveData<AuthorizedUser?>()

    fun updateCurrentAddress(address: Address) {
        _currentAddress.postValue(address)
    }

    fun getAuthorizedUser(): LiveData<AuthorizedUser?> {
        viewModelScope.launch {
            kotlin.runCatching {
                getAuthorizedUserUseCase.execute()
            }
            .onSuccess { user ->
                var authorizedUser: AuthorizedUser? = null

                if (user != null) {
                    authorizedUser = AuthorizedUser(
                        user.id,
                        user.lastname,
                        user.firstname,
                        user.middlename,
                        user.phoneNumber
                    )
                }

                _authorizedUser.postValue(authorizedUser)
            }
        }

        return _authorizedUser
    }
}