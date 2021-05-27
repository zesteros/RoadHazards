package com.fs.poc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fs.poc.utils.PermissionModule
import com.here.poc.BuildConfig
import com.here.poc.R
import com.here.see.LiveSenseEngine

/**
 * This activity navigates to different models based on Product Flavors.
 * By default navigates to Road Basics.
 *
 * This class take care of following features:
 * 1. Checking and asking runtime permissions
 * 2. Asking user consent
 * 3. Navigating to the model class activity based on build variant.
 */
class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        // Ensure permissions have been granted!
        if (PermissionModule.hasPermission(this)) {
            initialise()
        } else {
            PermissionModule.requestPermission(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(PermissionModule.checkPermissionResult(requestCode, permissions, grantResults)) {
            initialise()
        } else {
            PermissionModule.requestPermission(this)
        }

    }

    private fun initialise() {
        /**
         * In case of Premium User, developer need to ask for consent from user, and get the
         * response, for other users, they can omit the call and proceed with initialising the
         * models.
         * Below 2 calls are for following purpose:
         *
         * 1. Appending your own message to the user consent dialog.
         * 2. Requesting consent and getting response in return (accept / refuse)
         */
        LiveSenseEngine.getInstance().addMessageToConsent("<br/><p>This is additional message added from the user!!!</p>")
        LiveSenseEngine.getInstance().requestConsent(this, object: LiveSenseEngine.LSDConsentCallback {
            override fun onRefuse() {
                navigate()
            }

            override fun onAccept() {
                navigate()
            }
        })
    }


    fun navigate() {
        when (BuildConfig.FLAVOR) {
            "roadSign" -> {
                val intent = Intent(this, RoadSignsActivity::class.java)
                startActivity(intent)
            }
            "roadBasic" -> {
                val intent = Intent(this, RoadBasicsActivity::class.java)
                startActivity(intent)
            }
            else -> {
                val intent = Intent(this, RoadSignsActivity::class.java)
                startActivity(intent)
            }
        }
        finish()
    }
}
