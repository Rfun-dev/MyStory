package com.example.mystory.util

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment(){
        countIdlingResource.increment()
    }

    fun decrement(){
        countIdlingResource.decrement()
    }

}