package com.example.eccomerce_app.ui.component

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.eccomerce_app.model.StoreModel
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General


@Composable
fun StoreProductQuickInfo(
    storeData: StoreModel?=null,
    isCanNavigateToStore: Boolean,
    context: Context,
    getStoreData: () -> Unit
){
    if(storeData == null)return;
    Log.d("storeProductInfo","this the store image ${storeData.smallImage}")
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 15.dp)
    )

    {
        SubcomposeAsyncImage(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(50.dp)
                .width(50.dp)
                .clip(RoundedCornerShape(50.dp)),
            model = General.handlingImageForCoil(
                storeData.smallImage,
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
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(start = 5.dp)
        ) {

            Text(
                storeData.name,
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (16).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (isCanNavigateToStore)
                Text(
                    stringResource(R.string.visit_store),
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (16).sp,
                    color = CustomColor.primaryColor700,
                    modifier = Modifier
                        .clickable {
                            getStoreData()
                        }
                )


        }
    }

}