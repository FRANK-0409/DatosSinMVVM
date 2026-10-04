package com.example.datossinmvvm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUsuariosAutorizados() {
    val context = LocalContext.current
    val db = remember { 
        Room.databaseBuilder(context, MediCaseDatabase::class.java, "medicase_db").build() 
    }
    val dao = db.usuarioAutorizadoDao()
    val coroutineScope = rememberCoroutineScope()

    // Estados para el formulario (Basados en tu diseño "Nuevo Usuario")
    var idActual by remember { mutableStateOf<Int?>(null) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Cuidador") } // Default

    var listaUsuarios by remember { mutableStateOf(listOf<UsuarioAutorizado>()) }

    // Función para recargar la lista de la base de datos
    fun cargarUsuarios() {
        coroutineScope.launch {
            listaUsuarios = dao.getAll()
        }
    }

    // Cargar los usuarios al abrir la pantalla
    LaunchedEffect(Unit) {
        cargarUsuarios()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MediCase: Usuarios Autorizados") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // --- FORMULARIO (CREATE / UPDATE) ---
            Text("Formulario de Registro", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo (Ej. Laura Gómez)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono móvil") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = rol,
                onValueChange = { rol = it },
                label = { Text("Rol asignado (Cuidador / Administrador)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (idActual == null) {
                                // CREATE: Guardar nuevo usuario
                                val nuevoUsuario = UsuarioAutorizado(
                                    nombreCompleto = nombre,
                                    correo = correo,
                                    telefono = telefono,
                                    rol = rol
                                )
                                dao.insert(nuevoUsuario)
                            } else {
                                // UPDATE: Actualizar usuario existente
                                val usuarioActualizado = UsuarioAutorizado(
                                    id = idActual!!,
                                    nombreCompleto = nombre,
                                    correo = correo,
                                    telefono = telefono,
                                    rol = rol
                                )
                                dao.update(usuarioActualizado)
                            }
                            // Limpiar formulario y recargar lista
                            nombre = ""; correo = ""; telefono = ""; rol = "Cuidador"; idActual = null
                            cargarUsuarios()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (idActual == null) "Agregar Usuario" else "Actualizar Usuario")
                }
                
                if (idActual != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            // Cancelar edición
                            nombre = ""; correo = ""; telefono = ""; rol = "Cuidador"; idActual = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // --- LISTA (READ & DELETE) ---
            Text("Usuarios Registrados", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(listaUsuarios) { usuario ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(usuario.nombreCompleto, style = MaterialTheme.typography.titleMedium)
                                Text("${usuario.rol} • ${usuario.correo}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row {
                                // Botón EDITAR (Llena el formulario con los datos del usuario)
                                IconButton(onClick = {
                                    idActual = usuario.id
                                    nombre = usuario.nombreCompleto
                                    correo = usuario.correo
                                    telefono = usuario.telefono
                                    rol = usuario.rol
                                }) {
                                    Icon(Icons.Filled.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                                // Botón ELIMINAR (Borra de la DB)
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        dao.delete(usuario)
                                        cargarUsuarios()
                                    }
                                }) {
                                    Icon(Icons.Filled.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
