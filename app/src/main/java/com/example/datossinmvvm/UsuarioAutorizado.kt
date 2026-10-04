package com.example.datossinmvvm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios_autorizados")
data class UsuarioAutorizado(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreCompleto: String,
    val correo: String,
    val telefono: String,
    val rol: String
)
