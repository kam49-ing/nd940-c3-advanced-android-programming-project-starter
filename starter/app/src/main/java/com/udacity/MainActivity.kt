package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if(radio_group.checkedRadioButtonId != -1) {
                download()
            }
            else{
                custom_button.selectItem()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID){
                custom_button.downloadFinished()
            }
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
                val status =
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
                        Toast.makeText(
                            applicationContext,
                            "Download succeed",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.i("applicationContext", "download succeed")
                        donwloadFinished = true
                    }
                    DownloadManager.STATUS_PENDING -> {
                        Log.i(
                            "applicationContext",
                            "Download pending"
                        )
                    }
                    DownloadManager.STATUS_RUNNING -> {
                        Log.i(
                            "applicationContext",
                            "Download runing"
                        )
                    }
                    DownloadManager.STATUS_PAUSED -> {
                        Toast.makeText(
                            applicationContext,
                            "Download paused",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.i("applicationContext", "download paused")
                    }
                }
            }
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val CHANNEL_ID = "channelId"
    }

}
