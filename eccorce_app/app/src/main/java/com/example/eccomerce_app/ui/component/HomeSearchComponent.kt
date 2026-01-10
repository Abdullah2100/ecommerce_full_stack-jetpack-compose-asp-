package com.example.eccomerce_app.ui.component

import android.annotation.SuppressLint
import android.app.LocaleConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomeSearchComponent (
    isClickingSearch: Boolean,
    updateClickState:(state:Boolean)->Unit
){
    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp


    val interactionSource = remember { MutableInteractionSource() }
    val heightAnimation = animateDpAsState(if (isClickingSearch) (screenHeight-80).dp else 0.dp)

    Card(
        modifier = Modifier
            .padding(top = 5.dp, bottom = 10.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    updateClickState.invoke(!isClickingSearch)

                }
            ), colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 5.dp,
        ),
        shape = RoundedCornerShape(8.dp)
    )
    {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp, bottom = 15.dp, start = 4.dp)

        ) {

            Icon(
                Icons.Outlined.Search, "",
                tint = CustomColor.neutralColor950,
                modifier = Modifier.size(24.dp)
            )
            Sizer(width = 5)
            Text(
                stringResource(R.string.find_your_favorite_items),
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = CustomColor.neutralColor800,
                textAlign = TextAlign.Center

            )

        }

    }

    //this for search list items to let user search by products
    AnimatedVisibility(

        visible = isClickingSearch,
        enter =  expandVertically(
            // Optional: customize the animation behavior
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            expandFrom = Alignment.Top // Expands downwards from the top
        ),
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 350),
            shrinkTowards = Alignment.Top // Shrinks upwards toward the top
        ),

        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Green)
                    .height((screenHeight-80).dp)
            ) { }


        }
    )

  }
