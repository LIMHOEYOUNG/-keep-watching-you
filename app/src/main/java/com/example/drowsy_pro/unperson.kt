package com.example.drowsy_pro

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.drowsy_pro.databinding.UnpersonPageBinding
import com.example.drowsy_pro.unpersondata.putunperson
import com.example.drowsy_pro.unpersonlog.ApiServiceBuilder.apiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.io.IOException

class unperson : AppCompatActivity() {
    private lateinit var binding: UnpersonPageBinding
    private var unpersontime: String? = null
    private var unpersonla: Float? = null
    private var unpersonlo: Float? = null
    private var unpersonid: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UnpersonPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unpersontime = intent.getStringExtra("UNPERSON_TIME")
        unpersonla = intent.getFloatExtra("UNPERSONLA", 0F)
        unpersonlo = intent.getFloatExtra("UNPERSONLO", 0F)
        unpersonid = intent.getIntExtra("ITEM_ID", 0)

        binding.unpoersonDate.text = unpersontime

        // Retrofit을 사용하여 이미지 요청
        val pref = getSharedPreferences("TokenPrefs", MODE_PRIVATE)
        val logintoken = pref.getString("token", "Null")
        val requestData = putunperson(jwt = logintoken ?: "Null", id = unpersonid ?: 0)
        val call = apiService.getunperson(requestData)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        // 응답 바디에서 바이트 배열로 이미지 변환
                        val imageBytes = responseBody.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        // ImageView에 이미지 설정
                        binding.unpersonIamge.setImageBitmap(bitmap)

                        // 이미지를 로컬 파일로 저장
                        saveImageToInternalStorage(bitmap, "downloaded_image.png")
                    }
                } else {
                    println("응답 코드: ${response.code()}")
                    println("응답 바디: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("요청 실패: ${t.message}")
            }
        })

        binding.goHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.imgStore.setOnClickListener {
            binding.unpersonIamge.drawable?.let {
                val bitmap = (it as BitmapDrawable).bitmap
                if (saveImageOnAboveAndroidQ(bitmap)) {
                    Log.d("image","이미지 로컬저장")
                }
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap, fileName: String) {
        try {
            openFileOutput(fileName, MODE_PRIVATE).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.close()
                Log.d("image","이미지 내부저장")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("image","${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageOnAboveAndroidQ(bitmap: Bitmap): Boolean {
        val fileName = unpersontime.toString() + ".png" // 파일이름 현재시간.png

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave") // 경로 설정
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName) // 파일이름을 put해준다.
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1) // 현재 is_pending 상태임을 만들어준다.
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if (uri != null) {
                contentResolver.openFileDescriptor(uri, "w", null)?.use { pfd ->
                    FileOutputStream(pfd.fileDescriptor).use { fos ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        fos.close()
                    }
                }

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0) // 저장소 독점을 해제한다.
                contentResolver.update(uri, contentValues, null, null)

                return true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}
