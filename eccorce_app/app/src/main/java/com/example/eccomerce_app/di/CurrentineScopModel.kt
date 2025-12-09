package com.example.eccomerce_app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

fun coroutineAppScop (): CoroutineScope {
    return CoroutineScope(Dispatchers.IO+ SupervisorJob())
}

val coroutineScopModel= module{
    single {coroutineAppScop() }
}