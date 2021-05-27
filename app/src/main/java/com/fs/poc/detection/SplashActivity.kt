package com.fs.poc.detection

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.fs.poc.LaunchActivity
import com.fs.poc.ui.login.LoginActivity
import com.fs.poc.utils.Singleton
import com.here.poc.BuildConfig
import com.here.poc.R
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.String
import java.net.CookieManager
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val textNumVersion = String.format(
            Locale.getDefault(),
            "%s%n%s",
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        textVersion.text = textNumVersion
    }

    override fun onResume() {
        super.onResume()
        var handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}