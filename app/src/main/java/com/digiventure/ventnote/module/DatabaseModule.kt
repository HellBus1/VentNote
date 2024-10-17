package com.digiventure.ventnote.module

import android.app.Application
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabaseProxy(application: Application) = DatabaseProxy(application)

    @Provides
    fun provideDatabase(proxy: DatabaseProxy) = proxy.getObject()
}