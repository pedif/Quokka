package com.techspark.quokka.di

import com.techspark.core.data.IFeelRepository
import com.techspark.core.data.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFeelRepository(repo: IFeelRepository): Repository

}