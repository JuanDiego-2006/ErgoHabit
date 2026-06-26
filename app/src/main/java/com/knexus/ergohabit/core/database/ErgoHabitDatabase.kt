package com.knexus.ergohabit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.knexus.ergohabit.core.database.dao.TareaProgresoDao
import com.knexus.ergohabit.core.database.dao.UsuarioPerfilDao
import com.knexus.ergohabit.core.database.entities.TareaProgresoEntity
import com.knexus.ergohabit.core.database.entities.UsuarioPerfilEntity

@Database(
    entities = [
        TareaProgresoEntity::class,
        UsuarioPerfilEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ErgoHabitDatabase : RoomDatabase() {
    abstract fun tareaProgresoDao(): TareaProgresoDao
    abstract fun usuarioPerfilDao(): UsuarioPerfilDao
}
