package com.example.eccomerce_app.ui.component

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.model.ProductModel
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General.toCustomFil
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

@Composable
fun ProductShape(
    product: ProductModel,
    context: Context,
    selectedImage: String,
    updateSelectIndex: (value: String) -> Unit
) {
    val productImages = remember { mutableStateOf(product.productImages + product.thumbnail) }

    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start

    )
    {
        SubcomposeAsyncImage(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            model = General.handlingImageForCoil(
                product.thumbnail,
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

        if (product.productImages.isNotEmpty()) {
            Sizer(10)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,

                )
            {
                items(
                    items = productImages.value,
                    key = { image -> image })
                { image ->

                    Box(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .border(
                                1.dp,
                                if (image == selectedImage)
                                    CustomColor.primaryColor700 else CustomColor.neutralColor200,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        SubcomposeAsyncImage(
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                                .clip(
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (image != selectedImage)
                                        updateSelectIndex(image)
                                },
                            model = General.handlingImageForCoil(
                                image,
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
                                        modifier = Modifier.size(25.dp) // Adjust the size here
                                    )
                                }
                            },
                        )
                    }
                }
            }

        }
        Sizer(10)
        Text(
            text = product.name,
            color = CustomColor.neutralColor950,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (18).sp
        )
        Sizer(16)
        Text(
            text = "\$${product.price}",
            color = CustomColor.neutralColor950,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Sizer(16)
        Text(
            text = stringResource(R.string.product_details),
            color = CustomColor.neutralColor950,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Sizer(10)
        Text(
            text = product.description,
            color = CustomColor.neutralColor800,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
        Sizer(15)
    }

}


@Composable
fun CreateProductImage(
    thumbnail: String? = null,
    images: List<String>? = null,
    deleteImages: List<String>,
    context: Context,
    onSelectThumbnail: (value: String) -> Unit,
    onSelectImages: (value: List<String>) -> Unit,
    onRemoveAlreadyProductImage: (value: List<String>) -> Unit
) {
    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    )
    { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context)
            if (fileHolder != null) {
                if (thumbnail != null && !deleteImages.contains(thumbnail)) {
                    val deleteImageCopy = mutableListOf<String>()
                    deleteImageCopy.add(thumbnail)
                    deleteImageCopy.addAll(deleteImages)
                    onRemoveAlreadyProductImage(deleteImageCopy)
                }
                onSelectThumbnail(fileHolder.absolutePath)

            }
        }
    }


    val selectMultipleImages = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    )
    { uris ->
        val imagesHolder = mutableListOf<String>()

        if (uris.isNotEmpty()) {
            uris.forEach { productImages ->
                val file = productImages.toCustomFil(context)

                if (file != null) {
                    imagesHolder.add(file.absolutePath)
                }
            }
            onSelectImages(imagesHolder)
        }
    }


    Column() {
        Text(
            stringResource(R.string.product_thumbnail),
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (18).sp,
            color = CustomColor.neutralColor950,
            textAlign = TextAlign.Center,
        )
        Sizer(15)
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
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
                    .height(150.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CustomColor.neutralColor500,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = Color.White,
                    ),
                contentAlignment = Alignment.Center
            )
            {
                when (thumbnail == null) {
                    true -> {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                            "",
                            modifier = Modifier.size(80.dp),
                            tint = CustomColor.neutralColor200
                        )
                    }

                    else -> {
                        SubcomposeAsyncImage(
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            model = General.handlingImageForCoil(
                                thumbnail,
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
                    .padding(end = 5.dp, bottom = 10.dp)
                    .constrainAs(cameralRef) {
                        end.linkTo(imageRef.end)
                        bottom.linkTo(imageRef.bottom)
                    }


            )
            {

                IconButton(
                    onClick = {
                        onImageSelection.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
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
        Sizer(10)
        Text(
            stringResource(R.string.product_images),
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (18).sp,
            color = CustomColor.neutralColor950,
            textAlign = TextAlign.Center,
        )
        Sizer(5)
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            val (cameralRef) = createRefs()

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = CustomColor.neutralColor500,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(5.dp)

            ) {
                if (images.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                            "",
                            modifier = Modifier.size(80.dp),
                            tint = CustomColor.neutralColor200
                        )
                    }
                }

                images!!.forEach { value ->

                    ConstraintLayout {
                        Box(
                            modifier = Modifier

                                .height(120.dp)
                                .width(120.dp)
                                .background(
                                    color = Color.White,
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            SubcomposeAsyncImage(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                model = General.handlingImageForCoil(
                                    value,
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

                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .width(30.dp)
                                .background(
                                    Color.Red,
                                    RoundedCornerShape(20.dp)
                                )
                                .clip(
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    onRemoveAlreadyProductImage(images.filter { it != value })
                                },
                            contentAlignment = Alignment.Center
                        )
                        {
                            Icon(
                                Icons.Default.Clear, "",
                                tint = Color.White
                            )
                        }
                    }

                }
            }


            Box(
                modifier = Modifier
                    .padding(end = 5.dp, bottom = 10.dp)
                    .constrainAs(cameralRef) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
            {

                IconButton(
                    onClick = {
                        selectMultipleImages.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
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
}


