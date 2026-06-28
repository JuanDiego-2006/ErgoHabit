package com.knexus.ergohabit.features.posture.di

import com.knexus.ergohabit.features.posture.data.datasource.api.AuthApi
import com.knexus.ergohabit.features.posture.data.datasource.api.PosturaApi
import com.knexus.ergohabit.features.posture.data.mapper.PostureMapper
import com.knexus.ergohabit.features.posture.data.repository.AuthRepositoryImpl
import com.knexus.ergohabit.features.posture.data.repository.PostureRepositoryImpl
import com.knexus.ergohabit.features.posture.domain.repository.AuthRepository
import com.knexus.ergohabit.features.posture.domain.repository.PostureRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PostureModule {

    @Binds
    @Singleton
    abstract fun bindPostureRepository(
        postureRepositoryImpl: PostureRepositoryImpl
    ): PostureRepository

    // --- CAMBIO REALIZADO: Vinculación del repositorio de autenticación ---
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    // ----------------------------------------------------------------------

    companion object {
        @Provides
        @Singleton
        fun providePostureMapper(): PostureMapper = PostureMapper()

        // --- CAMBIO REALIZADO: Provisión de AuthApi para la conexión con la API local ---
        @Provides
        @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi {
            return retrofit.create(AuthApi::class.java)
        }

        @Provides
        @Singleton
        fun providePosturaApi(retrofit: Retrofit): PosturaApi {
            return retrofit.create(PosturaApi::class.java)
        }
    }
}
