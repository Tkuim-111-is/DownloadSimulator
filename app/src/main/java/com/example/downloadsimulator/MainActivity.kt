package com.example.downloadsimulator

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var download = 0
    private var download2 = 0
    private var download3 = 0
    private var success: Boolean = true
    private lateinit var btnstart: Button
    private lateinit var sbDP: SeekBar
    private lateinit var sbDP2: SeekBar
    private lateinit var sbDP3: SeekBar

    // 追蹤下載進度的標誌
    private var downloadsCompleted = 0
    private val totalDownloads = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnstart = findViewById(R.id.btnstart)
        sbDP = findViewById(R.id.sbDP)
        sbDP2 = findViewById(R.id.sbDP2)
        sbDP3 = findViewById(R.id.sbDP3)

        btnstart.setOnClickListener {
            btnstart.isEnabled = false
            download = 0
            download2 = 0
            download3 = 0
            sbDP.progress = 0
            sbDP2.progress = 0
            sbDP3.progress = 0
            startDownloads()
        }
    }

    // Handler 用於更新 UI
    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            1 -> {
                sbDP.progress = download
                sbDP2.progress = download2
                sbDP3.progress = download3
            }
            2 -> {
                sbDP.progress = download
                sbDP2.progress = download2
                sbDP3.progress = download3
                if (downloadsCompleted == totalDownloads) {
                    if (success) {
                        showToast("下載成功")
                    } else {
                        showToast("下載失敗，請重新下載")
                    }
                    btnstart.isEnabled = true
                }
            }
        }
        true
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun startDownloads() {
        downloadsCompleted = 0
        success = true

        // 開啟三個線程來模擬三個檔案下載
        Thread { downloadFile(1) }.start()
        Thread { downloadFile(2) }.start()
        Thread { downloadFile(3) }.start()
    }

    // 模擬單一檔案下載
    private fun downloadFile(fileIndex: Int) {
        val downloadSpeed = arrayOf(0,1, 2, 3, 4, 5)
        var downloadProgress = 0
        val downloadSpeed2 = arrayOf(0,1,2,3)
        val downloadSpeed3 = arrayOf(0,1)
        var fileSuccess = true

        while (downloadProgress < 100) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if(downloadProgress>=90){
                val speed = downloadSpeed3.random()
                downloadProgress += speed
                if(speed==0){
                    Thread.sleep(1200)
                }
            }
            if(downloadProgress>=50&&downloadProgress<90){
                val speed = downloadSpeed2.random()
                downloadProgress += speed
                if(speed==0){
                    Thread.sleep(800)
                }
            }
            if(downloadProgress>=0&&downloadProgress<50){
                val speed = downloadSpeed.random()
                downloadProgress += speed
                if(speed==0){
                    Thread.sleep(500)
                }
            }

            when (fileIndex) {
                1 -> download = downloadProgress
                2 -> download2 = downloadProgress
                3 -> download3 = downloadProgress
            }

            val msg = Message()
            msg.what = 1
            handler.sendMessage(msg)

            if (downloadProgress >= 100) {
                val successFlag = arrayOf(true,true,true,true, false).random()
                if (!successFlag) fileSuccess = false

                val statusMsg = Message()
                statusMsg.what = 2
                handler.sendMessage(statusMsg)

                synchronized(this) {
                    downloadsCompleted++
                    if (!fileSuccess) success = false
                }
            }
        }
    }
}


