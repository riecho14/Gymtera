@file:Suppress("UNUSED_PARAMETER")

package com.tugasakhir.gymtera.user.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.user.features.ui.theme.GymteraTheme

class JetpackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymteraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        RegisterScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {
    val goldColor = Color(ContextCompat.getColor(LocalContext.current, R.color.gold))
    val customFontFamily = FontFamily(
        Font(R.font.ft_semibold, FontWeight.SemiBold), Font(R.font.ft_regular, FontWeight.Normal)
    )

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(goldColor)
            ) {
                Column {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        painter = painterResource(
                            id = R.drawable.bg_clip_auth
                        ),
                        contentDescription = "Background Register",
                    )
                    Text(
                        text = stringResource(id = R.string.selamat_datang),
                        color = Color.White,
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(id = R.string.deskripsi_auth),
                        color = Color.White,
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .absoluteOffset(x = (-30).dp)
                    .size(140.dp),
                painter = painterResource(id = R.drawable.bg_cloud_left),
                contentDescription = "Background Left"
            )
            Image(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .absoluteOffset(x = 30.dp)
                    .size(140.dp),
                painter = painterResource(id = R.drawable.bg_cloud_right),
                contentDescription = "Backgorund Right"
            )
        }
        var nama by rememberSaveable { mutableStateOf("") }
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .absoluteOffset(y = (-40).dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text(stringResource(id = R.string.nama_lengkap)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 16.dp)
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 16.dp)
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    GymteraTheme {
        RegisterScreen(modifier = Modifier.fillMaxSize())
    }
}