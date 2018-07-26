package com.reed.leif

import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import java.util.*
import android.support.v4.content.FileProvider
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private var photoOutputPath : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView.setOnTouchListener(::imagePreviewTouchListener)
        dot.position = PointF(mapView.width/2f, mapView.height/2f)
        takePhoto()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val photoUri = Uri.fromFile(File(photoOutputPath))
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            mapView.setImageBitmap(bitmap)
        }
    }

    private fun takePhoto() {
        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(photoIntent.resolveActivity(packageManager) != null) {
            // Right now, the images are being stored to app-local storage
            // It would be better to just stream the data into memory,
            // But Android prides itself on making things difficult
            val photoFile = createImageFile()
            val photoURI = FileProvider.getUriForFile(this,
                    "com.reed.leif.fileprovider",
                    photoFile)
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile =  File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
        photoOutputPath = photoFile.absolutePath
        return photoFile
    }

    private fun imagePreviewTouchListener(view : View, motionEvent: MotionEvent) : Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> dot.position = PointF(motionEvent.x, motionEvent.y)
        }
        return true
    }

    // Invoked when complete_calibrate_button is pressed
    fun completeCalibration(view: View?) {
        val intent = Intent(this, NavigationActivity::class.java)
        intent.putExtra(CALIBRATION_LOCATION, dot.position)
        intent.putExtra(PHOTO_PATH, photoOutputPath)
        startActivity(intent)
    }
}
