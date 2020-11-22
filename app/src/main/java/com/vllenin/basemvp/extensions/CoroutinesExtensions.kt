package com.vllenin.basemvp.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

fun CoroutineScope.interval(timeIntervalMs: Long) : Flow<Long> = flow {
  while (isActive) {
    emit(0L)
    delay(timeIntervalMs)
  }
}

