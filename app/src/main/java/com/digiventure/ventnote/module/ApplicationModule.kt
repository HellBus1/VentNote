package com.digiventure.ventnote.module

import android.content.Context
import com.digiventure.ventnote.data.local.NoteDataStore
import com.digiventure.ventnote.feature.widget.NoteWidgetRefresher
import com.digiventure.ventnote.feature.widget.WidgetRefresher
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {
    @Binds
    @Singleton
    abstract fun bindWidgetRefresher(refresher: NoteWidgetRefresher): WidgetRefresher

    companion object {
        @Provides
        @Singleton
        fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())

        @Provides
        @Singleton
        fun provideExecutorCoroutineDispatcher(): ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

        @Provides
        @Singleton
        fun provideNoteDataStore(@ApplicationContext context: Context): NoteDataStore {
            return NoteDataStore(context)
        }
    }
}