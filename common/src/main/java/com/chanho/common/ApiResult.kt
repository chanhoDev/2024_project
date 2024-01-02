package com.chanho.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

sealed class ApiResult<out T> {
    object Loading : ApiResult<Nothing>()

    object Empty: ApiResult<Nothing>()

    data class Success<out T>(val data: T) : ApiResult<T>()

    sealed class Fail: ApiResult<Nothing>() {
        data class Error(val  code: Int,val message: String?) : Fail()
        data class Exception(val e:Throwable) : Fail()
    }

    fun <T> safeFlow(apiFunc: suspend () -> T): Flow<ApiResult<T>> = flow {
        emit(Loading)
        try {
            emit(Success(apiFunc.invoke()))
        } catch (e: NullPointerException) {
            emit(Empty)
        } catch (e: HttpException) {
            emit(Fail.Error(code = e.code(), message = e.message()))
        } catch (e: Exception) {
            emit(Fail.Exception(e = e))
        }
    }
}