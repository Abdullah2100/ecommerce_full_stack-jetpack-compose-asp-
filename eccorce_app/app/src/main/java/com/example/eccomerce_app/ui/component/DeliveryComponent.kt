package com.example.eccomerce_app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.model.Delivery
import com.example.eccomerce_app.util.General


@Composable
fun StoreDeliveryComponent(delivery: Delivery,screenWidth:Int)
{
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .height(140.dp)
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .padding(top = 1.dp)
            .background(
                Color.White,
                RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        )
        {
            ConstraintLayout(
                modifier = Modifier
                    .wrapContentSize()
            )
            {
                val (imageRef) = createRefs()
                Box(
                    modifier = Modifier
                        .constrainAs(imageRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .height(110.dp)
                        .width(110.dp)
                        .border(
                            width = 1.dp,
                            color = CustomColor.neutralColor500,
                            shape = RoundedCornerShape(60.dp)
                        ),
                    contentAlignment = Alignment.Center
                )
                {

                    when (delivery.thumbnail.isNullOrEmpty()) {
                        true -> {

                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.user),
                                "",
                                modifier = Modifier.size(80.dp)
                            )
                        }

                        else -> {

                            SubcomposeAsyncImage(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(90.dp)
                                    .width(90.dp)
                                    .clip(RoundedCornerShape(50.dp)),
                                model = General.handlingImageForCoil(
                                    delivery.thumbnail,
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
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .width((screenWidth - 130).dp)
                    .padding(start = 5.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    delivery.user.name,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (19).sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center
                )

                Text(
                    delivery.user.phone,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (19).sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center
                )

            }
        }
    }

}