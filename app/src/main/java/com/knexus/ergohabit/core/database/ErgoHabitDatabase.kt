package com.knexus.ergohabit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.knexus.ergohabit.core.database.dao.ProgresoDao
import com.knexus.ergohabit.core.database.dao.TareaDao
import com.knexus.ergohabit.core.database.dao.TareaProgresoDao
import com.knexus.ergohabit.core.database.dao.UsuarioPerfilDao
import com.knexus.ergohabit.core.database.entities.*

@Database(
    entities = [
        TareaProgresoEntity::class,
        UsuarioPerfilEntity::class,
        TareaEntity::class,
        HabitoProgresoEntity::class,
        RegistroSemanalEntity::class,
        FraseEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class ErgoHabitDatabase : RoomDatabase() {
    abstract fun tareaProgresoDao(): TareaProgresoDao
    abstract fun usuarioPerfilDao(): UsuarioPerfilDao
    abstract fun tareaDao(): TareaDao
    abstract fun progresoDao(): ProgresoDao
}
