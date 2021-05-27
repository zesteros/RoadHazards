package com.fs.poc.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import com.here.poc.R

/**
 * Class to handle runtime permission
 */
object PermissionModule {
    private const val PERMISSIONS_REQUEST = 1

    private const val PERMISSION_CAMERA = Manifest.permission.CAMERA

    fun hasPermission(activity: Activity): Boolean {
        return activity.checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity) {
        if (activity.shouldShowRequestPermissionRationale(PERMISSION_CAMERA)
        ) {
            Toast.makeText(
                activity,
                activity.getString(R.string.permission_reason),
                Toast.LENGTH_LONG
            ).show()
        }
        activity.requestPermissions(
            arrayOf(
                PERMISSION_CAMERA
            ), PERMISSIONS_REQUEST
        )
    }

    fun checkPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode == PERMISSIONS_REQUEST) {
            return grantResults.isNotEmpty() && permissions[0] == PERMISSION_CAMERA
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

}