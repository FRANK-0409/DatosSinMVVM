package com.example.datossinmvvm

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UsuarioAutorizado::class], version = 1)
abstract class MediCaseDatabase : RoomDatabase() {
    abstract fun usuarioAutorizadoDao(): UsuarioAutorizadoDao
}
