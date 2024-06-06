package io.clearquote.apptoappintegration.demo.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.clearquote.apptoappintegration.demo.app.cqapplauncher.CQAppLauncherActivity
import io.clearquote.apptoappintegration.demo.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listener on the next button
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, CQAppLauncherActivity::class.java))
        }
    }
}