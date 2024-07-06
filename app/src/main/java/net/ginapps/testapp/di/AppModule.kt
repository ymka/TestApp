package net.ginapps.testapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.MainCoroutineContext
import net.ginapps.testapp.repository.ConnectionRepository
import net.ginapps.testapp.repository.DefaultWeb3Repository
import net.ginapps.testapp.repository.UserRepository
import net.ginapps.testapp.repository.Web3Repository
import net.ginapps.testapp.repository.Web3UserRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMainCoroutineContext() = MainCoroutineContext(Dispatchers.Main)

    @Provides
    @Singleton
    fun provideIoCoroutineContext() = IoCoroutineContext(Dispatchers.IO)

    @Provides
    @Singleton
    fun provideDefaultWeb3Repository(ioCoroutineContext: IoCoroutineContext) =
        DefaultWeb3Repository(ioCoroutineContext)

    @Provides
    @Singleton
    fun provideWeb3Repository(repository: DefaultWeb3Repository): Web3Repository = repository

    @Provides
    @Singleton
    fun provideConnectionRepository(repository: DefaultWeb3Repository): ConnectionRepository =
        repository

    @Provides
    @Singleton
    fun provideUserRepository(io: IoCoroutineContext): UserRepository = Web3UserRepository(io)
}
