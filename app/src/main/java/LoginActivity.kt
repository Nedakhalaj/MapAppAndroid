package com.example.mapapp
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import com.example.mapapp.databinding.ActivityLoginBinding
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("LoginActivity", "onCreate called")


        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val saved = prefs.getString("username", null)
        saved?.let {
            binding.editUsername.setText(it)
        }

        binding.btnSave.setOnClickListener {
            val username = binding.editUsername.text.toString().trim()
            if (username.isNotEmpty()) {
                prefs.edit().putString("username", username).apply()
                startActivity(Intent(this, MapsActivity::class.java))
            }else{
                Toast.makeText(this, "please enter username", Toast.LENGTH_SHORT).show()
            }


        }
    }
}