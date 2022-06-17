package com.anyjob.ui.explorer.search.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anyjob.domain.profile.models.User
import com.anyjob.domain.search.OrderCreationParameters
import com.anyjob.domain.search.useCases.CancelSearchUseCase
import com.anyjob.domain.search.useCases.SearchWorkerUseCase
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchWorkerUseCase: SearchWorkerUseCase,
    private val cancelSearchUseCase: CancelSearchUseCase
) : ViewModel() {
    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String> = _orderId

    fun startWorkerSearching(orderParameters: OrderCreationParameters, onWorkerFound: (User) -> Unit) {
        viewModelScope.launch {
            _orderId.postValue(
                searchWorkerUseCase.execute(orderParameters, onWorkerFound)
            )
        }
    }

    fun cancelWorkerSearching(orderId: String) {
        viewModelScope.launch {
            cancelSearchUseCase.execute(orderId)
        }
    }
}