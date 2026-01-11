package com.example.eccomerce_app.ui.view.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.e_commercompose.ui.component.ProductLoading
import com.example.eccomerce_app.ui.component.ProductShape
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ProductCategoryScreen(
    nav: NavHostController,
    categoryId: String,
    categoryViewModel: CategoryViewModel,
    productViewModel: ProductViewModel
) {
    val categories = categoryViewModel.categories.collectAsState()
    val products = productViewModel.products.collectAsState()

    val categoryId = UUID.fromString(categoryId)
    val productsByCategory = products.value?.filter { it.categoryId == categoryId }

    val coroutine = rememberCoroutineScope()
    val lazyState = rememberLazyListState()
    val state = rememberPullToRefreshState()


    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }
    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }


    val page = remember { mutableIntStateOf(1) }




    LaunchedEffect(reachedBottom.value) {
        if (!productsByCategory.isNullOrEmpty() && reachedBottom.value && productsByCategory.size > 23) {
            productViewModel.getProductsByCategoryID(
                page.intValue,
                categoryId,
                isLoadingMore.value,
                updateLoadingState = { value -> isLoadingMore.value = value },
                updatePageNumber = { value -> page.intValue = value }
            )
        }

    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            SharedAppBar(
                title = categories.value?.firstOrNull { it.id == categoryId }?.name ?: "",
                nav = nav
            )
        }


    ) { paddingValue ->
        paddingValue.calculateTopPadding()
        paddingValue.calculateBottomPadding()


        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                coroutine.launch {
                    if (!isRefresh.value) isRefresh.value = true
                    page.intValue = 1;
                    productViewModel.getProductsByCategoryID(
                        1,
                        categoryId,
                        isLoadingMore.value,
                        updatePageNumber = { page.intValue = 1 },
                        updateLoadingState = { value -> isLoadingMore.value = value }
                    )
                    if (isRefresh.value) {
                        delay(1000)
                        isRefresh.value = false
                    }

                }
            },
            state = state,
            indicator = {
                Indicator(
                    modifier = Modifier
                        .padding(top = paddingValue.calculateTopPadding())
                        .align(Alignment.TopCenter),
                    isRefreshing = isRefresh.value,
                    containerColor = Color.White,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    state = state
                )
            },
        ) {
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .padding(paddingValue)
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 15.dp)
            ) {


                item {

                    Sizer(10)
                    when (productsByCategory == null) {
                        true -> {
                            ProductLoading(50)
                        }

                        else -> {
                            if (productsByCategory.isNotEmpty()) {
                                ProductShape(products.value!!, nav = nav)
                            }
                        }
                    }
                }

                if (isLoadingMore.value) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        )
                        {
                            CircularProgressIndicator(color = CustomColor.primaryColor700)
                        }
                    }
                }

                item {
                    Sizer(40)
                }
            }
        }


    }
}