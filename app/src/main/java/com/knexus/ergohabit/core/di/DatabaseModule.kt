package com.knexus.ergohabit.core.di

import android.content.Context
import androidx.room.Room
import com.knexus.ergohabit.core.database.ErgoHabitDatabase
import com.knexus.ergohabit.core.database.dao.TareaProgresoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ErgoHabitDatabase {
        return Room.databaseBuilder(
            context,
            ErgoHabitDatabase::class.java,
            "ergo_habit_db"
        )
            .fallbackToDestructiveMigration() // Esto evita el crash al cambiar la versión
            .build()
    }

    @Provides
    @Singleton
    fun provideTareaProgresoDao(database: ErgoHabitDatabase): TareaProgresoDao {
        return database.tareaProgresoDao()
    }

    @Provides
    @Singleton
    fun provideUsuarioPerfilDao(database: ErgoHabitDatabase): com.knexus.ergohabit.core.database.dao.UsuarioPerfilDao {
        return database.usuarioPerfilDao()
    }

    @Provides
    @Singleton
    fun provideTareaDao(database: ErgoHabitDatabase): com.knexus.ergohabit.core.database.dao.TareaDao {
        return database.tareaDao()
    }

    @Provides
    @Singleton
    fun provideProgresoDao(database: ErgoHabitDatabase): com.knexus.ergohabit.core.database.dao.ProgresoDao {
        return database.progresoDao()
    }
}
