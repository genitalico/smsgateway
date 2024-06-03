package com.g80bits.smsgateway

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class Permissions(private val activity: ComponentActivity) {

    private val permissionsList = listOf(
        "android.permission.SEND_SMS",
        "android.permission.READ_PHONE_STATE"
    )

    private val requestPermissionsLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { permission ->
                val permissionName = permission.key
                val isGranted = permission.value

                when (permissionName) {
                    "android.permission.SEND_SMS" -> {
                        if (isGranted) {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.sms_permission_granted),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.sms_permission_denied),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    "android.permission.READ_PHONE_STATE" -> {
                        if (isGranted) {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.read_phone_permission_granted),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                activity,
                                activity.getString(R.string.read_phone_permission_denied),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    fun checkPermissions() {
        val permissionsToRequest = permissionsList.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            Toast.makeText(
                activity,
                activity.getString(R.string.all_permissions),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
