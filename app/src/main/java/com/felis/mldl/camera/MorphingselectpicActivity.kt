package com.felis.mldl.camera

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.felis.mldl.databinding.MorphingselectpicActivityBinding
import com.felis.mldl.main.MainActivity
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
import java.util.*

class MorphingselectpicActivity : AppCompatActivity() {

    private lateinit var binding: MorphingselectpicActivityBinding

    //    private var imageView: ImageView? = null
    private var selectedImage: Uri? = null
    private val Gallery = 1
    lateinit var filePath: String // 문자열 형태의 사진 경로 값(초기 값을 null로 시작하고 싶을 때)
    var value = ""
    var btnchocie = ""
    var camera = ""
    var result = "shit"
    lateinit var pictureValue: ByteArray


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MorphingselectpicActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val intent = Intent(this, MorphinguserpicActivity::class.java)
        val intent2 = Intent(this, MainActivity::class.java)
        supportActionBar?.hide()

        println(camera)
        println("카메라뭐임??")

        setPermission()

        // 한번에 동작시키기 위해서 무한 루프를 준것이다.
        // 저장되어 있는 사진을 업로드 하기 위해 위치로 들어가고 사진을 선택하기 까지
        // 시간은 계속 흐르고 있고 ImageView에 사진이 나와야만 다음 조건으로 넘어간다.
        binding.galleryButton.setOnClickListener {
            Thread {
                btnchocie = "버튼2" // 이미지 업로드 버튼을 눌렀을 때
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

        binding.nextbtn.setOnClickListener {
            startActivity(intent)
        }

        binding.backbtn.setOnClickListener {
            startActivity(intent2)
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
        if (btnchocie == "버튼2") {
            if (requestCode == Gallery && resultCode == RESULT_OK && data != null) {
                selectedImage = data.getData()

                //여기서부터
                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.selectImageview.setImageBitmap(bitmap)
                //여기까지는 이미지를 갤러리에서 클릭해서 그 사진을 이미지뷰에 보여주는 코드

                // 나중에 해당 코드를 다시 공부해본다.
                // 현재로서는 완전하게 이해가 되지는 않음
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
                Log.e("tag", "절대 " + result)

                //커서 사용해서 경로 확인
            } else {
                Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_LONG).show()
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
        resized.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteArray = stream.toByteArray()
        pictureValue = byteArray


        // 비트맵 파일을 jpeg 파일로 바꿔주는 방법
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
                Log.e("saveBitmapToJpg", "FileNotFoundException : " + e.message)
            } catch (e: IOException) {
                Log.e("saveBitmapToJpg", "IOException : " + e.message)
            }
            Log.d("imgPath", getCacheDir().toString() + "/" + fileName)
            return getCacheDir().toString() + "/" + fileName
        }

        val imageFile = File(saveBitmapToJpg(resized, "selectpic"))
        println(imageFile.toString())
        println("이미지 파일 알려줘라")

        // 장고 서버를 위한 retrofit을 생성한다.
        val retrofit = Retrofit.Builder()
            .baseUrl(CameraService.DJANGO_SITE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //  서버 통신을 위해 필요한 RequestBody, Retrofit, multiPartBody, Call
        val postApi: CameraService = retrofit.create(CameraService::class.java)

        //  requestBody는 클라이언트가 전송하는 Json(application/json) 형태의 HTTP Body 내용을 Java Object로 변환시켜주는 역할을 한다.

        val requestBody: RequestBody =
            RequestBody.create("multipart/data".toMediaTypeOrNull(), imageFile) //추측 : 서버 디렉토리 경로설정?

        val multiPartBody: MultipartBody.Part = MultipartBody.Part
            .createFormData("model_pic", imageFile.name, requestBody)

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

    // 테드 퍼미션 설정
    private fun setPermission() {
        val permission = object : PermissionListener {
            override fun onPermissionGranted() { // 설정해놓은 위험권한들이 허용 되었을 경우 이 곳을 수행함.
//                    Toast.makeText(this@CameraActivity, "권한이 허용 되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { // 설정해놓은 위험권한들 중 거부를 한 경우 이곳을 수행함.
//                    Toast.makeText(this@CameraActivity, "권한이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("카메라 앱을 사용하시려면 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다. [앱 설정] -> [권한] 항목에서 허용해주세요/")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
            .check()
    }

    // 갤러리에 저장
//    private fun savePhoto(bitmap: Bitmap) {
////        val absolutePath = "/storage/emulated/0/"
////        val folderPath = "$absolutePath/Pictures/"
//        val folderPath =
//            Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" // 사진폴더로 저장하기 위한 경로 선언
//
//        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val filename = "${timestamp}.jpeg"
//        val folder = File(folderPath)
//        if (!folder.isDirectory) { // 현재 해당 경로에 폴더가 존재하는지 검사
//            folder.mkdirs() // make directory 줄임말로 해당 경로에 폴더 자동으로 새로 만들기.
//        }
//
//        // 실제적인 저장처리
//        val out = FileOutputStream(folderPath + filename)
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//        sendBroadcast(
//            Intent(
//                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                Uri.parse("file://$folderPath$filename")
//            )
//        )
//    }
//
//    // 촬영할 때 이미지 회전되는 것을 방지하기 위해 사용
//    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
//        val matrix = Matrix()
//        matrix.postRotate(angle)
//        return Bitmap.createBitmap(
//            source, 0, 0, source.width, source.height,
//            matrix, true
//        )
//    }
}