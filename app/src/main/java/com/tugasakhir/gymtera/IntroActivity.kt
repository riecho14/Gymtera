package com.tugasakhir.gymtera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.tugasakhir.gymtera.ui.theme.GymteraTheme
import com.tugasakhir.gymtera.user.HomeActivity
import kotlinx.coroutines.launch

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymteraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    IntroScreen(modifier = Modifier.padding(innerPadding), context = this)
                }
            }
        }
    }
}

val customFontFamily = FontFamily(
    Font(R.font.ft_bold, FontWeight.Bold), Font(R.font.ft_regular, FontWeight.Normal)
)

@Composable
fun IntroPage(
    imageResId: Int, title: String, description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title, style = TextStyle(
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            ), modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(80.dp))
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.padding(22.dp)
        )
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = description, style = TextStyle(
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
fun IntroScreen(modifier: Modifier = Modifier, context: Context) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            count = 5, state = pagerState, modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> IntroPage(
                    imageResId = R.drawable.intro_welcome,
                    title = "Selamat Datang",
                    description = "Gymtera adalah aplikasi panduan fitness yang dikembangkan untuk menyediakan petunjuk yang jelas dan terperinci untuk setiap latihan, baik untuk pemula maupun pengguna tingkat lanjut."
                )

                1 -> IntroPage(
                    imageResId = R.drawable.ic_alat,
                    title = "Alat & Gerakan",
                    description = "Menu alat & gerakan digunakan untuk melihat alat yang tersedia pada fasilitas Gym ITERA, selain itu juga terdapat informasi tentang gerakan yang dapat dilakukan dengan alat tersebut."
                )

                2 -> IntroPage(
                    imageResId = R.drawable.ic_latihan,
                    title = "Pilih Latihan",
                    description = "Anda dapat memilih latihan yang diinginkan dengan tingkat level yang berbeda mulai dari beginner, intermediate, hingga advance pada menu pilih latihan."
                )

                3 -> IntroPage(
                    imageResId = R.drawable.ic_bmi,
                    title = "Hitung Indeks Massa Tubuh",
                    description = "Anda dapat menghitung indeks massa tubuh anda pada menu hitung BMI, fitur ini dilengkapi dengan informasi rentang BMI yang normal serta saran berdasarkan indeks massa tubuh Anda."
                )

                4 -> IntroPage(
                    imageResId = R.drawable.ic_presensi,
                    title = "Presensi",
                    description = "Lakukan presensi dengan mengisi daftar hadir di fasilitas Gym ITERA melalui menu presensi untuk membantu pengelola memonitor jumlah pengunjung pada fasilitas Gym ITERA."
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = colorResource(id = R.color.gold),
                inactiveColor = Color.Gray
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Previous",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        coroutineScope.launch {
                            if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    })
            if (pagerState.currentPage < 4) {
                Image(painter = painterResource(id = R.drawable.arrow_next),
                    contentDescription = "Next",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            coroutineScope.launch {
                                if (pagerState.currentPage < pagerState.pageCount - 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        })
            } else {
                Text(text = "Selesai", style = TextStyle(
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.gold),
                    textAlign = TextAlign.Center
                ), modifier = Modifier
                    .padding(bottom = 2.dp)
                    .clickable {
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                        (context as? ComponentActivity)?.finish()
                    })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    GymteraTheme {
        IntroScreen(context = LocalContext.current)
    }
}