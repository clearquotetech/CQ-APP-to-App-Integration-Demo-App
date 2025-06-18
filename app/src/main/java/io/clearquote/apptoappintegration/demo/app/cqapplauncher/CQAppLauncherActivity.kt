package io.clearquote.apptoappintegration.demo.app.cqapplauncher

import android.R
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.clearquote.apptoappintegration.demo.app.databinding.ActivityCqappLauncherBinding

class CQAppLauncherActivity : AppCompatActivity() {
    // Binding
    private lateinit var binding: ActivityCqappLauncherBinding

    // Version selector
    private val integrationVersion1 = "Version 1 (Broadcast rec)"
    private val integrationVersion2 = "Version 2 (Deeplinks)"
    private val returnUri = Uri.encode("cqapptoappintegration://inspection")

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

        // Populate options for the version selector dropdown
        val versionList = listOf(integrationVersion1, integrationVersion2)
        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, versionList)
        binding.acSelectVersion.setAdapter(adapter)
        binding.acSelectVersion.setText(versionList[1], false)

        // Set click listener on the launch cq app
        binding.btnLaunchCQApp.setOnClickListener {
            prepareCQAppLaunch()
        }

        // Register broadcast receiver
        registerBroadCastReceivers()
    }

    override fun onResume() {
        super.onResume()

        // Get extras
        val msg = intent.data?.getQueryParameter("msg")
        val quoteId = intent.data?.getQueryParameter("quoteId")
        val publicDetailsPageUrl = intent.data?.getQueryParameter("publicDetailsPageUrl")
        val registrationNumber = intent.data?.getQueryParameter("registrationNumber")

        if (intent.data != null) {
            // Decode public detail page uri
            val decodedPublicDetailsPageUri = Uri.decode(publicDetailsPageUrl)

            binding.tvMsgFromCq.text = "Message: $msg"
            binding.tvQuoteIdFromCq.text = "QuoteId: $quoteId"
            binding.tvRegistrationNumberFromCq.text = "Registration Number: $registrationNumber"
            binding.tvPublicDetailsPageFromCq.text = "Public details page URL: $decodedPublicDetailsPageUri"
        }
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
        if (binding.acSelectVersion.text.toString() == integrationVersion1) {
            launchCQAppForIntegrationVersion1()
        } else {
            launchCQAppForIntegrationVersion2()
        }
    }

    private fun launchCQAppForIntegrationVersion1() {
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

    private fun launchCQAppForIntegrationVersion2() {
        val uri = Uri.parse(
            "clearinspect://inspection" +
                "?integrationVersion=v2" +
                "&registrationNumber=${binding.etLPNumber.text.toString()}" +
                "&source=DoForms" +
                "&packageName=$packageName" +
                "&returnPageAddress=$returnUri"
        )
        Log.e("AppToAppIntegrationSupportActivityAG", "Encoded URI -> $returnUri")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Could not find the target app", Toast.LENGTH_LONG).show()
        }
    }
}