package com.knexus.ergohabit.features.progreso.di

import com.knexus.ergohabit.features.progreso.data.datasource.api.ProgresoApi
import com.knexus.ergohabit.features.progreso.data.repositories.ProgresoRepositoryImpl
import com.knexus.ergohabit.features.progreso.domain.repositories.ProgresoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProgresoModule {

    @Provides
    @Singleton
    fun provideProgresoApi(retrofit: Retrofit): ProgresoApi {
        return retrofit.create(ProgresoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProgresoRepository(
        api: ProgresoApi,
        dao: com.knexus.ergohabit.core.database.dao.ProgresoDao
    ): ProgresoRepository {
        return ProgresoRepositoryImpl(api, dao)
    }
}
