package com.example.eccomerce_app.di

import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.viewModel.CartViewModel
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.StoreViewModel
import com.example.eccomerce_app.viewModel.SubCategoryViewModel
import com.example.eccomerce_app.viewModel.VariantViewModel
import com.example.eccomerce_app.viewModel.BannerViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import com.example.eccomerce_app.viewModel.CurrencyViewModel
import com.example.eccomerce_app.viewModel.DeliveryViewModel
import com.example.eccomerce_app.viewModel.GeneralSettingViewModel
import com.example.eccomerce_app.viewModel.HomeViewModel
import com.example.eccomerce_app.viewModel.MapViewModel
import com.example.eccomerce_app.viewModel.OrderItemsViewModel
import com.example.eccomerce_app.viewModel.OrderViewModel
import com.example.eccomerce_app.viewModel.PaymentViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModel = module {
    single { AuthViewModel(get(), get(),get()) }
    single { BannerViewModel(get(), get(named("bannerHub"))) }
    single { CartViewModel() }
    single { CategoryViewModel(get()) }
    single { SubCategoryViewModel(get()) }
    single { VariantViewModel(get()) }
    single { StoreViewModel(get(), get(named("storeHub"))) }
    single { ProductViewModel(get(),get(),get()) }
    single { UserViewModel(get(), get(), get(),get()) }
    single { GeneralSettingViewModel(get()) }
    single { OrderViewModel(get(), get(named("orderHub"))) }
    single { OrderItemsViewModel(get(), get(named("orderItemHub")),get(named("orderHub"))) }
    single { MapViewModel(get()) }
    single { DeliveryViewModel(get()) }
    single { CurrencyViewModel(get(),get(),get()) }
    single { HomeViewModel() }
    single { PaymentViewModel(get()) }
}