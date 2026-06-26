package com.knexus.ergohabit.features.tareas.di

import com.knexus.ergohabit.core.database.dao.TareaProgresoDao
import com.knexus.ergohabit.features.tareas.data.datasource.api.TareaApi
import com.knexus.ergohabit.features.tareas.data.repositories.TareaRepositoryImpl
import com.knexus.ergohabit.features.tareas.domain.repositories.TareaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TareasModule {

    @Provides
    @Singleton
    fun provideTareaApi(retrofit: Retrofit): TareaApi {
        return retrofit.create(TareaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTareaRepository(api: TareaApi, dao: TareaProgresoDao): TareaRepository {
        return TareaRepositoryImpl(api, dao)
    }
}
