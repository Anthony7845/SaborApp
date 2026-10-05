package com.senati.saborapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.senati.saborapp.databinding.ActivityPlatoFormBinding
import kotlinx.coroutines.launch
import java.io.IOException

class PlatoFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatoFormBinding
    private var platoId: Int = -1

    private val categorias = arrayOf("Entradas", "Fondos", "Bebidas", "Postres")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlatoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarDropdown()
        obtenerDatosIntent()

        binding.btnGuardar.setOnClickListener {
            guardarPlato()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun configurarDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categorias
        )
        binding.actvCategoria.setAdapter(adapter)
    }

    private fun obtenerDatosIntent() {
        if (intent.hasExtra("id")) {
            platoId = intent.getIntExtra("id", -1)
            binding.tvFormTitulo.text = "Editar Plato"

            val nombre = intent.getStringExtra("nombre") ?: ""
            val categoria = intent.getStringExtra("categoria") ?: ""
            val precio = intent.getDoubleExtra("precio", 0.0)
            val disponible = intent.getIntExtra("disponible", 1)

            binding.etNombre.setText(nombre)
            binding.actvCategoria.setText(categoria, false)
            binding.etPrecio.setText(precio.toString())
            binding.switchDisponible.isChecked = (disponible == 1)
        } else {
            binding.tvFormTitulo.text = "Nuevo Plato"
            binding.actvCategoria.setText(categorias[0], false)
        }
    }

    private fun guardarPlato() {
        val nombre = binding.etNombre.text?.toString()?.trim() ?: ""
        val categoria = binding.actvCategoria.text?.toString()?.trim() ?: ""
        val precioTexto = binding.etPrecio.text?.toString()?.trim() ?: ""
        val disponible = if (binding.switchDisponible.isChecked) 1 else 0

        if (nombre.isEmpty()) {
            binding.etNombre.error = "Ingrese el nombre del plato"
            binding.etNombre.requestFocus()
            return
        }

        if (categoria.isEmpty()) {
            binding.actvCategoria.error = "Seleccione una categoría"
            binding.actvCategoria.requestFocus()
            return
        }

        val precio = precioTexto.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            binding.etPrecio.error = "Ingrese un precio válido"
            binding.etPrecio.requestFocus()
            return
        }

        mostrarCarga(true)

        lifecycleScope.launch {
            try {
                val respuesta = if (platoId == -1) {
                    RetrofitClient.api.guardarPlato(nombre, categoria, precio, disponible)
                } else {
                    RetrofitClient.api.actualizarPlato(platoId, nombre, categoria, precio, disponible)
                }

                if (respuesta.isSuccessful) {
                    val datos = respuesta.body()
                    if (datos?.ok == true) {
                        Toast.makeText(
                            this@PlatoFormActivity,
                            datos.mensaje,
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(
                            this@PlatoFormActivity,
                            datos?.mensaje ?: "Error al guardar el plato",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@PlatoFormActivity,
                        "Error del servidor: ${respuesta.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@PlatoFormActivity,
                    "Sin conexión. Verifica tu red.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@PlatoFormActivity,
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