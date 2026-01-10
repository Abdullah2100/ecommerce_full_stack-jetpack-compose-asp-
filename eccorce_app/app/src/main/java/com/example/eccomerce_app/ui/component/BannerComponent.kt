package com.example.eccomerce_app.ui.component

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.model.BannerModel
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.theme.CustomColor
import kotlinx.coroutines.delay
import java.util.UUID


@Composable
fun BannerBage(

    banners: List<BannerModel>,
    isMe: Boolean? = false,
    nav: NavHostController? = null,
    deleteBanner:((id: UUID)->Unit)?=null,
    isShowTitle: Boolean=true
) {

    val context = LocalContext.current

    val currentPage = remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { banners.size }

    val operationType = remember { mutableStateOf('+') }

    if (banners.size > 1) {
        LaunchedEffect(Unit) {
            while (true) {
                pagerState.animateScrollToPage(currentPage.value, animationSpec = TweenSpec(
                    1000,
                    easing = EaseInOut
                ))

                if (currentPage.value== banners.size-1 ) {
                    operationType.value='-'
                } else if(currentPage.value==-1) {
                    operationType.value='+'
                }

                when(operationType.value){
                    '-'->{
                        currentPage.value-=1
                    }
                    else->{
                        currentPage.value+=1
                    }
                }
                delay(3000)
            }
        }
    }

if(isShowTitle)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {

        Text(
            stringResource(R.string.banners),
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = CustomColor.neutralColor950,
            textAlign = TextAlign.Center

        )

    }


    HorizontalPager(
        modifier = Modifier
            .padding(top = 15.dp), state = pagerState,
        pageSpacing = 2.dp
    ) { page ->

        ConstraintLayout {
            val (imageRef,buttonRef)= createRefs()

            SubcomposeAsyncImage(
                contentScale = ContentScale.Crop,
                modifier =
                    if (isMe == true) Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)) else
                        Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = CustomColor.neutralColor100,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                nav!!.navigate(Screens.Store(banners[page].storeId.toString()))
                            }
                            .constrainAs(imageRef) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.end)
                                start.linkTo(parent.start)
                            },
                model = General.handlingImageForCoil(
                    banners[page].image,
                    context
                ),
                contentDescription = "",
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                    ) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(54.dp) // Adjust the size here
                        )
                    }
                },
            )

            if(deleteBanner!=null)
                Box(
                    modifier = Modifier
                        .padding(
                            end = 10.dp,
                            top = 10.dp
                        )
                        .height(30.dp)
                        .width(30.dp)
                        .background(
                            CustomColor.alertColor_1_500,
                            RoundedCornerShape(10.dp)
                        )
                        .constrainAs(buttonRef) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            deleteBanner(banners[page].id)
                        }
                    , contentAlignment = Alignment.Center
                )
                {
                    Icon(Icons.Default.Delete,
                        "",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp))
                }
        }


    }

}

