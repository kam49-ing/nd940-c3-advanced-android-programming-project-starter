package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import kotlin.properties.Delegates

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var status by Delegates.notNull<Int>()
    private var downloadedFile by Delegates.notNull<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )
        custom_button.setOnClickListener {
            //canceling all previous notifications
            notificationManager.cancel()
            //downloading if an item is selected
            if(radio_group.checkedRadioButtonId != -1) {
                download()
                downloadedFile = when(radio_group.checkedRadioButtonId){
                    radio_glide.id-> getString(R.string.glide_url)
                    radio_project.id -> getString(R.string.project_url)
                    radio_retrofit.id -> getString(R.string.retrofit_url)
                    else -> "No project has been selected"
                }
            } else{
                custom_button.selectItem() //showing a message
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val detailIntent = Intent(context?.applicationContext, DetailActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("status", status)
                putExtra("downloadedFile", downloadedFile)
            }

            val detailPendingIntent:PendingIntent =
                PendingIntent.getActivity(context?.applicationContext, REQUEST_CODE, detailIntent, PendingIntent.FLAG_ONE_SHOT)
            action = NotificationCompat.Action(R.drawable.common_google_signin_btn_icon_light, "view", detailPendingIntent)
            val builder =
                context?.applicationContext?.let {
                    NotificationCompat.Builder(it, getString(R.string.download_notification_channel_id))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentText(getString(R.string.notification_description))
                        .setContentTitle(getString(R.string.notification_title))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(detailPendingIntent)
                        .setAutoCancel(true)
                        .addAction(action)
                }
            if(id == downloadID){
                custom_button.downloadFinished()
                with(context?.applicationContext?.let { NotificationManagerCompat.from(it) }){
                    builder?.build()?.let {
                        this!!.notify(
                            NOTIFICATION_ID,
                            it
                        )
                    }
                }
            }
        }
    }

    private fun createChannel(channel_notification_id:String, channel_notification_name:String){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    channel_notification_id,
                    channel_notification_name,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableVibration(true)
                notificationChannel.enableLights(true)
                notificationChannel.setShowBadge(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.description = getString(R.string.notification_description)
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(notificationChannel)
            }
    }

    @SuppressLint("Range")
    private fun download() {

        //parse the url with the selected option
        val checkedButton = radio_group.checkedRadioButtonId
        var url = when(checkedButton){
            radio_glide.id -> URL_GLIDE
            radio_retrofit.id -> URL_RETROFIT
            else -> URL
        }
        CoroutineScope( Dispatchers.IO).launch {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            var donwloadFinished = false
            while (!donwloadFinished) {
                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadID));
                while (cursor.moveToNext()) {
                    status =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    when (status) {
                        DownloadManager.STATUS_FAILED -> {
                            Toast.makeText(
                                applicationContext,
                                "Download failed",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("applicationContext", "download failed")
                            donwloadFinished = true
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Dispatchers.Main{
                                Toast.makeText(
                                    applicationContext,
                                    "Download succeed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            donwloadFinished = true
                        }
                        DownloadManager.STATUS_PENDING -> {

                        }
                        DownloadManager.STATUS_RUNNING -> {

                        }
                        DownloadManager.STATUS_PAUSED -> {
                            Dispatchers.Main {
                                Toast.makeText(
                                    applicationContext,
                                    "Download paused",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }

        }
    }

    fun NotificationManager.cancel(){
        cancelAll()
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val CHANNEL_ID = "channelId"
    }

}
