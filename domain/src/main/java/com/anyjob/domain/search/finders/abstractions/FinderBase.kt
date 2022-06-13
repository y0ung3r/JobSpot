package com.anyjob.domain.search.finders.abstractions

abstract class FinderBase {
    private var _stopped: Boolean = true

    fun start() {
        _stopped = false

        while (!_stopped) {
            onFind()
        }
    }

    fun stop() {
        _stopped = true
    }

    protected abstract fun onFind()
}