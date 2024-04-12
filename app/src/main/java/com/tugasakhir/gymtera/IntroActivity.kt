package com.tugasakhir.gymtera

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.tugasakhir.gymtera.addon.IntroPref
import com.tugasakhir.gymtera.user.HomeActivity

class IntroActivity : AppIntro() {
    private lateinit var introPref: IntroPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        introPref = IntroPref(this)
        if (!introPref.isFirstTime) {
            startHomeActivity()
            return
        }

        isColorTransitionsEnabled = true
        isIndicatorEnabled = true
        isWizardMode = true

        showStatusBar(true)
        setNextArrowColor(getColor(R.color.gold))
        setBackArrowColor(getColor(R.color.gold))
        setColorDoneText(getColor(R.color.gold))
        setStatusBarColor(getColor(R.color.gold))
        setIndicatorColor(
            selectedIndicatorColor = getColor(R.color.gold),
            unselectedIndicatorColor = getColor(R.color.tint)
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "Selamat Datang",
                description = "Gymtera adalah aplikasi panduan fitness yang dikembangkan untuk menyediakan petunjuk yang jelas dan terperinci untuk setiap latihan, baik untuk pemula maupun pengguna tingkat lanjut.",
                imageDrawable = R.drawable.intro_welcome,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                backgroundColorRes = R.color.white,
                titleTypefaceFontRes = R.font.ft_bold,
                descriptionTypefaceFontRes = R.font.ft_regular,
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "Alat & Gerakan",
                description = "Menu alat & gerakan digunakan untuk melihat alat yang tersedia pada fasilitas Gym ITERA, selain itu juga terdapat informasi tentang gerakan yang dapat dilakukan dengan alat tersebut.",
                imageDrawable = R.drawable.ic_alat,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                backgroundColorRes = R.color.white,
                titleTypefaceFontRes = R.font.ft_bold,
                descriptionTypefaceFontRes = R.font.ft_regular,
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "Pilih Latihan",
                description = "Anda dapat memilih latihan yang diinginkan dengan tingkat level yang berbeda mulai dari beginner, intermediate, hingga advance pada menu pilih latihan.",
                imageDrawable = R.drawable.ic_latihan,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                backgroundColorRes = R.color.white,
                titleTypefaceFontRes = R.font.ft_bold,
                descriptionTypefaceFontRes = R.font.ft_regular,
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "Hitung Indeks Massa Tubuh",
                description = "Anda dapat menghitung indeks massa tubuh anda pada menu hitung BMI, fitur ini dilengkapi dengan informasi rentang BMI yang normal serta saran berdasarkan indeks massa tubuh Anda.",
                imageDrawable = R.drawable.ic_bmi,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                backgroundColorRes = R.color.white,
                titleTypefaceFontRes = R.font.ft_bold,
                descriptionTypefaceFontRes = R.font.ft_regular,
            )
        )
        addSlide(
            AppIntroFragment.createInstance(
                title = "Presensi",
                description = "Lakukan presensi dengan mengisi daftar hadir di fasilitas Gym ITERA melalui menu presensi untuk membantu pengelola memonitor jumlah pengunjung pada fasilitas Gym ITERA.",
                imageDrawable = R.drawable.ic_presensi,
                titleColorRes = R.color.black,
                descriptionColorRes = R.color.black,
                backgroundColorRes = R.color.white,
                titleTypefaceFontRes = R.font.ft_bold,
                descriptionTypefaceFontRes = R.font.ft_regular,
            )
        )
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        introPref.isFirstTime = false
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        introPref.isFirstTime = false
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}