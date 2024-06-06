package io.clearquote.apptoappintegration.demo.app.cqapplauncher

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.clearquote.apptoappintegration.demo.app.databinding.ActivityCqappLauncherBinding

class CQAppLauncherActivity : AppCompatActivity() {
    // Binding
    private lateinit var binding: ActivityCqappLauncherBinding

    // Broadcast receiver for other events
    private val broadcastReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1 != null) {
                    when (p1.action) {
                        "ClearQuoteInspectionCreationAction" -> {
                            binding.tvMsgFromCq.text = "Message: ${p1.getStringExtra("msg")}"
                            binding.tvQuoteIdFromCq.text = "QuoteId: ${p1.getStringExtra("quoteId")}"
                            binding.tvRegistrationNumberFromCq.text = "Registration Number: ${p1.getStringExtra("registrationNumber")}"
                            binding.tvPublicDetailsPageFromCq.text = "Public details page URL: ${p1.getStringExtra("publicDetailsPageUrl")}"
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityCqappLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listener on the launch cq app
        binding.btnLaunchCQApp.setOnClickListener {
            prepareCQAppLaunch()
        }

        // Register broadcast receiver
        registerBroadCastReceivers()
    }

    override fun onDestroy() {
        // Unregister broadcast receiver
        unRegisterBroadCastReceiver()

        // Call super
        super.onDestroy()
    }

    private fun registerBroadCastReceivers() {
        // Create an instance of an intent filter
        val intentFilter = IntentFilter()
        intentFilter.addAction("ClearQuoteInspectionCreationAction")

        // Register receiver
        ContextCompat.registerReceiver(
            this,
            broadcastReceiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun unRegisterBroadCastReceiver() {
        unregisterReceiver(broadcastReceiver)
    }

    private fun clearMessagesFromCQ() {
        binding.tvMsgFromCq.text = ""
        binding.tvQuoteIdFromCq.text = ""
        binding.tvRegistrationNumberFromCq.text = ""
        binding.tvPublicDetailsPageFromCq.text = ""
    }

    private fun prepareCQAppLaunch() {
        // Clear messages from CQ
        clearMessagesFromCQ()

        // Launch CQ app
        launchCQApp()
    }

    private fun launchCQApp() {
        // Launch CQ app
        val CQAppPackageName = "io.clearquote.assessment"
        val cName = ComponentName(CQAppPackageName, "${CQAppPackageName}.AppToAppIntegrationSupportActivity")
        val intent = Intent(Intent.ACTION_MAIN).apply {
            component = cName
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("registrationNumber", binding.etLPNumber.text.toString())
            putExtra("source", "DoForms")
            putExtra("packageName", packageName)
            putExtra("returnPageAddress", "${packageName}.cqapplauncher.CQAppLauncherActivity")
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Could not find the target app", Toast.LENGTH_LONG).show()
        }
    }
}