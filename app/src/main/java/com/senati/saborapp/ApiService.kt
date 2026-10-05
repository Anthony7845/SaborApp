package com.senati.saborapp

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("platos_listar.php")
    suspend fun listarPlatos(): Response<PlatoResponse>

    @FormUrlEncoded
    @POST("platos_guardar.php")
    suspend fun guardarPlato(
        @Field("nombre") nombre: String,
        @Field("categoria") categoria: String,
        @Field("precio") precio: Double,
        @Field("disponible") disponible: Int
    ): Response<GuardarPlatoResponse>

    @FormUrlEncoded
    @POST("platos_actualizar.php")
    suspend fun actualizarPlato(
        @Field("id") id: Int,
        @Field("nombre") nombre: String,
        @Field("categoria") categoria: String,
        @Field("precio") precio: Double,
        @Field("disponible") disponible: Int
    ): Response<GuardarPlatoResponse>

    @FormUrlEncoded
    @POST("platos_eliminar.php")
    suspend fun eliminarPlato(
        @Field("id") id: Int
    ): Response<GuardarPlatoResponse>

    @GET("mesas_listar.php")
    suspend fun listarMesas(): Response<MesaResponse>

    @FormUrlEncoded
    @POST("mesas_guardar.php")
    suspend fun guardarMesa(
        @Field("numero") numero: String,
        @Field("capacidad") capacidad: Int,
        @Field("estado") estado: String
    ): Response<GuardarMesaResponse>

    @FormUrlEncoded
    @POST("mesas_actualizar.php")
    suspend fun actualizarMesa(
        @Field("id") id: Int,
        @Field("numero") numero: String,
        @Field("capacidad") capacidad: Int,
        @Field("estado") estado: String
    ): Response<GuardarMesaResponse>

    @FormUrlEncoded
    @POST("mesas_eliminar.php")
    suspend fun eliminarMesa(
        @Field("id") id: Int
    ): Response<GuardarMesaResponse>
}