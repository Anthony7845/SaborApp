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
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.saborapp.databinding.ActivityPlatosBinding
import kotlinx.coroutines.launch
import java.io.IOException

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding
    private lateinit var adapter: PlatoAdapter

    private var todosLosPlatos = listOf<Plato>()

    private val formularioLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado ->

            if (resultado.resultCode == RESULT_OK) {
                cargarPlatos()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        configurarBusqueda()
        configurarFab()

        cargarPlatos()
    }

    private fun configurarRecyclerView() {

        adapter = PlatoAdapter(
            onClick = { plato ->
                abrirFormulario(plato)
            },
            onLongClick = { plato ->
                confirmarEliminar(plato)
            }
        )

        binding.rvPlatos.layoutManager =
            LinearLayoutManager(this)

        binding.rvPlatos.adapter = adapter
    }

    private fun configurarBusqueda() {

        binding.etBuscar.doAfterTextChanged { texto ->

            val textoBusqueda =
                texto?.toString()?.trim()?.lowercase() ?: ""

            val filtrados = todosLosPlatos.filter {

                it.nombre.lowercase()
                    .contains(textoBusqueda)
            }

            mostrarPlatos(filtrados)
        }
    }

    private fun configurarFab() {

        binding.fabAgregar.setOnClickListener {

            val intent = Intent(
                this,
                PlatoFormActivity::class.java
            )

            formularioLauncher.launch(intent)
        }
    }

    private fun abrirFormulario(plato: Plato) {

        val intent = Intent(
            this,
            PlatoFormActivity::class.java
        )

        intent.putExtra("id", plato.id)
        intent.putExtra("nombre", plato.nombre)
        intent.putExtra("categoria", plato.categoria)
        intent.putExtra("precio", plato.precio)
        intent.putExtra("disponible", plato.disponible)

        formularioLauncher.launch(intent)
    }

    private fun cargarPlatos() {

        mostrarCarga(true)

        lifecycleScope.launch {

            try {

                val respuesta =
                    RetrofitClient.api.listarPlatos()

                if (respuesta.isSuccessful) {

                    val datos = respuesta.body()

                    if (datos?.ok == true) {

                        todosLosPlatos =
                            datos.platos.sortedWith(
                                compareBy<Plato> {
                                    ordenCategoria(it.categoria)
                                }.thenBy {
                                    it.nombre.lowercase()
                                }
                            )

                        val textoBusqueda =
                            binding.etBuscar.text
                                ?.toString()
                                ?.trim()
                                ?.lowercase()
                                ?: ""

                        val filtrados =
                            if (textoBusqueda.isEmpty()) {

                                todosLosPlatos

                            } else {

                                todosLosPlatos.filter {
                                    it.nombre.lowercase()
                                        .contains(textoBusqueda)
                                }
                            }

                        mostrarPlatos(filtrados)

                    } else {

                        mostrarPlatos(emptyList())

                        Toast.makeText(
                            this@PlatosActivity,
                            datos?.mensaje
                                ?: "No se pudieron cargar los platos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    mostrarPlatos(emptyList())

                    Toast.makeText(
                        this@PlatosActivity,
                        "Error del servidor: ${respuesta.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: IOException) {

                mostrarPlatos(emptyList())

                Toast.makeText(
                    this@PlatosActivity,
                    "Sin conexión. Verifica tu red y XAMPP.",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {

                mostrarPlatos(emptyList())

                Toast.makeText(
                    this@PlatosActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                mostrarCarga(false)
            }
        }
    }

    private fun ordenCategoria(categoria: String): Int {

        return when (categoria.lowercase()) {

            "entradas" -> 1
            "fondos" -> 2
            "bebidas" -> 3
            "postres" -> 4

            else -> 5
        }
    }

    private fun mostrarPlatos(lista: List<Plato>) {

        adapter.submitList(lista)

        if (lista.isEmpty()) {

            binding.tvVacio.visibility = View.VISIBLE

        } else {

            binding.tvVacio.visibility = View.GONE
        }
    }

    private fun mostrarCarga(mostrar: Boolean) {

        binding.progressBar.visibility =
            if (mostrar) View.VISIBLE else View.GONE

        if (mostrar) {

            binding.tvVacio.visibility = View.GONE
        }
    }

    private fun confirmarEliminar(plato: Plato) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar plato")
            .setMessage(
                "¿Deseas eliminar \"${plato.nombre}\"?"
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->

                eliminarPlato(plato.id)
            }
            .show()
    }

    private fun eliminarPlato(id: Int) {

        mostrarCarga(true)

        lifecycleScope.launch {

            try {

                val respuesta =
                    RetrofitClient.api.eliminarPlato(id)

                if (respuesta.isSuccessful) {

                    val datos = respuesta.body()

                    if (datos?.ok == true) {

                        Toast.makeText(
                            this@PlatosActivity,
                            "Plato eliminado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarPlatos()

                    } else {

                        Toast.makeText(
                            this@PlatosActivity,
                            datos?.mensaje
                                ?: "No se pudo eliminar",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this@PlatosActivity,
                        "Error del servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: IOException) {

                Toast.makeText(
                    this@PlatosActivity,
                    "Sin conexión",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    this@PlatosActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                mostrarCarga(false)
            }
        }
    }
}