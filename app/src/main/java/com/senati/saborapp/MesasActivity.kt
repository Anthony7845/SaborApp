package com.senati.saborapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MesasActivity : AppCompatActivity() {

    private lateinit var btnAtras: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesas)

        btnAtras = findViewById(R.id.btnAtras)

        btnAtras.setOnClickListener {
            finish()
        }
    }
}