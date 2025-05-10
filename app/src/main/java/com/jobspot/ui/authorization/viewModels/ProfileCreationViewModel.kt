package com.jobspot.ui.authorization.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.jobspot.domain.authorization.ProfileCreationParameters
import com.jobspot.domain.authorization.useCases.AddWorkerFilesUseCase
import com.jobspot.domain.authorization.useCases.CreateProfileUseCase
import com.jobspot.domain.profile.models.MapAddress
import com.jobspot.domain.services.models.Service
import com.jobspot.domain.services.useCases.GetServicesUseCase
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.search.ToponymObjectMetadata
import kotlinx.coroutines.launch
import java.io.InputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ProfileCreationViewModel(
    private val createProfileUseCase: CreateProfileUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val addWorkerFilesUseCase: AddWorkerFilesUseCase
) : ViewModel() {
    private val _isLastnameFilled = MutableLiveData<Boolean>()
    val isLastnameFilled: LiveData<Boolean> = _isLastnameFilled

    private val _isFirstnameFilled = MutableLiveData<Boolean>()
    val isFirstnameFilled: LiveData<Boolean> = _isFirstnameFilled

    private val _isAddressFilled = MutableLiveData<Boolean>()
    val isAddressFilled: LiveData<Boolean> = _isAddressFilled

    private val _isProfessionFilled = MutableLiveData<Boolean>()
    val isProfessionFilled: LiveData<Boolean> = _isProfessionFilled

    private val _isInnFilled = MutableLiveData<Boolean>()
    val isInnFilled: LiveData<Boolean> = _isInnFilled

    private val _isDiplomaFilled = MutableLiveData<Boolean>()
    val isDiplomaFilled: LiveData<Boolean> = _isDiplomaFilled

    private val _isEmploymentHistoryBookFilled = MutableLiveData<Boolean>()
    val isEmploymentHistoryBookFilled: LiveData<Boolean> = _isEmploymentHistoryBookFilled

    private val _onProfileCreated = MutableLiveData<Result<Unit>>()
    val onProfileCreated: LiveData<Result<Unit>> = _onProfileCreated

    private val _onWorkerFilesAdded = MutableLiveData<Result<Unit>>()
    val onWorkerFilesAdded: LiveData<Result<Unit>> = _onWorkerFilesAdded

    private val _homeAddress = MutableLiveData<MapAddress>()
    val homeAddress: LiveData<MapAddress> = _homeAddress

    private val _professionId = MutableLiveData<String?>(null)
    val professionId: LiveData<String?> = _professionId

    private val _encodedInn = MutableLiveData<String?>(null)
    val encodedInn: LiveData<String?> = _encodedInn

    private val _encodedDiploma = MutableLiveData<String?>(null)
    val encodedDiploma: LiveData<String?> = _encodedDiploma

    private val _encodedEmploymentHistoryBook = MutableLiveData<String?>(null)
    val encodedEmploymentHistoryBook: LiveData<String?> = _encodedEmploymentHistoryBook

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

    fun validateAddress(geoObject: GeoObject) {
        val toponym = geoObject.metadataContainer.getItem(ToponymObjectMetadata::class.java)
        _isAddressFilled.postValue(toponym?.balloonPoint != null)
    }

    fun selectAddress(geoObject: GeoObject) {
        val position = geoObject.metadataContainer.getItem(ToponymObjectMetadata::class.java)?.balloonPoint
            ?: return

        _homeAddress.postValue(
            MapAddress(
                position.latitude,
                position.longitude
            )
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun selectInn(stream: InputStream?) {
        _encodedInn.postValue(stream?.let { Base64.encode(it.readBytes()) })
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun selectDiploma(stream: InputStream?) {
        _encodedDiploma.postValue(stream?.let { Base64.encode(it.readBytes()) })
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun selectEmploymentHistoryBook(stream: InputStream?) {
        _encodedEmploymentHistoryBook.postValue(stream?.let { Base64.encode(it.readBytes()) })
    }

    fun validateInn(fileName: String?) {
        _isInnFilled.postValue(fileName != null && fileName.contains("pdf"))
    }

    fun validateDiploma(fileName: String?) {
        _isDiplomaFilled.postValue(fileName != null && fileName.contains("pdf"))
    }

    fun validateEmploymentHistoryBook(fileName: String?) {
        _isEmploymentHistoryBookFilled.postValue(fileName != null && fileName.contains("pdf"))
    }

    fun selectProfession(professionId: String?) {
        _professionId.postValue(professionId)
    }

    fun validateProfession(professionId: String?) {
        _isProfessionFilled.postValue(professionId != null)
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

    fun addWorkerFiles(userId: String, encodedInn: String?, encodedDiploma: String?, encodedEmploymentHistoryBook: String?) {
        viewModelScope.launch {
            kotlin.runCatching {
                addWorkerFilesUseCase.execute(userId, encodedInn, encodedDiploma, encodedEmploymentHistoryBook)
            }
            .onSuccess {
                _onWorkerFilesAdded.postValue(
                    Result.success(Unit)
                )
            }
            .onFailure { exception ->
                _onWorkerFilesAdded.postValue(
                    Result.failure(exception)
                )
            }
        }
    }

    fun getServicesList(): LiveData<List<Service>> = liveData {
        emit(
            getServicesUseCase.execute()
        )
    }
}