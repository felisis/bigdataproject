package com.felis.mldl.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.felis.mldl.main.MainActivity
import com.felis.mldl.activity.WelcomeActivity
import com.felis.mldl.MyApplication
import com.felis.mldl.R
import com.felis.mldl.databinding.LoginActivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding
    var imm: InputMethodManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        val cookiePolicy = CookieManager()
        cookiePolicy.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        var okHttpClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://15.164.24.10:8000/login/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        var loginService = retrofit.create(LoginService::class.java)

        val intent = Intent(this, WelcomeActivity::class.java)
        val intent2 = Intent(this, MainActivity::class.java)

        binding.BackBtn1.setOnClickListener {
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener() {
            //이메일, 비밀번호 로그인.......................
            val email: String = binding.idEdt.text.toString()
            val password: String = binding.pwEdt.text.toString()

            loginService.requestLogin(email, password).enqueue(object : Callback<Login> {
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    var login = response.body()

                    if (email == login?.user_id) {
                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(intent2)
                    }
                    if (email == "") {
                        Toast.makeText(this@LoginActivity, "아이디는 공백이 될 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    if (password == "") {
                        Toast.makeText(
                            this@LoginActivity,
                            "비밀번호는 공백이 될 수 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (email != login?.user_id) {
                        Toast.makeText(this@LoginActivity, "아이디가 맞지 않습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("실패!")
                    dialog.setMessage("통신에 실패했습니다")
                    dialog.show()
                }
            })
        }

//            MyApplication.auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    binding.idEdt.text.clear()
//                    binding.pwEdt.text.clear()
//                    if (task.isSuccessful) {
//                        if (MyApplication.checkAuth()) {
//                            //로그인 성공 상황...........
//                            MyApplication.email = email
//                            startActivity(intent2)
//                        } else {
//                            //발송된 메일로 인증 확인을 안한경우...........
//                            Toast.makeText(
//                                baseContext, "전송된 메일로 이메일 인증이 되지 않았습니다.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                    } else {
//                        Toast.makeText(
//                            baseContext, "로그인 실패",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                }

        binding.googleBtn.setOnClickListener {
            //구글 로그인
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail()
                .build()
            //구글의 인증관리 앱 실행
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            startActivityForResult(signInIntent, 10)
            //startActivityIfNeeded(signInIntent, 10) 같은 타입 참조함수 일단 적어둠

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intent2 = Intent(this, MainActivity::class.java)
        val intent4 = Intent(this, WelcomeActivity::class.java)
        super.onActivityResult(requestCode, resultCode, data)
        //구글 로그인 결과 처리
        if (requestCode == 10) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                MyApplication.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            //구글 로그인 성공
                            MyApplication.email = account.email
                            startActivity(intent2)
                        } else {
                            //구글 로그인 실패
                            startActivity(intent4)
                        }
                    }

            } catch (e: ApiException) {
                startActivity(intent4)
            }
        }
    }

    fun hidekeyboard(v: View) {
        if (v != null) {
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

}