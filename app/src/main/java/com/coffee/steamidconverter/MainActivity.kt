package com.coffee.steamidconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.coffee.steamidconverter.utils.SteamIDConverter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val steamIDInput: TextInputEditText = findViewById(R.id.steamIDInput)
        val steamIDTV: MaterialTextView = findViewById(R.id.tvSteamID)
        val convertBtn: MaterialButton = findViewById(R.id.convertBtn)

        convertBtn.setOnClickListener {
            if (!TextUtils.isEmpty(steamIDInput.text)) {
                val idMap =
                    SteamIDConverter(applicationContext).convert(steamIDInput.text.toString())

                val displayText = if (!idMap.isNullOrEmpty()) {
                    idMap.entries.toString()
                } else "Invalid SteamID"

                steamIDTV.text = displayText
            }
        }
    }
}