package net.ginapps.testapp.di

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import net.ginapps.testapp.core.IoCoroutineContext
import net.ginapps.testapp.core.MainCoroutineContext
import net.ginapps.testapp.repository.ConnectionRepository
import net.ginapps.testapp.repository.Web3InRepository
import net.ginapps.testapp.repository.SettingsRepository
import net.ginapps.testapp.repository.SharedPrefSettingsRepository
import net.ginapps.testapp.repository.UserRepository
import net.ginapps.testapp.repository.SignInRepository
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
    fun provideDefaultWeb3Repository(
        userRepository: UserRepository,
        ioCoroutineContext: IoCoroutineContext,
        @ApplicationContext context: Context,
    ) = Web3InRepository(
        userRepository,
        ioCoroutineContext,
        PreferenceManager.getDefaultSharedPreferences(context),
    )

    @Provides
    @Singleton
    fun provideWeb3Repository(repository: Web3InRepository): SignInRepository = repository

    @Provides
    @Singleton
    fun provideConnectionRepository(repository: Web3InRepository): ConnectionRepository =
        repository

    @Provides
    @Singleton
    fun provideUserRepository(
        settingsRepository: SettingsRepository,
        io: IoCoroutineContext
    ): UserRepository = Web3UserRepository(settingsRepository, io)

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository =
        SharedPrefSettingsRepository(PreferenceManager.getDefaultSharedPreferences(context))
}
