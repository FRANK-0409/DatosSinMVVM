package com.example.datossinmvvm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsuarioAutorizadoDao {
    @Query("SELECT * FROM usuarios_autorizados")
    suspend fun getAll(): List<UsuarioAutorizado>

    @Insert
    suspend fun insert(usuario: UsuarioAutorizado)

    @Update
    suspend fun update(usuario: UsuarioAutorizado)

    @Delete
    suspend fun delete(usuario: UsuarioAutorizado)
}
