package com.jobspot.ui.explorer.viewModels

import androidx.lifecycle.*
import com.jobspot.domain.profile.models.User
import com.jobspot.domain.profile.useCases.AddRateToUserUseCase
import com.jobspot.domain.profile.useCases.GetAuthorizedUserUseCase
import com.jobspot.domain.profile.useCases.LogoutUseCase
import com.jobspot.domain.profile.useCases.StartVerificationListenerUseCase
import com.jobspot.domain.search.models.Order
import com.jobspot.domain.search.useCases.*
import com.jobspot.ui.explorer.profile.models.AuthorizedUser
import com.yandex.mapkit.GeoObject
import kotlinx.coroutines.launch

class ExplorerViewModel(
    private val getAuthorizedUserUseCase: GetAuthorizedUserUseCase,
    private val searchClientUseCase: SearchClientUseCase,
    private val acceptJobUseCase: AcceptJobUseCase,
    private val getExecutedOrderUseCase: GetExecutedOrderUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val getOrderExecutorUseCase: GetOrderExecutorUseCase,
    private val getOrderInvokerUseCase: GetOrderInvokerUseCase,
    private val checkOrderStateUseCase: CheckOrderStateUseCase,
    private val startVerificationListenerUseCase: StartVerificationListenerUseCase,
    private val finishOrderUseCase: FinishOrderUseCase,
    private val addRateToUserUseCase: AddRateToUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _currentGeoObject = MutableLiveData<GeoObject>()
    val currentGeoObject: LiveData<GeoObject> = _currentGeoObject

    private val _worker = MutableLiveData<User>()
    val worker: LiveData<User> = _worker

    private val _client = MutableLiveData<User>()
    val client: LiveData<User> = _client

    private val _order = MutableLiveData<Order>()
    val order: LiveData<Order> = _order

    fun setWorker(foundWorker: User) {
        _worker.postValue(foundWorker)
    }

    fun setClient(foundOrder: Order) {
        viewModelScope.launch {
            _client.postValue(
                getOrderInvokerUseCase.execute(foundOrder)
            )
        }
    }

    fun setOrder(foundOrder: Order) {
        _order.postValue(foundOrder)
    }

    fun startOrderChecker(order: Order, onOrderChanged: (isFinished: Boolean, isCanceled: Boolean) -> Unit) {
        viewModelScope.launch {
            checkOrderStateUseCase.execute(order.id, onOrderChanged)
        }
    }

    fun startVerificationListener(onVerified: (User) -> Unit) {
        viewModelScope.launch {
            startVerificationListenerUseCase.execute(onVerified)
        }
    }

    fun startClientSearching(onClientFound: (Order) -> Unit) {
        viewModelScope.launch {
            searchClientUseCase.execute(onClientFound)
        }
    }

    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            cancelOrderUseCase.execute(order.id)
        }
    }

    fun addRateToUser(user: User, rate: Float) {
        viewModelScope.launch {
            addRateToUserUseCase.execute(user.id, rate)
        }
    }

    fun finishOrder(order: Order) {
        viewModelScope.launch {
            finishOrderUseCase.execute(order.id)
        }
    }

    fun acceptJob(order: Order) {
        viewModelScope.launch {
            acceptJobUseCase.execute(order)
        }
    }

    fun updateCurrentAddress(geoObject: GeoObject) {
        _currentGeoObject.postValue(geoObject)
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase.execute()
        }
    }

    fun getAuthorizedUser(): LiveData<AuthorizedUser?> = liveData {
        val userSource = getAuthorizedUserUseCase.execute()
        val currentOrder = getExecutedOrderUseCase.execute()
        var authorizedUser: AuthorizedUser? = null

        if (currentOrder != null) {
            setClient(currentOrder)
            setOrder(currentOrder)

            val orderExecutor = getOrderExecutorUseCase.execute(currentOrder)

            if (orderExecutor != null) {
                setWorker(orderExecutor)
            }
        }

        if (userSource != null) {
            authorizedUser = AuthorizedUser(
                userSource.id,
                userSource.lastname,
                userSource.firstname,
                userSource.middlename,
                userSource.phoneNumber,
                userSource.isWorker,
                currentOrder,
                userSource.isDocumentsVerified,
                userSource.averageRate
            )
        }

        emit(authorizedUser)
    }
}