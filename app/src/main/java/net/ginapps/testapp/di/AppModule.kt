package net.ginapps.testapp.di

import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.MainCoroutineContext

@Module
object AppModule {
    @Provides
    @Singleton
    fun provideMainCoroutineContext() = MainCoroutineContext(Dispatchers.Main)

    @Provides
    @Singleton
    fun provideIoCoroutineContext() = IoCoroutineContext(Dispatchers.IO)
}
