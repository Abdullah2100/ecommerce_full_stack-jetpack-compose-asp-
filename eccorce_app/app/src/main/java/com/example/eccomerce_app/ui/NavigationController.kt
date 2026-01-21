package com.example.eccomerce_app.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.eccomerce_app.viewModel.CartViewModel
import com.example.eccomerce_app.ui.view.OnBoarding.OnBoardingScreen
import com.example.eccomerce_app.ui.view.Auth.LoginScreen
import com.example.eccomerce_app.ui.view.Auth.SignUpPage
import com.example.eccomerce_app.ui.view.address.EditOrAddLocationScreen
import com.example.eccomerce_app.ui.view.account.AccountPage
import com.example.eccomerce_app.ui.view.account.ProfileScreen
import com.example.eccomerce_app.ui.view.account.store.CreateProductScreen
import com.example.eccomerce_app.ui.view.account.store.ProductDetail
import com.example.eccomerce_app.ui.view.account.store.StoreScreen
import com.example.eccomerce_app.ui.view.checkout.CheckoutScreen
import com.example.eccomerce_app.ui.view.home.CartScreen
import com.example.eccomerce_app.ui.view.home.HomePage
import com.example.eccomerce_app.ui.view.home.OrderScreen
import com.example.eccomerce_app.ui.view.home.ProductCategoryScreen
import com.example.eccomerce_app.ui.view.address.AddressHomeScreen
import com.example.eccomerce_app.ui.view.address.PickCurrentAddressFromAddressScreen
import com.example.eccomerce_app.ui.view.ReseatPassword.GenerateOtpScreen
import com.example.eccomerce_app.ui.view.ReseatPassword.OtpVerificationScreen
import com.example.eccomerce_app.ui.view.ReseatPassword.ReseatPasswordScreen
import com.example.eccomerce_app.ui.view.account.OrderForMyStoreScreen
import com.example.eccomerce_app.ui.view.account.store.delivery.DeliveriesListScreen
import com.example.eccomerce_app.ui.view.home.CategoryScreen
import com.example.eccomerce_app.ui.view.address.MapHomeScreen
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
import com.example.eccomerce_app.viewModel.PaymentTypeViewModel
import com.example.eccomerce_app.viewModel.PaymentViewModel
import com.example.eccomerce_app.viewModel.UserViewModel


@Composable
fun NavController(
    nav: NavHostController,
    authViewModel: AuthViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel(),
    bannerViewModel: BannerViewModel = koinViewModel(),
    categoryViewModel: CategoryViewModel = koinViewModel(),
    subCategoryViewModel: SubCategoryViewModel = koinViewModel(),
    variantViewModel: VariantViewModel = koinViewModel(),
    storeViewModel: StoreViewModel = koinViewModel(),
    productViewModel: ProductViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    generalSettingViewModel: GeneralSettingViewModel = koinViewModel(),
    orderViewModel: OrderViewModel = koinViewModel(),
    orderItemViewModel: OrderItemsViewModel = koinViewModel(),
    mapViewModel: MapViewModel = koinViewModel(),
    deliveryViewModel: DeliveryViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    currencyViewModel: CurrencyViewModel = koinViewModel(),
    paymentViewModel:   PaymentViewModel= koinViewModel(),
    paymentTypeViewModel: PaymentTypeViewModel= koinViewModel(),
    currentScreen: Int,
) {

    NavHost(
        startDestination = when (currentScreen) {
            1 -> Screens.OnBoarding
            2 -> Screens.AuthGraph
            3 -> Screens.LocationGraph
            else -> Screens.HomeGraph
        }, navController = nav
    ) {

        composable<Screens.MapScreen>(
            enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
                ) + fadeIn(tween(200))
            },

            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(300)
                ) + fadeIn(tween(200))

            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(300)
                ) + fadeOut(tween(200))

            }) { result ->
            val data = result.toRoute<Screens.MapScreen>()

            MapHomeScreen(
                longitude = data.lognit,
                latitude = data.latitt,
                additionLong = data.additionLong,
                additionLat = data.additionLat,
                title = data.title,
                id = data.id,
                isFomLogin = data.isFromLogin,
                mapType = data.mapType,
                nav = nav,
                userViewModel = userViewModel,
                storeViewModel = storeViewModel,
                mapViewModel = mapViewModel,
                cartViewModel = cartViewModel
            )

        }



        composable<Screens.OnBoarding>(
            enterTransition = {
                return@composable fadeIn(tween(2000))
            },
            exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
                )
            },

            ) {
            OnBoardingScreen(nav = nav, userViewModel = userViewModel)
        }



        navigation<Screens.LocationGraph>(
            startDestination = Screens.LocationHome
        )
        {

            composable<Screens.LocationHome>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                },

                popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }) {
                AddressHomeScreen(
                    nav = nav, userViewModel = userViewModel
                )
            }

            composable<Screens.PickCurrentAddress>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },

                popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }) { value ->
                PickCurrentAddressFromAddressScreen(
                    nav = nav,
                    userViewModel = userViewModel,
                    orderViewModel = orderViewModel,
                    generalSettingViewModel = generalSettingViewModel,
                    bannerViewModel = bannerViewModel,
                    productViewModel = productViewModel,
                    variantViewModel = variantViewModel,
                    categoryViewModel = categoryViewModel,
                )
            }

        }


        navigation<Screens.ReseatPasswordGraph>(
            startDestination = Screens.GenerateOtp
        ) {

            composable<Screens.GenerateOtp>(enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }) {
                GenerateOtpScreen(
                    nav = nav, authViewModel
                )
            }
            composable<Screens.OtpVerification>(enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }) { result ->
                val data = result.toRoute<Screens.OtpVerification>()
                OtpVerificationScreen(
                    nav = nav, authViewModel, email = data.email
                )
            }

            composable<Screens.ReseatPassword>(enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }) { result ->
                val data = result.toRoute<Screens.ReseatPassword>()
                ReseatPasswordScreen(
                    nav = nav, authViewModel, email = data.email, otp = data.otp
                )
            }

        }

        navigation<Screens.AuthGraph>(
            startDestination = Screens.Login
        ) {

            composable<Screens.Login>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                },

                popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }) {
                LoginScreen(
                    nav = nav, authKoin = authViewModel
                )
            }

            composable<Screens.Signup>(
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                },

                popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                },
            ) {
                SignUpPage(
                    nav = nav, authKoin = authViewModel
                )
            }
        }

        navigation<Screens.HomeGraph>(
            startDestination = Screens.Home
        ) {

            composable<Screens.Home>(enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }) {
                HomePage(
                    nav = nav,
                    bannerViewModel = bannerViewModel,
                    categoryViewModel = categoryViewModel,
                    variantViewModel = variantViewModel,
                    productViewModel = productViewModel,
                    userViewModel = userViewModel,
                    generalSettingViewModel = generalSettingViewModel,
                    orderViewModel = orderViewModel,
                    currencyViewModel = currencyViewModel,
                    homeViewModel = homeViewModel,
                    paymentTypeViewModel = paymentTypeViewModel
                )
            }

            composable<Screens.Category>(enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }) {
                CategoryScreen(
                    nav = nav,
                    categoryViewModel = categoryViewModel,
                    productViewModel = productViewModel
                )
            }

            composable<Screens.ProductCategory>(enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }) { result ->
                val data = result.toRoute<Screens.ProductCategory>()
                ProductCategoryScreen(
                    nav = nav,
                    categoryId = data.categoryId,
                    categoryViewModel = categoryViewModel,
                    productViewModel = productViewModel
                )
            }



            composable<Screens.Account>(enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }) {
                AccountPage(
                    nav = nav,
                    userViewModel = userViewModel,
                    orderItemsViewModel = orderItemViewModel,
                    authViewModel = authViewModel,
                    productViewModel = productViewModel,
                    currencyViewModel = currencyViewModel

                )
            }


            composable<Screens.Profile>(enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                )
            }, exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                )
            }) {
                ProfileScreen(
                    nav = nav, userViewModel = userViewModel
                )
            }

            composable<Screens.Store>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) { navRef ->
                val storeId = navRef.toRoute<Screens.Store>()
                StoreScreen(
                    copyStoreId = storeId.storeId,
                    isFromHome = storeId.isFromHome,
                    nav = nav,
                    bannerViewModel = bannerViewModel,
                    categoryViewModel = categoryViewModel,
                    subCategoryViewModel = subCategoryViewModel,
                    storeViewModel = storeViewModel,
                    productViewModel = productViewModel,
                    userViewModel = userViewModel,
                )
            }

            composable<Screens.DeliveriesList>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) { navRef ->
                DeliveriesListScreen(
                    nav = nav,
                    deliveryViewModel = deliveryViewModel
                )
            }
            composable<Screens.CreateProduct>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) { navRef ->

                val store = navRef.toRoute<Screens.CreateProduct>()

                CreateProductScreen(
                    nav = nav,
                    storeId = store.storeId,
                    productId = store.productId,
                    subCategoryViewModel = subCategoryViewModel,
                    variantViewModel = variantViewModel,
                    productViewModel = productViewModel,
                    currencyViewModel = currencyViewModel
                )

            }

            composable<Screens.ProductDetails>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) { navRef ->

                val store = navRef.toRoute<Screens.ProductDetails>()

                ProductDetail(
                    nav = nav,
                    cartViewModel = cartViewModel,
                    productID = store.productId,
                    isFromHome = store.isFromHome,
                    variantViewModel = variantViewModel,
                    storeViewModel = storeViewModel,
                    bannerViewModel = bannerViewModel,
                    subCategoryViewModel = subCategoryViewModel,
                    productViewModel = productViewModel,
                    isCanNavigateToStore = store.isCanNavigateToStore,
                    userViewModel = userViewModel,
                    currencyViewModel = currencyViewModel
                )

            }

            composable<Screens.Cart>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) { navRef ->


                CartScreen(
                    nav = nav,
                    cartViewModel = cartViewModel,
                    variantViewModel = variantViewModel,
                    userViewModel = userViewModel,
                    storeViewModel = storeViewModel
                )

            }

            composable<Screens.Checkout>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) { navRef ->


                CheckoutScreen(
                    nav = nav,
                    cartViewModel = cartViewModel,
                    userViewModel = userViewModel,
                    generalSettingViewModel = generalSettingViewModel,
                    orderViewModel = orderViewModel,
                    paymentViewModel = paymentViewModel,
                    paymentTypeViewModel = paymentTypeViewModel
                )

            }


            composable<Screens.EditeOrAddNewAddress>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) {
                EditOrAddLocationScreen(
                    nav = nav,
                    userViewModel = userViewModel,
                    cartViewModel = cartViewModel,
                    storeViewModel = storeViewModel

                )

            }

            composable<Screens.Order>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) {
                OrderScreen(orderViewModel = orderViewModel)

            }

            composable<Screens.OrderForMyStore>(

                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(750)
                    )
                }, exitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(750)
                    )
                }) {
                OrderForMyStoreScreen(
                    nav = nav,
                    orderItemsViewModel = orderItemViewModel
                )

            }


        }

    }

}