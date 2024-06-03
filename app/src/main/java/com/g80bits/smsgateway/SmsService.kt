package com.g80bits.smsgateway

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat

class SmsService(private val context: Context) {

    fun sendSms(message: SmsModel) {

        if (ContextCompat.checkSelfPermission(
                context,
                "android.permission.SEND_SMS"
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            if (message.number.isNotEmpty() && message.text.isNotEmpty()) {
                val subscriptionManager =
                    context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                val subscriptionId =
                    subscriptionManager.activeSubscriptionInfoList[0].subscriptionId
                val smsManager = context.getSystemService(SmsManager::class.java)
                    .createForSubscriptionId(subscriptionId)
                smsManager.sendTextMessage(message.number, null, message.text, null, null)
            }
        }
    }
}