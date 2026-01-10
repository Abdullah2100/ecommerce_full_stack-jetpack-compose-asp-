package com.example.eccomerce_app.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.e_commercompose.model.OrderItem
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


//import qrgenerator.QRCodeImage


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ResourceType")
@Composable
fun OrderItemShape(
    orderItem: OrderItem,
    context: Context,
    screenWidth: Int,
    isShowOrderStatus: Boolean = false
) {
    val isShowDialog = remember { mutableStateOf(false) }
    val qrBitMap = remember { mutableStateOf<Bitmap?>(null) }

    val orderStatus = when (orderItem.orderStatusName) {
        "Rejected" -> R.drawable.rejected
        "Inprogress" -> R.drawable.inprogress
        "Accepted" -> R.drawable.accepted
        "In away" -> R.drawable.in_away
        "Received" -> R.drawable.recived
        else -> R.drawable.completed

    }

    fun openQrDialog() {
        isShowDialog.value = true
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                orderItem.id.toString(),
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            qrBitMap.value = bitmap
        } catch (e: Exception) {
            isShowDialog.value = false;

        }

    }
    if (isShowDialog.value) {
        Dialog(
            onDismissRequest = { isShowDialog.value = false },
            properties = DialogProperties(), content = {
                if (qrBitMap.value != null)
                    Box {
                        Image(
                            bitmap = qrBitMap.value!!.asImageBitmap(),
                            contentDescription = "",
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(8.dp)
                                ),

                            )
                    }
            })
    }


    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )

    {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .wrapContentHeight()
                .width((screenWidth).dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = General.handlingImageForCoil(
                    orderItem.product.thumbnail,
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
                            modifier = Modifier.size(53.dp) // Adjust the size here
                        )
                    }
                },
            )
            Sizer(width = 10)
            Column {
                Text(
                    orderItem.product.name,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = (16).sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Sizer(width = 5)
                orderItem.productVariant.forEach { value ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val title = value.variantName
                        Text(
                            "$title :",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )
                        Sizer(width = 5)
                        when (title == "Color") {
                            true -> {
                                val colorValue =
                                    General.convertColorToInt(value.productVariantName)

                                if (colorValue != null)
                                    Box(
                                        modifier = Modifier
                                            .height(20.dp)
                                            .width(20.dp)
                                            .background(
                                                colorValue,
                                                RoundedCornerShape(20.dp)
                                            )

                                            .clip(RoundedCornerShape(20.dp))
                                    )
                            }

                            else -> {
                                Box(
                                    modifier = Modifier

                                        .clip(RoundedCornerShape(20.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = value.productVariantName,
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor800,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                    }
                }
                if (isShowOrderStatus) {
                    Row {
                        Text(
                            stringResource(R.string.quantity),

                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )
                        Sizer(width = 5)
                        Text(
                            "${orderItem.quantity}",

                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            stringResource(R.string.order_status),

                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )
                        Sizer(width = 5)
                        Image(
                            imageVector = ImageVector
                                .vectorResource(orderStatus), ""
                        )
                        Sizer(width = 5)
                        Text(
                            orderItem.orderStatusName,
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (12).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )

                    }

                    if (orderItem.orderItemStatus != "ReceivedByDelivery")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        )
                        {
                            Text(
                                stringResource(R.string.collect_order),

                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                            Image(
                                imageVector = ImageVector
                                    .vectorResource(R.drawable.qr_button),
                                "",
                                modifier = Modifier.clickable(onClick = {
                                    openQrDialog()
                                })
                            )


                        }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Text(
                            stringResource(R.string.item_status),

                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            orderItem.orderItemStatus,
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (12).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )

                    }


                }
            }

        }


    }

}