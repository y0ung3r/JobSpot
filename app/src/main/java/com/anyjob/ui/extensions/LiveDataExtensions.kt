package com.anyjob.ui.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <TObservable> LiveData<TObservable>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<TObservable>) {
    observe(
        lifecycleOwner,
        object : Observer<TObservable> {
            override fun onChanged(observable: TObservable?) {
                observer.onChanged(observable)
                removeObserver(this)
            }
        }
    )
}