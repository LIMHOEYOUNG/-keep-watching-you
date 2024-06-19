package com.example.drowsy_pro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drowsy_pro.databinding.LoginPageBinding
import com.example.drowsy_pro.login.loginApi
import com.example.drowsy_pro.login.logindata
import com.example.drowsy_pro.login.resultlogin
import com.example.drowsy_pro.login.putokenApi
import com.example.drowsy_pro.login.tokenjwp
import com.example.drowsy_pro.login.tokensuccess
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class loginpage : AppCompatActivity() {
    private lateinit var binding: LoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val userId = binding.editId.text.toString()
            val password = binding.editPw.text.toString()

            // Retrofit을 이용한 로그인 요청
            login(userId, password)
        }
    }
    //로그인
    private fun login(username: String, password: String) {
        val loginApi = loginApi.create()
        val loginCall = loginApi.login(logindata(username, password))

        loginCall.enqueue(object : Callback<resultlogin> {
            override fun onResponse(call: Call<resultlogin>, response: Response<resultlogin>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        //로그인 성공 시 메인페이지로
                        if(it.success==1){
                            Log.d("Login Success", "Token: ${it.logintoken}")
                            val sharedPreferences = getSharedPreferences("TokenPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token", it.logintoken)
                            editor.apply()
                            pushtoken()
                            val intent = Intent(this@loginpage, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Log.d("Login wrong", "id or passwd wrong.")
                        }
                    }
                } else {
                    Log.d("Login Failed", "Response: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<resultlogin>, t: Throwable) {
                Log.e("Login Error", "Error: ${t.message}")
            }
        })
    }
    //로그인 시 서버에 fcm토큰 넣기
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
                        Toast.makeText(this@loginpage, "Failed to fetch data: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<tokensuccess>, t: Throwable) {
                    Toast.makeText(this@loginpage, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        })

    }
}
