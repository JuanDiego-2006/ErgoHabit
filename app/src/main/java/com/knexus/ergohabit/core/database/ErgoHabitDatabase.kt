package com.knexus.ergohabit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.knexus.ergohabit.core.database.dao.AguaDao
import com.knexus.ergohabit.core.database.dao.NutricionDao
import com.knexus.ergohabit.core.database.dao.PosturaDao
import com.knexus.ergohabit.core.database.dao.ProgresoDao
import com.knexus.ergohabit.core.database.dao.SuenoDao
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
        FraseEntity::class,
        AguaDashboardEntity::class,
        SuenoEntity::class,
        NutricionEntity::class,
        PosturaEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class ErgoHabitDatabase : RoomDatabase() {
    abstract fun tareaProgresoDao(): TareaProgresoDao
    abstract fun usuarioPerfilDao(): UsuarioPerfilDao
    abstract fun tareaDao(): TareaDao
    abstract fun progresoDao(): ProgresoDao
    abstract fun aguaDao(): AguaDao
    abstract fun suenoDao(): SuenoDao
    abstract fun nutricionDao(): NutricionDao
    abstract fun posturaDao(): PosturaDao
}
