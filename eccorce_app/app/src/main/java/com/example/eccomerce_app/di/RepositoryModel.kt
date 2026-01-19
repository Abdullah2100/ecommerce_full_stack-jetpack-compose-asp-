package com.example.eccomerce_app.di

import com.example.eccomerce_app.data.repository.AddressRepository
import com.example.eccomerce_app.data.repository.AuthRepository
import com.example.eccomerce_app.data.repository.BannerRepository
import com.example.eccomerce_app.data.repository.CategoryRepository
import com.example.eccomerce_app.data.repository.CurrencyRepository
import com.example.eccomerce_app.data.repository.DeliveryRepository
import com.example.eccomerce_app.data.repository.GeneralSettingRepository
import com.example.eccomerce_app.data.repository.MapRepository
import com.example.eccomerce_app.data.repository.OrderItemRepository
import com.example.eccomerce_app.data.repository.OrderRepository
import com.example.eccomerce_app.data.repository.PaymentRepository
import com.example.eccomerce_app.data.repository.ProductRepository
import com.example.eccomerce_app.data.repository.StoreRepository
import com.example.eccomerce_app.data.repository.SubCategoryRepository
import com.example.eccomerce_app.data.repository.UserRepository
import com.example.eccomerce_app.data.repository.VariantRepository
import org.koin.dsl.module

val repositoryModel = module {
    single { AddressRepository(get()) }
    single { AuthRepository(get()) }
    single { BannerRepository(get()) }
    single { CategoryRepository(get()) }
    single { GeneralSettingRepository(get()) }
    single { OrderItemRepository(get()) }
    single { OrderRepository(get()) }
    single { ProductRepository(get()) }
    single { StoreRepository(get()) }
    single { SubCategoryRepository(get()) }
    single { UserRepository(get()) }
    single { VariantRepository(get()) }
    single { MapRepository(get()) }
    single { DeliveryRepository(get()) }
    single { CurrencyRepository(get()) }
    single { PaymentRepository(get()) }
}