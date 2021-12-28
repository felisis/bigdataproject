package com.felis.mldl.register

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.felis.mldl.activity.WelcomeActivity
import com.felis.mldl.R
import com.felis.mldl.databinding.RegisterActivityBinding
import com.felis.mldl.login.LoginActivity
import okhttp3.JavaNetCookieJar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.net.CookieManager

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: RegisterActivityBinding
    var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var okHttpClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://15.164.24.10:8000/login/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        var registerService = retrofit.create(RegisterService::class.java)

        val intent = Intent(this, WelcomeActivity::class.java)
        val intent2 = Intent(this, LoginActivity::class.java)

        binding.signBtn.setOnClickListener {
            //이메일,비밀번호 회원가입........................
            val email: String = binding.idEdt2.text.toString()
            val password: String = binding.pwEdt2.text.toString()

            registerService.requestRegister(email, password).enqueue(object : Callback<Register> {
                override fun onResponse(call: Call<Register>, response: Response<Register>) {
                    val Register = response.body()

                    if (email == Register?.user_id) {
                        Toast.makeText(this@RegisterActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<Register>, t: Throwable) {
                    var dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("실패!")
                    dialog.setMessage("통신에 실패했습니다")
                    dialog.show()
                }
            })
            startActivity(intent2)
        }

        binding.cancelBtn.setOnClickListener {
            startActivity(intent)
        }
//            MyApplication.auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    binding.idEdt2.text.clear()
//                    binding.pwEdt2.text.clear()
//                    if (task.isSuccessful) {
//                        // 비밀번호는 최소 6자 이상
//                        //send email...
//                        MyApplication.auth.currentUser?.sendEmailVerification()
//                            ?.addOnCompleteListener { sendTask ->
//                                if (sendTask.isSuccessful) {
//                                    Toast.makeText(
//                                        baseContext, "회원가입에 성공하였습니다. 전송된 메일을 확인해 주세요.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    startActivity(intent2)
//                                } else {
//                                    Toast.makeText(
//                                        baseContext, "메일 전송 실패",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    startActivity(intent)
//                                }
//                            }
//                    } else {
//                        Toast.makeText(
//                            baseContext, "회원가입 실패",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        startActivity(intent)
//                    }
//                }


    }

    fun hidekeyboard(v: View) {
        if (v != null) {
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

}