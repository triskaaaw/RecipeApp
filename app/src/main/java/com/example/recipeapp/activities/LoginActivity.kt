package com.example.recipeapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.recipeapp.R
import com.example.recipeapp.api.models.UserLoginRequest
import com.example.recipeapp.api.models.UserLoginResponse
import com.example.recipeapp.api.services.ApiClient
import com.example.recipeapp.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper


class LoginActivity : AppCompatActivity() {

    lateinit var editUsername: EditText
    lateinit var editPass: EditText
    lateinit var btnLogin: Button
    lateinit var btnSignup: TextView
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        editUsername = findViewById(R.id.et_username)
        editPass = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnSignup = findViewById(R.id.tv_signup_link)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging")
        progressDialog.setMessage("Please wait...")

        btnLogin.setOnClickListener {
            val username = editUsername.text.toString().trim()
            val password = editPass.text.toString().trim()

            if(username.isEmpty()){
                editUsername.error = "Username required"
                editUsername.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                editPass.error = "Password required"
                editPass.requestFocus()
                return@setOnClickListener
            }

            progressDialog.show()


            val request = UserLoginRequest(username, password)
            ApiClient.getApiService().userLogin(request)
                .enqueue(object: Callback<UserLoginResponse>{
                    override fun onResponse(
                        call: Call<UserLoginResponse>,
                        response: Response<UserLoginResponse>
                    ) {
                        progressDialog.dismiss()
                        if (response.isSuccessful){
                            //val user = response.body()
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                val sessionId = loginResponse.sessionId
//                              //val token = loginResponse.token
                                val username = loginResponse.username

                                Log.d("LOGIN_RESPONSE", "Response token: $sessionId")


                                if (sessionId.isNullOrEmpty()) {
                                    Toast.makeText(applicationContext, "Session ID is empty", Toast.LENGTH_LONG).show()
                                    return
                                }

                                //simpan sessionId dan username ke SharedPreferences
                                SharedPrefManager.getInstance(this@LoginActivity).saveUsername(username)
                                SharedPrefManager.getInstance(this@LoginActivity).saveSessionId(sessionId)

                                ApiClient.init(applicationContext)

                                Toast.makeText(applicationContext, "Wellcome back, $username!", Toast.LENGTH_LONG).show()

                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("TRIGGER_REFRESH", true)
                                    startActivity(intent)
                                    finish()
                                }, 300) // delay 300ms untuk pastikan SharedPref kebaca dulu

                            } else{
                                Toast.makeText(applicationContext, "Login failed: empty response", Toast.LENGTH_LONG).show()
                            }

                        }else{
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(applicationContext, "Failed to login: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    }

                })

        }

        btnSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()

        if (SharedPrefManager.getInstance(this).isLoggedIn){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}