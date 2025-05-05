package com.jobspot.ui.explorer.search.viewModels

import androidx.lifecycle.*
import com.jobspot.domain.profile.models.User
import com.jobspot.domain.search.OrderCreationParameters
import com.jobspot.domain.search.models.Order
import com.jobspot.domain.search.useCases.CancelSearchUseCase
import com.jobspot.domain.search.useCases.SearchWorkerUseCase
import com.jobspot.domain.services.models.Service
import com.jobspot.domain.services.useCases.GetServicesUseCase
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchWorkerUseCase: SearchWorkerUseCase,
    private val cancelSearchUseCase: CancelSearchUseCase,
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {
    private val _service = MutableLiveData<Service>()
    val service: LiveData<Service> = _service

    fun startWorkerSearching(orderParameters: OrderCreationParameters, onWorkerFound: (User) -> Unit): LiveData<Order> = liveData {
        emit(
            searchWorkerUseCase.execute(orderParameters, onWorkerFound)
        )
    }

    fun cancelWorkerSearching(order: Order) {
        viewModelScope.launch {
            cancelSearchUseCase.execute(order.id)
        }
    }

    fun setService(selectedService: Service) {
        _service.postValue(selectedService)
    }

    fun getServicesList(): LiveData<List<Service>> = liveData {
        emit(
            getServicesUseCase.execute()
        )
    }
}