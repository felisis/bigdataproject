package com.felis.mldl.camera

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.felis.mldl.main.MainActivity
import com.felis.mldl.databinding.MorphingendActivityBinding
import java.io.File
import com.bumptech.glide.signature.ObjectKey
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class MorphingendActivity : AppCompatActivity() {

    private lateinit var binding: MorphingendActivityBinding
    private lateinit var filePath: String


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MorphingendActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val intent = Intent(baseContext, MainActivity::class.java)
        val intent1 = Intent(this, MorphingselectpicActivity::class.java)

        binding.retrymorph.setOnClickListener {
            startActivity(intent1)
        }

        binding.loadmorphingImg.setOnClickListener {
            println("이미지 불러오겠음")
            val requestOptions = RequestOptions()
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
            requestOptions.skipMemoryCache(false)
            requestOptions.centerCrop()
            requestOptions.signature(ObjectKey(System.currentTimeMillis()))
            Glide.with(this.binding.morphingImg)
                .load(CameraService.IMAGELOAD_SITE)
                .apply(requestOptions)
                .into(binding.morphingImg)
            println("이미지 불러왔음")
            //glide library 사용하여 이미지 서버에서 로딩 후 뷰 생성
            binding.loadmorphingImg.visibility = View.INVISIBLE
            binding.retrymorph.visibility = View.VISIBLE
            binding.saveimg.visibility = View.VISIBLE
            binding.mainbtn.visibility = View.VISIBLE
        }

        binding.saveimg.setOnClickListener {
            downloadImage(CameraService.IMAGELOAD_SITE)
        }

        binding.mainbtn.setOnClickListener {
            startActivity(intent)
        }


    }

    var msg: String? = ""
    var lastMsg = ""
    @SuppressLint("Range")
    private fun downloadImage(url: String) {

        val directory = File(Environment.DIRECTORY_PICTURES)

        if (!directory.exists()) {
            directory.mkdirs()
        }
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }
        // ...

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    this.runOnUiThread {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }).start()
    }

    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "사진을 압축중입니다."
            DownloadManager.STATUS_RUNNING -> "사진을 다운로드중입니다."
            DownloadManager.STATUS_SUCCESSFUL -> "사진이 성공적으로 저장되었습니다."
            else -> "There's nothing to download"
        }
        return msg
    }
}