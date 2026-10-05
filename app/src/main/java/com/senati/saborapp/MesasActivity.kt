package com.senati.saborapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.senati.saborapp.databinding.ActivityMesasBinding
import kotlinx.coroutines.launch
import java.io.IOException

class MesasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesasBinding
    private lateinit var adapter: MesaAdapter

    private var todasLasMesas = listOf<Mesa>()

    private val formularioLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                cargarMesas()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        configurarBusqueda()
        configurarFab()

        cargarMesas()
    }

    private fun configurarRecyclerView() {
        adapter = MesaAdapter(
            onClick = { mesa ->
                abrirFormulario(mesa)
            },
            onLongClick = { mesa ->
                confirmarEliminar(mesa)
            }
        )

        binding.rvMesas.layoutManager = GridLayoutManager(this, 3)
        binding.rvMesas.adapter = adapter
    }

    private fun configurarBusqueda() {
        binding.etBuscar.doAfterTextChanged { texto ->
            val textoBusqueda = texto?.toString()?.trim()?.lowercase() ?: ""

            val filtradas = todasLasMesas.filter {
                it.numero.lowercase().contains(textoBusqueda) ||
                        it.estado.lowercase().contains(textoBusqueda)
            }

            mostrarMesas(filtradas)
        }
    }

    private fun configurarFab() {
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, MesaFormActivity::class.java)
            formularioLauncher.launch(intent)
        }
    }

    private fun abrirFormulario(mesa: Mesa) {
        val intent = Intent(this, MesaFormActivity::class.java)
        intent.putExtra("id", mesa.id)
        intent.putExtra("numero", mesa.numero)
        intent.putExtra("capacidad", mesa.capacidad)
        intent.putExtra("estado", mesa.estado)

        formularioLauncher.launch(intent)
    }

    private fun cargarMesas() {
        mostrarCarga(true)

        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.listarMesas()

                if (respuesta.isSuccessful) {
                    val datos = respuesta.body()
                    val listaMesas = datos?.mesas ?: emptyList()

                    if (listaMesas.isNotEmpty() || datos?.ok == true) {
                        todasLasMesas = listaMesas.sortedBy {
                            it.numero.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                        }

                        val textoBusqueda = binding.etBuscar.text
                            ?.toString()
                            ?.trim()
                            ?.lowercase()
                            ?: ""

                        val filtradas = if (textoBusqueda.isEmpty()) {
                            todasLasMesas
                        } else {
                            todasLasMesas.filter {
                                it.numero.lowercase().contains(textoBusqueda) ||
                                        it.estado.lowercase().contains(textoBusqueda)
                            }
                        }

                        mostrarMesas(filtradas)
                    } else {
                        mostrarMesas(emptyList())
                        Toast.makeText(
                            this@MesasActivity,
                            datos?.mensaje ?: "No hay mesas registradas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    mostrarMesas(emptyList())
                    Toast.makeText(
                        this@MesasActivity,
                        "Error del servidor: ${respuesta.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                mostrarMesas(emptyList())
                Toast.makeText(
                    this@MesasActivity,
                    "Sin conexión. Verifica tu red y XAMPP.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                mostrarMesas(emptyList())
                Toast.makeText(
                    this@MesasActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                mostrarCarga(false)
            }
        }
    }

    private fun mostrarMesas(lista: List<Mesa>) {
        adapter.submitList(lista)
        if (lista.isEmpty()) {
            binding.tvVacio.visibility = View.VISIBLE
        } else {
            binding.tvVacio.visibility = View.GONE
        }
    }

    private fun mostrarCarga(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        if (mostrar) {
            binding.tvVacio.visibility = View.GONE
        }
    }

    private fun confirmarEliminar(mesa: Mesa) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar mesa")
            .setMessage("¿Deseas eliminar la mesa \"${mesa.numero}\"?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarMesa(mesa.id)
            }
            .show()
    }

    private fun eliminarMesa(id: Int) {
        mostrarCarga(true)

        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.eliminarMesa(id)

                if (respuesta.isSuccessful) {
                    val datos = respuesta.body()

                    if (datos?.ok == true) {
                        Toast.makeText(
                            this@MesasActivity,
                            "Mesa eliminada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        cargarMesas()
                    } else {
                        Toast.makeText(
                            this@MesasActivity,
                            datos?.mensaje ?: "No se pudo eliminar la mesa",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MesasActivity,
                        "Error del servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@MesasActivity,
                    "Sin conexión",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@MesasActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                mostrarCarga(false)
            }
        }
    }
}