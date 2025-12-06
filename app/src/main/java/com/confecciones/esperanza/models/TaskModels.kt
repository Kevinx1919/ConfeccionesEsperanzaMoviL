package com.confecciones.esperanza.models

import com.google.gson.annotations.SerializedName

data class Tarea(
    val idTarea: Int,
    val nombreTarea: String,
    val descripcion: String,
    val comentarios: String,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val asignacionesActivas: Int,
    val asignacionesCompletadas: Int
)

data class TareasResponse(
    val tareas: List<Tarea>,
    val totalCount: Int
)
