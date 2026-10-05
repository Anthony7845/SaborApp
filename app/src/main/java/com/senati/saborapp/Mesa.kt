package com.senati.saborapp

import com.google.gson.annotations.SerializedName

data class Mesa(
    val id: Int = 0,
    @SerializedName(value = "numero", alternate = ["numero_mesa", "num_mesa", "num"])
    val numero: String = "",
    @SerializedName(value = "capacidad", alternate = ["capacidad_mesa", "sillas", "aforo"])
    val capacidad: Int = 4,
    @SerializedName(value = "estado", alternate = ["estado_mesa", "disponible"])
    val estado: String = "Disponible"
)

data class MesaResponse(
    val ok: Boolean? = true,
    @SerializedName(value = "mesas", alternate = ["data", "lista", "resultado"])
    val mesas: List<Mesa>? = emptyList(),
    val mensaje: String? = null
)

data class GuardarMesaResponse(
    val ok: Boolean? = true,
    val mensaje: String? = null,
    val id: Int? = null
)