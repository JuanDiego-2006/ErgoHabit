package com.knexus.ergohabit.features.perfil.di

import android.content.Context
import com.knexus.ergohabit.features.perfil.data.datasource.api.PerfilApi
import com.knexus.ergohabit.features.perfil.data.repositories.PerfilRepositoryImpl
import com.knexus.ergohabit.features.perfil.domain.repositories.PerfilRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PerfilModule {

    @Provides
    @Singleton
    fun providePerfilApi(retrofit: Retrofit): PerfilApi {
        return retrofit.create(PerfilApi::class.java)
    }

    @Provides
    @Singleton
    fun providePerfilRepository(
        api: PerfilApi,
        @ApplicationContext context: Context
    ): PerfilRepository {
        return PerfilRepositoryImpl(api, context)
    }
}
