package com.knexus.ergohabit.features.progreso.di

import com.knexus.ergohabit.features.progreso.data.datasource.api.ProgresoDiarioApi
import com.knexus.ergohabit.features.progreso.data.repositories.ProgresoRepositoryImpl
import com.knexus.ergohabit.features.progreso.domain.repositories.ProgresoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProgresoModule {

    @Binds
    @Singleton
    abstract fun bindProgresoRepository(impl: ProgresoRepositoryImpl): ProgresoRepository

    companion object {
        @Provides
        @Singleton
        fun provideProgresoDiarioApi(retrofit: Retrofit): ProgresoDiarioApi {
            return retrofit.create(ProgresoDiarioApi::class.java)
        }
    }
}
