package com.sanctra.util

sealed class R<out T> {
  data class Ok<T>(val value: T) : R<T>()
  data class Err(val error: Throwable) : R<Nothing>()
}
