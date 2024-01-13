package com.xinhui.mobfinalproject.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xinhui.mobfinalproject.core.utils.AlarmManagerHelper
import com.xinhui.mobfinalproject.core.utils.NotificationUtil
import com.xinhui.mobfinalproject.data.model.Notification
import com.xinhui.mobfinalproject.data.repo.notification.NotificationRepo
import com.xinhui.mobfinalproject.data.repo.product.ProductRepo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class NotifyBroadcastReceiver: BroadcastReceiver() {

    @Inject
    lateinit var productRepo: ProductRepo
    @Inject
    lateinit var notificationRepo: NotificationRepo

    override fun onReceive(context: Context?, data: Intent?) {
        if (context == null){
            return
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val status = data?.getStringExtra("status") ?: ""
                data?.getStringExtra("id")?.let { id ->
                    productRepo.getProductById(id)?.let { product ->
                        val notificationBuilder = NotificationUtil.createNotification(
                            context,
                            product.productName,
                            status)
                        NotificationUtil.notify(context, notificationBuilder.build())
                        AlarmManagerHelper.apply {
                            itemIdToRequestCodeMap["$id$status"]?.let { reqCode ->
                                usedRequestCodes.remove(reqCode)
                                itemIdToRequestCodeMap.remove("$id$status")
                            }
                        }
                        addNotification(product.productName, status)
                    }
                }
            }
        }
    }

    private fun addNotification(productName: String, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                notificationRepo.addNotification(
                    Notification(
                        productName = productName,
                        expireStatus = status,
                        notifyDateTime = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"))
                    )
                )
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }
}