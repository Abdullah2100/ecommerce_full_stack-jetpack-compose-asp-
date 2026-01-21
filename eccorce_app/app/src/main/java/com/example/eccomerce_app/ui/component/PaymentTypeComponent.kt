package com.example.eccomerce_app.ui.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.model.PaymentType
import com.example.eccomerce_app.util.General


@Composable
fun PaymentTypeShape(paymentType: PaymentType ,
                     isSelectedIndex: Boolean,
                     currentIndex:Int,
                     paymentTypeSize:Int,
                     screenWidth:Int,
                     context: Context,
                     updateCurrentIndex:(value:Int, )->Unit){
    Row(
        modifier = Modifier
            .height(50.dp)
            .width(
                if (paymentTypeSize <= 3) ((((screenWidth - 30) / paymentTypeSize) - 15).dp)
                else ((paymentType.name.length * 18) + 20).dp
            )
            .border(
                width = if (isSelectedIndex) 0.dp else 1.dp,
                color = CustomColor.neutralColor200,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isSelectedIndex) CustomColor.primaryColor700 else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                 updateCurrentIndex.invoke(currentIndex)
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(25.dp)
                .width(25.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
            ,
            model = General.handlingImageForCoil(
                paymentType.thumbnail,
                context
            ),
            contentDescription = "",
            colorFilter = if (isSelectedIndex) ColorFilter.tint(Color.White) else ColorFilter.tint(Color.Black),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                ) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(25.dp) // Adjust the size here
                    )
                }
            },
        )
        Sizer(width = 5)
        Text(
            paymentType.name,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (isSelectedIndex) Color.White else CustomColor.neutralColor950,
            textAlign = TextAlign.Center
        )
    }
}