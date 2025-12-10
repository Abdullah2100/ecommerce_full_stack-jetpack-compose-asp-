package com.example.e_commercompose.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.model.ProductModel
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.theme.CustomColor
import java.util.UUID


@Composable
fun ProductShape(
    product: List<ProductModel>,
    delFun: ((it: UUID) -> Unit)? = null,
    updFun: ((productId: UUID) -> Unit)? = null,
    nav: NavHostController,
    isFromHome: Boolean = false,
    isCanNavigateToStore: Boolean = true
) {
    val context = LocalContext.current

    FlowRow(
        modifier = Modifier

            .fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(product.size) { index ->
            ConstraintLayout {
                val (rightRef, leftRef) = createRefs()
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 58.dp
                    ),
                    modifier = Modifier
                        .width(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            nav.navigate(
                                Screens.ProductDetails(
                                    product[index].id.toString(),
                                    isFromHome = isFromHome,
                                    isCanNavigateToStore = isCanNavigateToStore
                                )
                            )
                        }
                        .border(
                            (0.7).dp,
                            CustomColor.neutralColor200,
                            RoundedCornerShape(8.dp)
                        )
                ) {

                    Box(
                        modifier = Modifier
                            .height(150.dp)
                            .width(160.dp)
                            .background(
                                CustomColor.primaryColor50,
                                RoundedCornerShape(8.dp)
                            )
                    ) {

                        SubcomposeAsyncImage(
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            model = General.handlingImageForCoil(
                                product[index].thumbnail,
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
                    Column(
                        modifier = Modifier
                            .padding(
                                horizontal = 5.dp,
                                vertical = 5.dp
                            )
                    ) {
                        Sizer(10)
                        Text(
                            product[index].name,
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = (16).sp, color = CustomColor.neutralColor950,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Sizer(10)
                        Row {
                            Text(
                                product[index].symbol,
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp, color = CustomColor.neutralColor950
                            )
                            Text(
                                "${product[index].price}",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp, color = CustomColor.neutralColor950
                            )
                        }
                    }
                }
                if (delFun != null)
                    Box(
                        modifier = Modifier
                            .height(45.dp)
                            .width(45.dp)
                            .padding(top = 5.dp, start = 5.dp)
                            .background(
                                CustomColor.alertColor_1_600,
                                RoundedCornerShape(15.dp)
                            )
                            .clip(RoundedCornerShape(15.dp))
                            .clickable {
                                delFun(product[index].id)
                            }
                            .constrainAs(rightRef) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                            },
                        contentAlignment = Alignment.Center

                    ) {
                        Icon(
                            Icons.Default.Delete, "",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                if (updFun != null)
                    Box(
                        modifier = Modifier
                            .height(45.dp)
                            .width(45.dp)
                            .padding(top = 5.dp, start = 5.dp)
                            .background(
                                CustomColor.primaryColor700,
                                RoundedCornerShape(15.dp)
                            )
                            .clip(RoundedCornerShape(15.dp))
                            .clickable {
                                updFun(product[index].id)
                            }
                            .constrainAs(leftRef) {
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                            },
                        contentAlignment = Alignment.Center

                    ) {
                        Icon(
                            Icons.Default.Edit, "",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }


            }

        }
    }


}