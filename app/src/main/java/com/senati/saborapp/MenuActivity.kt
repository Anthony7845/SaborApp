package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.widget.TextView

class MenuActivity : AppCompatActivity() {

    private lateinit var txtBienvenida: TextView
    private lateinit var txtRol: TextView

    private lateinit var btnPlatos: MaterialButton
    private lateinit var btnMesas: MaterialButton
    private lateinit var btnPedidos: MaterialButton
    private lateinit var btnReportes: MaterialButton
    private lateinit var btnSalir: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        // Referencias
        txtBienvenida = findViewById(R.id.txtBienvenida)
        txtRol = findViewById(R.id.txtRol)

        btnPlatos = findViewById(R.id.btnPlatos)
        btnMesas = findViewById(R.id.btnMesas)
        btnPedidos = findViewById(R.id.btnPedidos)
        btnReportes = findViewById(R.id.btnReportes)
        btnSalir = findViewById(R.id.btnSalir)

        // Recibir datos del Login
        val usuario = intent.getStringExtra("usuario")
        val rol = intent.getStringExtra("rol")

        // Mostrar usuario
        txtBienvenida.text = "Bienvenido, $usuario"

        // Mostrar rol
        txtRol.text = "Rol: $rol"

        // ==================================
        // REPORTES SOLO PARA ADMIN
        // ==================================

        if (rol == "ADMIN") {

            btnReportes.visibility = android.view.View.VISIBLE

        } else {

            btnReportes.visibility = android.view.View.GONE
        }

        // ==================================
        // PLATOS
        // ==================================

        btnPlatos.setOnClickListener {

            val intent = Intent(
                this,
                PlatosActivity::class.java
            )

            startActivity(intent)
        }

        // ==================================
        // MESAS
        // ==================================

        btnMesas.setOnClickListener {

            val intent = Intent(
                this,
                MesasActivity::class.java
            )

            startActivity(intent)
        }

        // ==================================
        // PEDIDOS
        // ==================================

        btnPedidos.setOnClickListener {

            val intent = Intent(
                this,
                PedidosActivity::class.java
            )

            startActivity(intent)
        }

        // ==================================
        // REPORTES
        // ==================================

        btnReportes.setOnClickListener {

            val intent = Intent(
                this,
                ReportesActivity::class.java
            )

            startActivity(intent)
        }

        // ==================================
        // SALIR
        // ==================================

        btnSalir.setOnClickListener {

            // Volver al Login
            val intent = Intent(
                this,
                MainActivity::class.java
            )

            // Limpia las Activities anteriores
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }
}
