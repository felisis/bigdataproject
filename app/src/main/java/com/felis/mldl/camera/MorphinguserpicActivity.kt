package com.felis.mldl.camera


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.felis.mldl.databinding.MorphinguserpicActivityBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MorphinguserpicActivity : AppCompatActivity() {

    private lateinit var binding: MorphinguserpicActivityBinding

    private var selectedImage: Uri? = null
    val REQUEST_IMAGE_CAPTURE = 1 // 카메라 사진 촬영 요청코드
    lateinit var filePath: String // 문자열 형태의 사진 경로 값(초기 값을 null로 시작하고 싶을 때)
    private val Gallery = 1
    var value = ""
    var btnchoice = ""
    var camera = ""
    var result = "shit"
    lateinit var pictureValue: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MorphinguserpicActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        println(camera)
        println("카메라뭐임??")

        setPermission()

        val intent = Intent(this, LoadingActivity::class.java)
        val intent2 = Intent(this, MorphingselectpicActivity::class.java)

        binding.nextbtn.setOnClickListener {
            startActivity(intent)
        }

        binding.backbtn.setOnClickListener {
            startActivity(intent2)
        }

        // 한번에 동작시키기 위해서 무한 루프를 준것이다.
        // 저장되어 있는 사진을 업로드 하기 위해 위치로 들어가고 사진을 선택하기 까지
        // 시간은 계속 흐르고 있고 ImageView에 사진이 나와야만 다음 조건으로 넘어간다.
        binding.cameraButton.setOnClickListener {
            Thread {
                btnchoice = "버튼1" // 이미지 업로드 버튼을 눌렀을 때
                takeCapture()
                var running = true
                while (running) {
                    if (result == "shit") {
                    } else {
                        uploadPhoto()

                        result = "shit"
                        running = false
                    }
                }
            }.start()
        }

        // 한번에 동작시키기 위해서 무한 루프를 준것이다.
        // 저장되어 있는 사진을 업로드 하기 위해 위치로 들어가고 사진을 선택하기 까지
        // 시간은 계속 흐르고 있고 ImageView에 사진이 나와야만 다음 조건으로 넘어간다.
        binding.galleryButton.setOnClickListener {
            Thread {
                btnchoice = "버튼2" // 이미지 업로드 버튼을 눌렀을 때
                openImage()
                var running = true
                while (running) {
                    if (result == "shit") {
                    } else {
                        uploadPhoto()

                        result = "shit"
                        running = false
                    }
                }
            }.start()
        }


    }

    // 미디어 파일에서 이미지를 열 수 있는 상당히 간단한 코드
    private fun openImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, Gallery)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (btnchoice == "버튼1") {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                // 사진의 경로가 있어야만 파일 형태로 저장이 된다.

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 카메라 실행 부분
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(cameraIntent, 1)
                    } else {
                        Log.d("test", "권한 설정 요청")
                        ActivityCompat.requestPermissions(
                            this@MorphinguserpicActivity,
                            arrayOf<String?>(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            1
                        )
                    }
                }
                val bitmap = BitmapFactory.decodeFile(filePath)
                lateinit var exif: ExifInterface

                try {
                    exif = ExifInterface(filePath)
                    var exifOrientation = 0
                    var exifDegree = 0

                    if (exif != null) {
                        exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                        exifDegree = exifOrientationToDegress(exifOrientation)
                    }

                    binding.userImageView.setImageBitmap(rotateImage(bitmap, exifDegree))
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                savePhoto(bitmap)
            }
        } else if (btnchoice == "버튼2") {
            if (requestCode == Gallery && resultCode == RESULT_OK && data != null) {
                selectedImage = data.getData()


                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.userImageView.setImageBitmap(bitmap)
//                selectedImage = data.getData()
                //여기까지는 이미지를 갤러리에서 클릭해서 그 사진을 이미지뷰에 보여주는 코드

                // 나중에 해당 코드를 다시 공부해본다.
                // 갤러리에서 사진을 가져오는코드
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                var cursor: Cursor = selectedImage?.let {
                    contentResolver?.query(
                        it,
                        filePathColumn,
                        null,
                        null,
                        null
                    )
                }!!

                if (cursor == null) {
                    selectedImage?.path.toString()
                } else {
                    cursor.moveToFirst()
                    var idx = cursor.getColumnIndex(filePathColumn[0])
                    result = cursor.getString(idx)
                    cursor.close()
                }


            } else {
                Toast.makeText(this, "사진 업로드 실패, 다시 시도해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadPhoto() {
        //커서 사용해서 경로 확인
        val imagePath = result
        println(imagePath)
        println("이미지 경로 알려주셈")

        // 이미지를 다시 줄이고 퀄리트를 높여 Stream 형태에서 BiteArray 형태로 만들어준다.
        var src: Bitmap = BitmapFactory.decodeFile(imagePath)
        var resized: Bitmap = Bitmap.createScaledBitmap(src, 1024, 1024, true)
        val stream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        val byteArray = stream.toByteArray()
        pictureValue = byteArray

        // 비트맵 파일을 JPG 파일로 바꿔주는 방법
        fun saveBitmapToJpg(bitmap: Bitmap, name: String): String {
            val storage: File = getCacheDir() //  path = /data/user/0/YOUR_PACKAGE_NAME/cache
            val fileName = "$name.jpeg"
            val imgFile = File(storage, fileName)
            try {
                imgFile.createNewFile()
                val out = FileOutputStream(imgFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) //퀄리티를 최대로
                out.close()
            } catch (e: FileNotFoundException) {
                Log.e("saveBitmapToJpeg", "FileNotFoundException : " + e.message)
            } catch (e: IOException) {
                Log.e("saveBitmapToJpeg", "IOException : " + e.message)
            }
            Log.d("imgPath", getCacheDir().toString() + "/" + fileName)
            return getCacheDir().toString() + "/" + fileName
        }

        val imageFile = File(saveBitmapToJpg(resized, "userpic"))

        println(imageFile.toString())
        println("이미지 파일 알려줘라")

        // 장고 서버를 위한 retrofit을 생성한다.
        val retrofit = Retrofit.Builder()
            .baseUrl(UserService.DJANGO_SITE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        println("이미지 파일 알려줘라")

        // 서버 통신을 위해 필요한 RequestBody, Retrofit, multiPartBody, Call
        val postApi: UserService = retrofit.create(UserService::class.java)
        val requestBody: RequestBody =
            RequestBody.create("multipart/data".toMediaTypeOrNull(), imageFile)
        val multiPartBody: MultipartBody.Part =
            MultipartBody.Part.createFormData("model_pic", imageFile.name, requestBody)
        val call: Call<RequestBody?>? = postApi.uploadFile(multiPartBody)
        call?.enqueue(object : Callback<RequestBody?> {
            override fun onResponse(call: Call<RequestBody?>?, response: Response<RequestBody?>?) {
                Log.d("good", "good")
                println(requestBody)
            }

            override fun onFailure(call: Call<RequestBody?>?, t: Throwable?) {
                Log.d("fail", "fail")
            }
        })
    }

    // 이미지 파일 생성
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpeg",
            storageDir
        ).apply {
            filePath = absolutePath
        }
    }

    fun takeCapture() {
        // 기본 카메라 앱 실행
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.felis.mldl.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    try {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    } catch (e: ActivityNotFoundException) {
                        // display error state to the user
                    }
                }
            }
        }
    }

    //회전된 만큼 더 회전시켜 정방향 세팅
    private fun exifOrientationToDegress(exifOrientation: Int): Int {
        when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                Log.d("rotate", "rotate90")
                return 90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                Log.d("rotate", "rotate180")
                return 180
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                Log.d("rotate", "rotate270")
                return 270
            }
            else -> {
                Log.d("rotate", "rotate0")
                return 0
            }

        }
    }

    // 촬영할 때 이미지 회전되는 것을 방지하기 위해 사용
    fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap {
        Log.d("rotate", "init rotate")
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // 테드 퍼미션 설정
    private fun setPermission() {
        var permis = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@MorphinguserpicActivity, "권한 허가", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MorphinguserpicActivity, "권한 거부", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.finishAffinity(this@MorphinguserpicActivity) // 권한 거부시 앱 종료
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permis)
            .setRationaleMessage("카메라 사진 권한 필요")
            .setDeniedMessage("카메라 권한 요청 거부")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
            .check()
    }

    // 갤러리에 저장
    private fun savePhoto(bitmap: Bitmap) {
//        val absolutePath = "/storage/emulated/0/"
//        val folderPath = "$absolutePath/Pictures/"
        val folderPath =
            Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" // 사진폴더로 저장하기 위한 경로 선언

        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val filename = "${timestamp}.jpeg"
        val folder = File(folderPath)
        if (!folder.isDirectory) { // 현재 해당 경로에 폴더가 존재하는지 검사
            folder.mkdirs() // make directory 줄임말로 해당 경로에 폴더 자동으로 새로 만들기.
        }
        // 실제적인 저장처리
        val out = FileOutputStream(folderPath + filename)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://$folderPath$filename")
            )
        )
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }
}

