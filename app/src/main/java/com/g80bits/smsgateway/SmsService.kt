package com.g80bits.smsgateway

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class SmsService(private val activity: ComponentActivity) {

    private val permissionStr: String = "android.permission.SEND_SMS"

    private val requestSmsPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.sms_permission_granted),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.sms_permission_denied),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    fun checkSmsPermissionAndSend() {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                permissionStr
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.sms_permission_granted),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                requestSmsPermissionLauncher.launch(permissionStr)
            }
        }
    }
}