package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var etUsuario: TextInputEditText
    private lateinit var etClave: TextInputEditText
    private lateinit var btnIngresar: MaterialButton

    private val url = "http://192.168.106.61/saborapp/login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        etUsuario = findViewById(R.id.etUsuario)
        etClave = findViewById(R.id.etClave)
        btnIngresar = findViewById(R.id.btnIngresar)

        btnIngresar.setOnClickListener {

            val usuario = etUsuario.text.toString().trim()
            val clave = etClave.text.toString()

            if (usuario.isEmpty()) {
                etUsuario.error = "Ingrese su usuario"
                etUsuario.requestFocus()
                return@setOnClickListener
            }

            if (clave.isEmpty()) {
                etClave.error = "Ingrese su contraseña"
                etClave.requestFocus()
                return@setOnClickListener
            }

            iniciarSesion(usuario, clave)
        }
    }

    private fun iniciarSesion(
        usuario: String,
        clave: String
    ) {

        val request = object : StringRequest(

            Request.Method.POST,
            url,

            { respuesta ->

                val resultado = respuesta.trim()

                when {

                    resultado.startsWith("OK|") -> {

                        val datos = resultado.split("|")

                        val id = datos[1]
                        val nombreUsuario = datos[2]
                        val rol = datos[3]

                        Toast.makeText(
                            this,
                            "Bienvenido $nombreUsuario",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this,
                            MenuActivity::class.java
                        )

                        intent.putExtra("id", id)
                        intent.putExtra("usuario", nombreUsuario)
                        intent.putExtra("rol", rol)

                        startActivity(intent)

                        finish()
                    }


                    resultado == "USUARIO_NO_EXISTE" -> {

                        Toast.makeText(
                            this,
                            "El usuario no existe",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                    resultado == "CLAVE_INCORRECTA" -> {

                        Toast.makeText(
                            this,
                            "Contraseña incorrecta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    resultado == "FALTAN_DATOS" -> {

                        Toast.makeText(
                            this,
                            "Complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {

                        Toast.makeText(
                            this,
                            "Servidor: $resultado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },

            { error ->

                Toast.makeText(
                    this,
                    "Error de conexión: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                val parametros = HashMap<String, String>()

                parametros["usuario"] = usuario
                parametros["clave"] = clave

                return parametros
            }
        }

        val cola = Volley.newRequestQueue(this)

        cola.add(request)
    }
}

