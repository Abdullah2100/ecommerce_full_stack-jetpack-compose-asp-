package com.example.eccomerce_app.ui.component

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.model.Category
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.ProductViewModel
import java.util.UUID


@Composable
fun CategoryShape(
    categories:List<Category>,
    productViewModel: ProductViewModel,
    nav: NavHostController
){
    val context = LocalContext.current

    fun getProductAndNavigateToProduct(id: UUID){
        productViewModel.getProductsByCategoryID(
            pageNumber = 1,
            categoryId = id
        )
        nav.navigate(
            Screens.ProductCategory(
                id.toString()
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        Text(
            stringResource(R.string.category),
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = CustomColor.neutralColor950,
            textAlign = TextAlign.Center

        )
        if (categories.size > 4) Text(
            stringResource(R.string.view_all),
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = CustomColor.neutralColor950,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {
                nav.navigate(Screens.Category)
            }

        )
    }
    LazyRow(

        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        items(items = categories,
            key = {it.id}) { category->
            Column(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        getProductAndNavigateToProduct(category.id)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .height(69.dp)
                        .width(70.dp)
                        .background(CustomColor.primaryColor50, RoundedCornerShape(8.dp))
                        .padding(horizontal = 5.dp)
                ){
                    SubcomposeAsyncImage(
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .height(69.dp)
                            .width(70.dp),
                        model = General.handlingImageForCoil(category.image,context) ,
                        contentDescription = "",
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                            ) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(35.dp),
                                    strokeWidth = 2.dp// Adjust the size here
                                )
                            }
                        },
                        onError = {error->
                        }
                    )

                }
                Sizer(4)
                Text( category.name,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = CustomColor.neutralColor900,
                )
            }
        }
    }

}