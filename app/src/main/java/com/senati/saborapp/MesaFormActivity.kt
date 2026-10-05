package com.senati.saborapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.senati.saborapp.databinding.ActivityMesaFormBinding
import kotlinx.coroutines.launch
import java.io.IOException

class MesaFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesaFormBinding
    private var mesaId: Int = -1

    private val estados = arrayOf("Disponible", "Ocupada", "Reservada")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMesaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarDropdown()
        obtenerDatosIntent()

        binding.btnGuardar.setOnClickListener {
            guardarMesa()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun configurarDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            estados
        )
        binding.actvEstado.setAdapter(adapter)
    }

    private fun obtenerDatosIntent() {
        if (intent.hasExtra("id")) {
            mesaId = intent.getIntExtra("id", -1)
            binding.tvFormTitulo.text = "Editar Mesa"

            val numero = intent.getStringExtra("numero") ?: ""
            val capacidad = intent.getIntExtra("capacidad", 4)
            val estado = intent.getStringExtra("estado") ?: "Disponible"

            binding.etNumero.setText(numero)
            binding.etCapacidad.setText(capacidad.toString())
            binding.actvEstado.setText(estado, false)
        } else {
            binding.tvFormTitulo.text = "Nueva Mesa"
            binding.actvEstado.setText(estados[0], false)
        }
    }

    private fun guardarMesa() {
        val numero = binding.etNumero.text?.toString()?.trim() ?: ""
        val capacidadTexto = binding.etCapacidad.text?.toString()?.trim() ?: ""
        val estado = binding.actvEstado.text?.toString()?.trim() ?: "Disponible"

        if (numero.isEmpty()) {
            binding.etNumero.error = "Ingrese el número de mesa"
            binding.etNumero.requestFocus()
            return
        }

        val capacidad = capacidadTexto.toIntOrNull()
        if (capacidad == null || capacidad <= 0) {
            binding.etCapacidad.error = "Ingrese una capacidad válida"
            binding.etCapacidad.requestFocus()
            return
        }

        mostrarCarga(true)

        lifecycleScope.launch {
            try {
                val respuesta = if (mesaId == -1) {
                    RetrofitClient.api.guardarMesa(numero, capacidad, estado)
                } else {
                    RetrofitClient.api.actualizarMesa(mesaId, numero, capacidad, estado)
                }

                if (respuesta.isSuccessful) {
                    val datos = respuesta.body()
                    if (datos?.ok == true) {
                        Toast.makeText(
                            this@MesaFormActivity,
                            datos.mensaje,
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(
                            this@MesaFormActivity,
                            datos?.mensaje ?: "Error al guardar la mesa",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MesaFormActivity,
                        "Error del servidor: ${respuesta.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@MesaFormActivity,
                    "Sin conexión. Verifica tu red.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@MesaFormActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                mostrarCarga(false)
            }
        }
    }

    private fun mostrarCarga(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        binding.btnGuardar.isEnabled = !mostrar
        binding.btnCancelar.isEnabled = !mostrar
    }
}