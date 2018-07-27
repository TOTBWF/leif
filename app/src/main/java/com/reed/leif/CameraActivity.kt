package com.reed.leif

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.View
import com.google.android.cameraview.CameraView
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.lang.Long.signum
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    private var backgroundThread : HandlerThread? = null
    private var backroundHander : Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraButton.setOnClickListener { _ -> cameraView.takePicture() }
        cameraView.addCallback(cameraCallback)
    }

    override fun onResume() {
        super.onResume()
        requestPermission()
        cameraView.start()
        startBackgroundThread()
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
        stopBackgroundThead()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraView.stop()
        stopBackgroundThead()
    }

    private fun requestPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            return
        } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            TODO("Create a custom dialog fragment")
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_ACCESS)
        }
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backroundHander = Handler(backgroundThread?.looper)
    }

    private fun stopBackgroundThead() {
        backgroundThread?.quitSafely()
        backgroundThread?.join()
        backgroundThread = null
        backroundHander = null
    }

    private val cameraCallback = object : CameraView.Callback() {
        override fun onCameraOpened(cameraView: CameraView?) {
            super.onCameraOpened(cameraView)
            Log.d(TAG, "Opening Camera")
        }

        override fun onCameraClosed(cameraView: CameraView?) {
            super.onCameraClosed(cameraView)
            Log.d(TAG, "Closing Camera")
        }

        override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {
            backroundHander?.post{
                val file = createImageFile()
                val outputStream = file.outputStream()
                outputStream.write(data)
                outputStream.close()
                launchCalibrationActivity(file.absolutePath)
            }
        }
    }

    private fun launchCalibrationActivity(photoPath: String) {
        val intent = Intent(this, CalibrationActivity::class.java)
        intent.putExtra(PHOTO_PATH, photoPath)
        startActivity(intent)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile( imageFileName, ".jpg", storageDir)
    }



}
