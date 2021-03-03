package com.takhir.openapiapp.ui

interface DataStateChangeListener {

  fun onDataStateChange(dataState: DataState<*>?)
}