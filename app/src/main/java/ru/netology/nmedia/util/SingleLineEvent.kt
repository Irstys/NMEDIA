package ru.netology.nmedia.util

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private var pending = false

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        require(!hasActiveObservers()) {
            error("Multiple observers registered but only one will be notified of changes.")
        }
        super.observe(owner) {
            if (pending) {
                pending = false
                observer.onChanged(it)
            }
        }
    }

    override fun setValue(t: T?) {
        pending = true
        super.setValue(t)
    }


/**
 * Used for cases where T is Void, to make calls cleaner.
 */
@MainThread
fun call() {
    value = null
}

companion object {
    private const val TAG = "SingleLiveEvent"
}
}