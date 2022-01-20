package com.wmazoni.pontointeligente.response

import kotlin.collections.ArrayList

data class Response<T>(val errors: ArrayList<String> = arrayListOf(), var data: T? = null) {
}