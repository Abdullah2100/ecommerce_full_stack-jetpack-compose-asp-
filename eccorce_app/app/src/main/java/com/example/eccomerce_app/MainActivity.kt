package com.example.eccomerce_app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.e_commercompose.R
import com.example.e_commercompose.model.ButtonNavItem
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.NavController
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.currentLocal
import com.example.eccomerce_app.util.General.whenLanguageUpdateDo
import com.example.eccomerce_app.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin
import java.util.Locale

class MainActivity : ComponentActivity() {
    var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authViewModel: AuthViewModel = getKoin().get()

       lifecycleScope.launch{
           currentLocal.collect { value->
               whenLanguageUpdateDo(value?:"ar",this@MainActivity)
           }
       }





        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplash
            }
        }

        enableEdgeToEdge()
        setContent {


            val nav = rememberNavController()
            val currentScreen = authViewModel.currentScreen.collectAsState()
            val currentLocale = currentLocal.collectAsState()
            val updateDirection = remember {
                derivedStateOf {
                    if (currentLocale.value == "ar") {
                        LayoutDirection.Rtl

                    } else {
                        LayoutDirection.Ltr
                    }
                }
            }




            if (currentScreen.value != null) {
                keepSplash = false
            }

            if (currentScreen.value != null)

                CompositionLocalProvider(
                    LocalLayoutDirection provides updateDirection.value
                ) {
                Scaffold(
                    bottomBar = {
                        ApplicationBottonNav(nav)
                    }
                )
                {
                    it.calculateTopPadding()
                    it.calculateBottomPadding()
                    NavController(nav, currentScreen = currentScreen.value ?: 1)
                }
                }
        }
    }


    override fun onDestroy() {
//        connection?.stop()
        super.onDestroy()
    }
}

@Composable
fun ApplicationBottonNav(nav: NavHostController){
    val buttonNavItemHolder = listOf(
        ButtonNavItem(
            name = R.string.home,
            imageId = Icons.Outlined.Home,
            0
        ),
        ButtonNavItem(
            name = R.string.order,
            imageId = ImageVector.vectorResource(R.drawable.order),
            1
        ),
        ButtonNavItem(
            name = R.string.cart,
            imageId = Icons.Outlined.ShoppingCart,
            2
        ),
        ButtonNavItem(
            name = R.string.account,
            imageId = Icons.Outlined.Person,
            3
        ),
    )



    val pages = listOf(
        Screens.Home,
        Screens.Order,
        Screens.Cart,
        Screens.Account,
    )

    val navBackStackEntry = nav.currentBackStackEntryAsState()
    if (
        navBackStackEntry.value?.destination?.hasRoute(Screens.Home::class) == true ||
        navBackStackEntry.value?.destination?.hasRoute(Screens.Cart::class) == true ||
        navBackStackEntry.value?.destination?.hasRoute(Screens.Order::class) == true ||
        navBackStackEntry.value?.destination?.hasRoute(Screens.Account::class) == true
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        0.2.dp
                    )
                    .background(CustomColor.neutralColor200)
            )
            NavigationBar(
                modifier = Modifier.background(Color.White),
                tonalElevation = 5.dp,
                containerColor = Color.White,
                content = {
                    buttonNavItemHolder.fastForEachIndexed { index, value ->
                        val isSelectedItem =
                            navBackStackEntry.value?.destination?.hasRoute(
                                pages[index]::class
                            ) == true
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CustomColor.primaryColor700,
                                unselectedIconColor = CustomColor.neutralColor600,
                                selectedTextColor = CustomColor.primaryColor700,
                                unselectedTextColor = CustomColor.neutralColor600,
                            ),

                            selected = isSelectedItem,
                            onClick = {
                                if (!isSelectedItem)
                                    nav.navigate(pages[index])
                            },
                            label = {
                                Text(
                                    text = stringResource(value.name),
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = value.imageId,
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp)
                                )
                            })
                    }

                }
            )
        }
    }
}