package com.example.eccomerce_app.ui.component

import android.content.Context
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.e_commercompose.model.UserModel
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General
import java.io.File


@Composable
fun ProfileImage(
    file: File?=null,
    userThumbnail:String?=null ,
    context: Context,
    onChoseImage:()-> Unit
){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp)
    )
    {
        val (imageRef, cameralRef) = createRefs()
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
            when (file == null) {
                true -> {
                    when (userThumbnail==null) {
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
                                    userThumbnail,
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

                else -> {
                    SubcomposeAsyncImage(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(90.dp)
                            .width(90.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        model = General.handlingImageForCoil(
                            file.absolutePath,
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
        Box(
            modifier = Modifier
                .padding(end = 5.dp)
                .constrainAs(cameralRef) {
                    end.linkTo(imageRef.end)
                    bottom.linkTo(imageRef.bottom)
                }


        )
        {

            IconButton(
                onClick = {
                    onChoseImage()
                },
                modifier = Modifier
                    .size(30.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = CustomColor.primaryColor200
                )
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.camera),
                    "",
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
        }

    }

}