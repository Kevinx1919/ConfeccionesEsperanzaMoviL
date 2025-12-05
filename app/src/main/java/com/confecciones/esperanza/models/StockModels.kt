package com.confecciones.esperanza.models

import com.google.gson.annotations.SerializedName

data class Material(
    val idMaterial: Int,
    val nombre: String,
    val cantidad: Double,
    val fechaEntrada: String,
    val proveedor: String,
    @SerializedName("tipoMaterial_IdTipoMaterial") val tipoMaterialId: Int,
    val tipoMaterialDescripcion: String,
    val unidadMedida: String,
    @SerializedName("color_IdColor") val colorId: Int,
    val colorDescripcion: String,
    val fechaCreacion: String,
    val fechaActualizacion: String
)

data class MaterialesResponse(
    val materiales: List<Material>,
    val totalCount: Int
)

data class CreateMaterialRequest(
    val nombre: String,
    val cantidad: Int,
    val fechaEntrada: String,
    val proveedor: String,
    @SerializedName("tipoMaterial_IdTipoMaterial") val tipoMaterialId: Int,
    @SerializedName("color_IdColor") val colorId: Int
)

data class MaterialOperationResponse(
    val message: String
)

data class TipoMaterial(
    @SerializedName("idTipoMaterial") val id: Int,
    @SerializedName("descripcionMaterial") val descripcion: String,
    val unidadMedida: Double
)

data class Color(
    @SerializedName("idColor") val id: Int,
    @SerializedName("descripcionColor") val nombre: String
)
