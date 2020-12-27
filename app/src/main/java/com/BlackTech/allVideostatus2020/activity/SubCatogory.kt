package com.BlackTech.allVideostatus2020.activity

import android.app.DownloadManager
import android.content.Intent
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.BlackTech.allVideostatus2020.R
import com.BlackTech.allVideostatus2020.Utility.Config
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.File
import java.io.IOException
import java.util.*

class SubCatogory : AppCompatActivity() {
    private var player: SimpleExoPlayer? = null
    private var playerView: PlayerView? = null
    private var tf_video_title: TextView? = null
    private var tv_video_subcat: TextView? = null
    private var btn_download: ImageView? = null
    private val videoPath: String? = null
    var downloadManager: DownloadManager? = null
    val list = ArrayList<Long>()
    var rel_showProgress: RelativeLayout? = null
    var detail_toolbar: View? = null
    var txt_fileSize: TextView? = null
    var txt_progress: TextView? = null
    var btn_share: ImageView? = null
    var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_catogory)
        playerView = findViewById(R.id.video_player)
        tf_video_title = findViewById(R.id.tf_video_title)
        tv_video_subcat = findViewById(R.id.tv_video_subcat)
        btn_download = findViewById(R.id.btn_download)
        rel_showProgress = findViewById<RelativeLayout>(R.id.rel_showProgress)
        detail_toolbar = findViewById<View>(R.id.detail_toolbar)
        txt_progress = findViewById(R.id.txt_progress)
        txt_fileSize = findViewById(R.id.txt_fileSize)
        progressBar = findViewById(R.id.progressBarHorizontal)
        btn_share = findViewById(R.id.btn_share)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val intent = intent
        val stringExtra = intent.getStringExtra("videoTitle")
        val stringExtra3 = intent.getStringExtra("videoCategory")
        val stringExtra6 = intent.getStringExtra("videoImage")
        val stringExtra7 = intent.getStringExtra("videoView")

        Log.e("check_image_search", "//////////////////////////////" + stringExtra7)

        tf_video_title?.text = stringExtra
        tv_video_subcat?.text = stringExtra3
        val trackSelectorDef: TrackSelector = DefaultTrackSelector()
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelectorDef) //creating a player instance

        val userAgent = Util.getUserAgent(this, this.getString(R.string.app_name))
        val defdataSourceFactory = DefaultDataSourceFactory(this, userAgent)
        val uriOfContentUrl = Uri.parse(Config.BASE_URL + stringExtra7)
        val mediaSource: MediaSource = ExtractorMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl) // creating a media source
        playerView?.useController = true
        playerView?.requestFocus()
        player?.prepare(mediaSource, false, false)
        player?.playWhenReady = true // start loading video and play it at the moment a chunk of it is available offline
        player?.volume = 7f
        playerView!!.player = player

        btn_download?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //        this.videoPath = this.videoPath.replace(" ", "%20");
//                videoPath ? = Config.BASE_URL + str_video.video_name
                list.clear()
                val request = DownloadManager.Request(Uri.parse(Config.BASE_URL + stringExtra7))
                request.setAllowedNetworkTypes(3)
                request.setAllowedOverRoaming(false)
                request.setTitle(resources.getString(R.string.downloading) + stringExtra)
                request.setVisibleInDownloadsUi(true)
                request.allowScanningByMediaScanner()
                request.setDestinationInExternalPublicDir(getString(R.string.destinationFolderName), stringExtra7)
                val enqueue: Long = downloadManager!!.enqueue(request)
                Log.e("OUT", "" + enqueue)
                list.add(java.lang.Long.valueOf(enqueue))
                setupProgress(java.lang.Long.valueOf(enqueue))
            }

        })

        btn_download?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //        this.videoPath = this.videoPath.replace(" ", "%20");
//                videoPath ? = Config.BASE_URL + str_video.video_name
//
//                list.clear()
                val request = DownloadManager.Request(Uri.parse(Config.BASE_URL + stringExtra7))
//                request.setAllowedNetworkTypes(3)
//                request.setAllowedOverRoaming(false)
//                request.setTitle(resources.getString(R.string.downloading) + stringExtra)
//                request.setVisibleInDownloadsUi(true)
//                request.allowScanningByMediaScanner()
//
//                request.setDestinationInExternalPublicDir(getString(R.string.destinationFolderName), stringExtra7)
//                val enqueue: Long = downloadManager!!.enqueue(request)
//                Log.e("OUT", "" + enqueue)
//                list.add(java.lang.Long.valueOf(enqueue))
//                setupProgress(java.lang.Long.valueOf(enqueue))
                val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VideoStatus/status.mp4")
                val downloadmanager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val uri = Uri.parse(Config.BASE_URL + stringExtra7)
//                Toast.makeText(this, "Downloading", Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext,"Downloading",Toast.LENGTH_SHORT).show();
                request.setTitle(stringExtra7)
                request.setDescription("Downloading")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setVisibleInDownloadsUi(false)
                val urddi = Uri.fromFile(outputFile)
                request.setDestinationUri(urddi)
                downloadmanager.enqueue(request)
            }

        })

        btn_share?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                shareToDefault()


            }

            private fun shareToDefault() {

//                str_video = getIntent().getSerializableExtra("videoserilizeable") as Videocategory
                var outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "myfile.mp4")

                outputFile.mkdir()
                try {
                    outputFile.createNewFile()
                    if (!outputFile.exists()) {
                        val downloadmanager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        val uri = Uri.parse(Config.BASE_URL + stringExtra7)
                        val request = DownloadManager.Request(uri)
                        request.setTitle("My File")
                        request.setDescription("Downloading")
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setVisibleInDownloadsUi(false)
                        val urddi = Uri.fromFile(outputFile)
                        request.setDestinationUri(urddi)
                        downloadmanager.enqueue(request)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val intent = Intent("android.intent.action.SEND")
                intent.type = "video/*"
                intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this@SubCatogory, getString(R.string.authority), outputFile))
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                    startActivity(Intent.createChooser(intent, "Share to"))
                    return
                }
//                Toast.makeText(this, "try_later", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun setupProgress(l: Long) {
        Thread {
            var z = true
            while (z) {
                val query = DownloadManager.Query()
                query.setFilterById(l)
                try {
                    val query2: Cursor ? = this@SubCatogory.downloadManager?.query(query)
                    if (query2 == null || query2.count == 0) {
                        this@SubCatogory.runOnUiThread(Runnable {
                            rel_showProgress?.setVisibility(View.VISIBLE)
                            detail_toolbar?.setVisibility(View.INVISIBLE)
                        })
                        z = false
                    } else {
                        query2.moveToFirst()
                        val i = query2.getInt(query2.getColumnIndex("bytes_so_far"))
                        val i2 = query2.getInt(query2.getColumnIndex("total_size"))
                        if (query2.getInt(query2.getColumnIndex("status")) == 16) {
                            this@SubCatogory.runOnUiThread(Runnable {
                                rel_showProgress?.setVisibility(View.VISIBLE)
                                detail_toolbar?.setVisibility(View.INVISIBLE)
                            })
                            z = false
                        }
                        val d = i.toDouble()
                        java.lang.Double.isNaN(d)
                        val d2 = i2.toDouble()
                        java.lang.Double.isNaN(d2)
                        val d3 = d * 100.0 / d2
                        val str: String = this@SubCatogory.getResources().getString(R.string.downloading).toString() + " " + d3.toInt() + "%"
                        this@SubCatogory.runOnUiThread(Runnable {
                            rel_showProgress?.setVisibility(View.VISIBLE)
                            detail_toolbar?.setVisibility(View.VISIBLE)
                            txt_progress?.setText(str)
                            val format = String.format(Locale.ENGLISH, "%.2f", java.lang.Float.valueOf(i2.toFloat() / 1048576.0f))
                            txt_fileSize?.setText("$format MB")
                            if (Build.VERSION.SDK_INT >= 24) {
                                progressBar?.setProgress(d3.toInt(), true)
                            } else {
                                progressBar?.setProgress(d3.toInt())
                            }
                        })
                        if (query2.getInt(query2.getColumnIndex("status")) == 8) {
                            this@SubCatogory.runOnUiThread(Runnable {
                                rel_showProgress?.setVisibility(View.INVISIBLE)
                                detail_toolbar?.setVisibility(View.INVISIBLE)

//                                this@SubCatogory.increaseDownload()
//                                this@SubCatogory.saveToDatabase()
                                this@SubCatogory.changeBar()
                            })
                            z = false
                        }
                        query2.close()
                    }
                } catch (e: CursorIndexOutOfBoundsException) {
                    e.printStackTrace()
                    this@SubCatogory.runOnUiThread(Runnable {
                        rel_showProgress?.setVisibility(View.INVISIBLE)
                        detail_toolbar?.setVisibility(View.VISIBLE)
                    })
                    throw e
                }
            }
        }.start()
    }

    fun changeBar() {
        btn_download!!.setImageResource(R.drawable.icon_download_on)


//        if (Boolean.valueOf(hasCurrentVideo()).booleanValue()) {
//            this.btn_download.setImageResource(R.drawable.icon_donwload_off);
//        } else {
//            this.btn_download.setImageResource(R.drawable.icon_download_on);
//        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}