package com.example.a82105.healthcare

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.R.attr.data
import kotlinx.android.synthetic.main.introduce_popup.*


class MainActivity : AppCompatActivity(){

    private val REQUEST_IMAGE_CAPTURE = 672
    private var imageFilePath: String? = null
    private var photoUri: Uri? = null
    private val flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TAG","onCreate")
        super.onCreate(savedInstanceState)

        //권환체크
        TedPermission.with(applicationContext)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check()

        setContentView(R.layout.activity_main)

        btn_capture.setOnClickListener()
        {
            startActivityForResult(Intent(this, IntroducePopup::class.java),1202)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "TEST_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )
        imageFilePath = image.absolutePath
        return image
    }
    override fun onPause() {
        super.onPause()
        Log.d("TAG","onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG1","onResume")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("TAG1","onActivityResult")
        if (requestCode == 1202 && resultCode == Activity.RESULT_OK ){
            if(data?.getStringExtra("flag")=="true")
            {
                Log.d("TAG1","팝업창 학인 버튼을 눌른 후")

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null) {

                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (e: IOException) {

                    }

                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(applicationContext,packageName, photoFile)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
            else
            {
                Log.d("TAG1","닫기버튼을 눌러 돌아옴")
            }
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d("TAG1","카메라 촬영 후")
            val bitmap = BitmapFactory.decodeFile(imageFilePath)
            var exif: ExifInterface? = null

            try {
                exif = ExifInterface(imageFilePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val exifOrientation: Int
            val exifDegree: Int

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                exifDegree = exifOrientationToDegress(exifOrientation)
            } else {
                exifDegree = 0
            }
            iv_result.setImageBitmap(rotate(bitmap, exifDegree.toFloat()))
        }
        else{
            Log.d("TAG1","이게 뭐람..")
        }
    }

    private fun exifOrientationToDegress(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }
    private fun rotate(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(applicationContext, "권한이 허용됨", Toast.LENGTH_SHORT).show()
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
            Toast.makeText(applicationContext, "권한이 거부됨", Toast.LENGTH_SHORT).show()
        }
    }
}



