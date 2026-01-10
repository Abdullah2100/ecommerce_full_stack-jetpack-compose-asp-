package com.example.eccomerce_app.ui.view.OnBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.R
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


@Composable
fun OnBoardingScreen(
    nav: NavController,
    userViewModel: UserViewModel
) {
    val fontScall = LocalDensity.current.fontScale
    val coroutine = rememberCoroutineScope()

    fun navToHome(){
        userViewModel.setIsPassOnBoardingScreen()

        nav.navigate(Screens.AuthGraph) {
            popUpTo(nav.graph.id) {
                inclusive = true
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()
        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(it)
                .padding(horizontal = 15.dp)
                .fillMaxSize()
        ) {
            val (titleRef, bottomReef) = createRefs()


            Column(
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .fillMaxWidth()
                    .constrainAs(titleRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.onboarding_log),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 20.dp)
                )

                Text(
                    stringResource(R.string.welcome_to_shopzen),
                    fontWeight = FontWeight.Bold,
                    fontFamily = General.satoshiFamily,
                    color = CustomColor.neutralColor950,
                    fontSize = ((32 / fontScall).sp)
                )

                Text(
                    stringResource(R.string.your_one_stop_destination_for_hassle_free_online_shopping),
                    color = CustomColor.neutralColor800,
                    fontSize = (18 / fontScall).sp,
                    textAlign = TextAlign.Center,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }


            Button(
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .height(50.dp)
                    .fillMaxWidth()
                    .constrainAs(bottomReef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    },
                onClick = {navToHome()},
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColor.primaryColor700
                ),

                ) {

                Text(
                    stringResource(R.string.get_started),
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (16 / fontScall).sp
                )
            }

        }
    }
}