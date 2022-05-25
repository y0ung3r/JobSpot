package com.anyjob.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anyjob.domain.authorization.ProfileCreationParameters
import com.anyjob.domain.authorization.useCases.CreateProfileUseCase
import kotlinx.coroutines.launch

class ProfileCreationViewModel(private val createProfileUseCase: CreateProfileUseCase) : ViewModel() {
    private val _isLastnameFilled = MutableLiveData<Boolean>()
    val isLastnameFilled: LiveData<Boolean> = _isLastnameFilled

    private val _isFirstnameFilled = MutableLiveData<Boolean>()
    val isFirstnameFilled: LiveData<Boolean> = _isFirstnameFilled

    private val _isDataValid = MutableLiveData<Boolean>()
    val isFieldsValidated: LiveData<Boolean> = _isDataValid

    private val _onProfileCreating = MutableLiveData<Result<Unit>>()
    val onProfileCreating: LiveData<Result<Unit>> = _onProfileCreating

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

    fun createProfile(parameters: ProfileCreationParameters) {
        viewModelScope.launch {
            kotlin.runCatching {
                createProfileUseCase.execute(parameters)
            }
            .onSuccess {
                _onProfileCreating.postValue(
                    Result.success(Unit)
                )
            }
            .onFailure { exception ->
                _onProfileCreating.postValue(
                    Result.failure(exception)
                )
            }
        }
    }
}