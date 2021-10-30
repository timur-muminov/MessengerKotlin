package com.messengerkotlin.core

sealed class LoadingState<out DATA, out ERROR> {
    class Loading() : LoadingState<Nothing, Nothing>()
    class Loaded<out DATA>(val data: DATA) : LoadingState<DATA, Nothing>()
    class Error<out ERROR>(val error: ERROR): LoadingState<Nothing, ERROR>()
}