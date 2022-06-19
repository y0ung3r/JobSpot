package com.anyjob.ui.authorization.viewModels

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.domain.authorization.useCases.CreateProfileUseCase
import com.anyjob.domain.profile.models.MapsAddress
import kotlinx.coroutines.launch

class ProfileCreationViewModel(
    private val createProfileUseCase: CreateProfileUseCase
) : ViewModel() {
    private val _isLastnameFilled = MutableLiveData<Boolean>()
    val isLastnameFilled: LiveData<Boolean> = _isLastnameFilled

    private val _isFirstnameFilled = MutableLiveData<Boolean>()
    val isFirstnameFilled: LiveData<Boolean> = _isFirstnameFilled

    private val _isAddressFilled = MutableLiveData<Boolean>()
    val isAddressFilled: LiveData<Boolean> = _isAddressFilled

    private val _onProfileCreated = MutableLiveData<Result<Unit>>()
    val onProfileCreated: LiveData<Result<Unit>> = _onProfileCreated

    private val _homeAddress = MutableLiveData<MapsAddress>()
    val homeAddress: LiveData<MapsAddress> = _homeAddress

    fun validateLastname(lastname: String) {
        _isLastnameFilled.postValue(
            lastname.isNotBlank()
        )
    }

    fun validateFirstname(firstname: String) {
        _isFirstnameFilled.postValue(
            firstname.isNotBlank()
        )
    }

    fun validateAddress(address: Address) {
        _isAddressFilled.postValue(
            address.hasLatitude() && address.hasLongitude()
        )
    }

    fun selectAddress(address: Address) {
        _homeAddress.postValue(
            MapsAddress(
                address.latitude,
                address.longitude
            )
        )
    }

    fun createProfile(parameters: ProfileCreationParameters) {
        viewModelScope.launch {
            kotlin.runCatching {
                createProfileUseCase.execute(parameters)
            }
            .onSuccess {
                _onProfileCreated.postValue(
                    Result.success(Unit)
                )
            }
            .onFailure { exception ->
                _onProfileCreated.postValue(
                    Result.failure(exception)
                )
            }
        }
    }
}