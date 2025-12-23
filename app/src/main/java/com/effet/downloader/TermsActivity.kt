package com.effet.downloader

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val prefs = Prefs(this)

        // If terms already accepted, go to main
        if (prefs.isTermsAccepted()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val confirmCheckbox = findViewById<CheckBox>(R.id.confirmCheckbox)
        val agreeButton = findViewById<Button>(R.id.agreeButton)
        val exitButton = findViewById<Button>(R.id.exitButton)

        // Enable agree button only when checkbox is checked
        confirmCheckbox.setOnCheckedChangeListener { _, isChecked ->
            agreeButton.isEnabled = isChecked
            agreeButton.alpha = if (isChecked) 1.0f else 0.5f
        }

        agreeButton.setOnClickListener {
            prefs.setTermsAccepted(true)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        exitButton.setOnClickListener {
            finishAffinity()
        }
    }
}
