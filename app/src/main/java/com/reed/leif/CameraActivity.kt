package com.reed.leif

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.cameraview.CameraView
import com.reed.leif.util.InjectorUtils
import com.reed.leif.util.REQUEST_CAMERA_ACCESS
import com.reed.leif.util.SESSION_ID
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    private var backgroundThread : HandlerThread? = null
    private var backroundHander : Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val cameraViewModel = InjectorUtils.getCameraViewModel(this)

        val cameraCallback = object: CameraView.Callback() {
            override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {
                backroundHander?.post{
                    val file = createImageFile()
                    val outputStream = file.outputStream()
                    outputStream.write(data)
                    outputStream.close()
                    val sessionId = cameraViewModel.createSession(file.absolutePath)
                    launchCalibrationActivity(sessionId)
                }
            }

        }
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

    override fun onStop() {
        super.onStop()
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

    private fun launchCalibrationActivity(sessionId: Long) {
        val intent = Intent(this, CalibrationActivity::class.java)
        Log.d(com.reed.leif.util.TAG, "Created session $sessionId")
        intent.putExtra(SESSION_ID, sessionId)
        startActivity(intent)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile( imageFileName, ".jpg", storageDir)
    }
}
