package com.knexus.ergohabit.features.posture.di

import com.knexus.ergohabit.features.posture.data.mapper.PostureMapper
import com.knexus.ergohabit.features.posture.data.repository.PostureRepositoryImpl
import com.knexus.ergohabit.features.posture.domain.repository.PostureRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PostureModule {

    @Binds
    @Singleton
    abstract fun bindPostureRepository(
        postureRepositoryImpl: PostureRepositoryImpl
    ): PostureRepository

    companion object {
        @Provides
        @Singleton
        fun providePostureMapper(): PostureMapper = PostureMapper()
    }
}
