package com.example.mapapp
import android.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import com.example.mapapp.databinding.ActivityLoginBinding
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        Log.d("LoginActivity", "onCreate called")

        //save
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Load
        val savedUsername = prefs.getString("username", null)
        val savedPassword = prefs.getString("password", null)
        savedUsername?.let { binding.editUsername.setText(it) } // sets the EditText to show the saved password.
        savedPassword?.let { binding.editPassword.setText(it) }


        binding.btnSave.setOnClickListener {
            val username = binding.editUsername.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener { createTask ->
                        if (createTask.isSuccessful) {
                        savePrefs(username, password)
                            val intent = Intent(this, MapsActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, createTask.exception?.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.editUsername.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener

            }
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { createTask ->
                    if (createTask.isSuccessful) {
                        val user = hashMapOf("email" to username)
                        db.collection("users").add(user)
                        Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show()
                        savePrefs(username, password)
                        val intent = Intent(this, MapsActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, createTask.exception?.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
        }

        fun savePrefs(username: String, password: String) {
            val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("username", username)
                .putString("password", password)
                .apply()
        }
    }










//data class User(val username: String, val password: String)
//
//val user = User("neda", "1234")
//
//// Serialize
//val json = Gson().toJson(user) // {"username":"neda","password":"1234"}
//
//// Deserialize
//val user2 = Gson().fromJson(json, User::class.java) \\ just for having sample