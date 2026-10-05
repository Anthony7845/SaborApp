package com.senati.saborapp

data class Plato(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val disponible: Int
)

data class PlatoResponse(
    val ok: Boolean,
    val platos: List<Plato> = emptyList(),
    val mensaje: String? = null
)

data class GuardarPlatoResponse(
    val ok: Boolean,
    val mensaje: String,
    val id: Int? = null
)