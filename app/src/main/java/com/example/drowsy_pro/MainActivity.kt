package com.example.drowsy_pro

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.drowsy_pro.databinding.ActivityMainBinding
import com.example.drowsy_pro.login.putokenApi
import com.example.drowsy_pro.login.tokenjwp
import com.example.drowsy_pro.login.tokensuccess
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "drowsylog"
    private val PERMISSION_REQUEST_CODE = 5000
    //push알림 권한 요청
    private fun permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionCheck()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // FCM 등록 토큰 가져오기
            val token = task.result

            val msg = "FCM Registration token: " + token;
            Log.d(TAG, "$msg")
            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
        checkIntent(intent)
        val pref= getSharedPreferences("TokenPrefs",MODE_PRIVATE)
        var logintoken=pref.getString("token","Null")
        Log.d("Logintoken","${logintoken}")
        checkjwt()
        pushtoken()

        binding.goMypage.setOnClickListener {
           // val intent = Intent(this, mypage::class.java)
           // startActivity(intent)
        }
        binding.goLocationcar.setOnClickListener {
            val intent = Intent(this, locationcar::class.java)
            startActivity(intent)
        }
        binding.goDrowsynotification.setOnClickListener {
            val intent = Intent(this, drowsynotification::class.java)
            startActivity(intent)
        }
        binding.goUnperson.setOnClickListener {
            val intent = Intent(this, unpersonrecord::class.java)
            startActivity(intent)
        }
        binding.goDrivingrecord.setOnClickListener {
            val intent = Intent(this, drivingrecord::class.java)
            startActivity(intent)
        }
        binding.goAccident.setOnClickListener {
            val intent = Intent(this, accidentrecord::class.java)
            startActivity(intent)
        }
        binding.goDrowsyrecord.setOnClickListener {
            val intent = Intent(this, drowsyrecord::class.java)
            startActivity(intent)
        }
        binding.goLogout.setOnClickListener {
            logout()
        }
    }
    //push알림 오면 이동
    private fun checkIntent(intent: Intent?) {
        intent?.let {
            val clickAction = it.getStringExtra("click_action")
            if (!clickAction.isNullOrBlank()) {
                // 원하는 액티비티로 리디렉션
                val redirectIntent = Intent(clickAction).also { redirectIntent ->
                    redirectIntent.setPackage(this.packageName)
                }
                startActivity(redirectIntent)
                finish()
            }
        }
    }
    //권한 요청후 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "push알림을 허가 후 사용해주세요.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "push 알림 허가", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    //로그아웃
    private fun logout(){
        val pref = getSharedPreferences("TokenPrefs",MODE_PRIVATE)
        val editor = pref.edit()
        val logintoken=pref.getString("token","Null")
        val requestData = tokenjwp(jwt = logintoken?:"Null", apptoken = "NULL")
        putokenApi.create().getokenApi(requestData).enqueue(object : Callback<tokensuccess> {
            override fun onResponse(call: Call<tokensuccess>, response: Response<tokensuccess>) {
                if (response.isSuccessful) {
                    val successValue = response.body()?.success
                    Log.d("TokenSuccess", "Success value: $successValue")
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch data: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<tokensuccess>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
        editor.remove("token")
        editor.apply()

        Log.d("delete_Logintoken","${logintoken}")
        val intent = Intent(this, loginpage::class.java)
        startActivity(intent)
    }
    //로그인 안되있으면 로그인 페이지로
    private fun checkjwt(){
        val pref = getSharedPreferences("TokenPrefs",MODE_PRIVATE)
        val userjwt = pref.getString("token","NULL")
        Log.d("Logintoken fun","${userjwt}")
        if(userjwt == "NULL" || userjwt == null){
            val intent = Intent(this, loginpage::class.java)
            Log.d("Logintoken fun2","${userjwt}")
            startActivity(intent)
        }
    }
    //서버에 토큰 넣기
    private fun pushtoken(){
        var token:String?
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            token = task.result
            val pref = getSharedPreferences("TokenPrefs",MODE_PRIVATE)
            var logintoken=pref.getString("token","Null")
            val requestData = tokenjwp(jwt = logintoken?:"Null", apptoken = token?:"Null")
            putokenApi.create().getokenApi(requestData).enqueue(object : Callback<tokensuccess> {
                override fun onResponse(call: Call<tokensuccess>, response: Response<tokensuccess>) {
                    if (response.isSuccessful) {
                        val successValue = response.body()?.success
                        Log.d("TokenSuccess", "Success value: $successValue")
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to fetch data: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<tokensuccess>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        })

    }
}
