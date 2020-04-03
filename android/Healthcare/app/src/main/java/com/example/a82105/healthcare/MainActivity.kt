package com.example.a82105.healthcare

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.a82105.healthcare.R.id.iv_result
import com.google.gson.GsonBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 672
    private var imageFilePath: String? = null
    private var photoUri: Uri? = null
    private val flag = false
    var tempFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TAG", "onCreate")
        super.onCreate(savedInstanceState)


        //권환체크
        TedPermission.with(applicationContext)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check()

        setContentView(R.layout.activity_main)

        //사진촬영
        btn_capture.setOnClickListener()
        {
            startActivityForResult(Intent(this, IntroducePopup::class.java), 1202)
        }
        open.setOnClickListener()
        {
            Log.d("TAG1", "갤러리 오픈")
            selectGallery()
        }
        send.setOnClickListener()
        {
            Log.d("TAG1", "전송")
            testRetrofit(imageFilePath)

        }
    }
    fun testRetrofit(path : String?)
    {
        Log.d("TAG1","경로 : "+path)
        val file=File(path)
        var fileName=path?.substring(path.lastIndexOf("/")+1)
        var requestbody= RequestBody.create(MediaType.parse("image/*"),file)
        var body=MultipartBody.Part.createFormData("uploaded_file",fileName,requestbody)
        Log.d("TAG1","파일 : "+file)
        Log.d("TAG1","파일명 : "+fileName)


        var gson = GsonBuilder()
                .setLenient()
                .create()
        Log.d("TAG1","gson객체생성")

        var retrofit=
                Retrofit.Builder()
                        .baseUrl("http://52.71.193.244")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()

        Log.d("TAG1","레트로핏객체 생성")

        var server = retrofit.create(retrofit_interface::class.java)
        Log.d("TAG1","인터페이스 생성")

        server.post_request(body).enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {

            }
        })

    }
       private fun getByte(num: Int): ByteArray {
            val buf = ByteArray(4)
            buf[0] = (num.ushr(24) and 0xFF).toByte()
            buf[1] = (num.ushr(16) and 0xFF).toByte()
            buf[2] = (num.ushr(8) and 0xFF).toByte()
            buf[3] = (num.ushr(0) and 0xFF).toByte()

            return buf
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
            Log.d("TAG", "onPause")
        }

        override fun onResume() {
            super.onResume()
            Log.d("TAG1", "onResume")
        }



        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            Log.d("TAG1", "onActivityResult")
            if (requestCode == 1202 && resultCode == Activity.RESULT_OK) {
                if (data?.getStringExtra("flag") == "true") {
                    Log.d("TAG1", "팝업창 학인 버튼을 눌른 후")

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(packageManager) != null) {

                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                            tempFile=photoFile
                        } catch (e: IOException) {

                        }

                        if (photoFile != null) {
                            photoUri = FileProvider.getUriForFile(applicationContext, packageName, photoFile)

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                } else {
                    Log.d("TAG1", "닫기버튼을 눌러 돌아옴")
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                Log.d("TAG1", "카메라 촬영 후")
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
            else if(requestCode==1207 && resultCode == Activity.RESULT_OK)
            {

                val bitmap=MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
                iv_result.setImageBitmap(bitmap)
            }

            else {
                Log.d("TAG1", "이게 뭐람..")
            }
        }

        private fun selectGallery(){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent,1207)
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



