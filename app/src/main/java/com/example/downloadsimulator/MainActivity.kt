package com.example.downloadsimulator

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var llProgress: LinearLayout  // 用來顯示進度條的遮罩層
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sbDP: SeekBar
    private lateinit var sbDP2: SeekBar
    private lateinit var sbDP3: SeekBar
    private lateinit var btnstart: Button
    private var download = 0
    private var download2 = 0
    private var download3 = 0
    private var success: Boolean = true
    private var downloadsCompleted = 0
    private val totalDownloads = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 UI 元件
        llProgress = findViewById(R.id.llProgress)  // 黑色進度條遮罩
        tvProgress = findViewById(R.id.tvProgress)
        progressBar = findViewById(R.id.progressBar)
        sbDP = findViewById(R.id.sbDP)
        sbDP2 = findViewById(R.id.sbDP2)
        sbDP3 = findViewById(R.id.sbDP3)
        btnstart = findViewById(R.id.btnstart)

        btnstart.setOnClickListener {
            btnstart.isEnabled = false
            download = 0
            download2 = 0
            download3 = 0
            sbDP.progress = 0
            sbDP2.progress = 0
            sbDP3.progress = 0
            tvProgress.text = "0%"
            showBlackOverlayAndStartDownload() // 顯示黑幕並等待進度條完成
        }
    }

    // 顯示黑色進度條遮罩，並等待達到 100% 後開始下載
    private fun showBlackOverlayAndStartDownload() {
        llProgress.visibility = View.VISIBLE  // 顯示進度條遮罩
        progressBar.progress = 0
        tvProgress.text = "0%"

        Thread{
            var progress = 0
            while (progress<100){
                try {
                    Thread.sleep(50)
                }catch (ignored: InterruptedException){
                    //空
                }
                progress++
                runOnUiThread {
                    progressBar.progress = progress
                    tvProgress.text= "$progress%"
                }
            }
            runOnUiThread {
                llProgress.visibility = View.GONE
                startDownloads()
            }
        }.start()
        // 使用 Handler 在主線程更新進度條
    }

    // 開始三條下載
    private fun startDownloads() {
        downloadsCompleted = 0
        success = true

        // 啟動三條線程模擬下載
        Thread { downloadFile(1) }.start()
        Thread { downloadFile(2) }.start()
        Thread { downloadFile(3) }.start()
    }

    // 模擬下載過程
    private fun downloadFile(fileIndex: Int) {
        val downloadSpeed = arrayOf(0, 1, 2, 3, 4, 5)
        var downloadProgress = 0
        val downloadSpeed2 = arrayOf(0, 1, 2, 3)
        val downloadSpeed3 = arrayOf(0, 1)
        var fileSuccess = true

        while (downloadProgress < 100) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            // 模擬下載速度
            if (downloadProgress >= 90) {
                val speed = downloadSpeed3.random()
                downloadProgress += speed
                if (speed == 0) {
                    Thread.sleep(1200)
                }
            }
            if (downloadProgress in 50..89) {
                val speed = downloadSpeed2.random()
                downloadProgress += speed
                if (speed == 0) {
                    Thread.sleep(800)
                }
            }
            if (downloadProgress < 50) {
                val speed = downloadSpeed.random()
                downloadProgress += speed
                if (speed == 0) {
                    Thread.sleep(500)
                }
            }

            // 更新進度條
            when (fileIndex) {
                1 -> download = downloadProgress
                2 -> download2 = downloadProgress
                3 -> download3 = downloadProgress
            }

            // 使用 Handler 更新 UI 進度
            val msg = Message()
            msg.what = 1
            handler.sendMessage(msg)

            // 當下載完成時
            if (downloadProgress >= 100) {
                val successFlag = arrayOf(true, true, true, true, false).random()
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

    // 用 Handler 更新 UI
    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            1 -> {
                sbDP.progress = download
                sbDP2.progress = download2
                sbDP3.progress = download3
                tvProgress.text = "$download%"  // 更新進度顯示
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

    // 顯示 Toast 提示
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
