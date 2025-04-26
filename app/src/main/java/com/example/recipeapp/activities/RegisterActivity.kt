package com.example.recipeapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.R
import com.example.recipeapp.api.services.ApiClient
import com.example.recipeapp.api.models.UserRegisterRequest
import com.example.recipeapp.api.models.UserRegisterResponse
import com.example.recipeapp.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private lateinit var call: Call<UserRegisterResponse>

    lateinit var editUsername: EditText
    lateinit var editName: EditText
    lateinit var editPass: EditText
    lateinit var editPassConf: EditText
    lateinit var btnRegister: Button
    lateinit var btnLogin: TextView
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        linearLayout = findViewById(R.id.linear_layout_register)

        editUsername = findViewById(R.id.et_username)
        editName = findViewById(R.id.et_name)
        editPass = findViewById(R.id.et_password)
        editPassConf = findViewById(R.id.et_confirm_password)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging")
        progressDialog.setMessage("Please wait...")

        btnRegister = findViewById(R.id.btn_signup)
        btnLogin = findViewById(R.id.tv_login_link)

        btnLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnRegister.setOnClickListener{
            val name = editName.text.toString().trim()
            val username = editUsername.text.toString().trim()
            val password = editPass.text.toString().trim()
            val passwordconf = editPassConf.text.toString().trim()

            if(name.isEmpty()){
                editName.error = "Name required"
                editName.requestFocus()
                return@setOnClickListener
            }
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
            if(passwordconf.isEmpty()){
                editPassConf.error = "Password required"
                editPassConf.requestFocus()
                return@setOnClickListener
            }


            val request = UserRegisterRequest(name, username, password)
            ApiClient.getApiService().postUserRegister(request)
//            ApiClient.registerService.postUserRegister(name, username, password)
                .enqueue(object: Callback<UserRegisterResponse>{
                    override fun onResponse(
                        call: Call<UserRegisterResponse>,
                        response: Response<UserRegisterResponse>
                    ) {
                        val user = response.body()
                        if(user != null){
                            Toast.makeText(applicationContext, "Registration successful! Welcome ${user.username}", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else{
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(applicationContext, "Failed to register: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    }

                })



        }

    }

    override fun onStart() {
        super.onStart()

        if (SharedPrefManager.getInstance(this).isLoggedIn){
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}