package com.udacity

import android.app.DownloadManager
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        var extra = getIntent().extras
        if (extra!=null){
            var status = extra.getInt("status")
            var downloadedFile = extra.getString("downloadedFile")
            var statusDescription = ""
            when(status){
                DownloadManager.STATUS_SUCCESSFUL->statusDescription = "Download succeed"
                DownloadManager.STATUS_FAILED -> statusDescription = "Download Failed"
            }
            binding.detail.dataStatus = statusDescription
            binding.detail.dataFile = downloadedFile

        }

        binding.detail.activityMain.setOnClickListener{
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        val motionLayout = binding.detail.motionLayout
        //motionLayout.transitionToEnd()
        //motionLayout.transitionToStart()
        setSupportActionBar(toolbar)
    }

}
