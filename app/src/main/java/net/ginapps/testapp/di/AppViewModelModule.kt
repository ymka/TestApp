package net.ginapps.testapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import net.ginapps.testapp.repository.DefaultWeb3Repository
import net.ginapps.testapp.repository.UserRepository
import net.ginapps.testapp.usecase.AppInitUseCase
import net.ginapps.testapp.usecase.DefaultAppInitUseCase
import net.ginapps.testapp.usecase.SIWEUseCase
import net.ginapps.testapp.usecase.Web3ModalSIWEUseCase

@Module
@InstallIn(ViewModelComponent::class)
object AppViewModelModule {
    @ViewModelScoped
    @Provides
    fun provideInitUseCase(
        repository: DefaultWeb3Repository,
        userRepository: UserRepository,
    ): AppInitUseCase = DefaultAppInitUseCase(repository, userRepository)

    @ViewModelScoped
    @Provides
    fun provideSIWEUseCase(userRepository: UserRepository): SIWEUseCase =
        Web3ModalSIWEUseCase(userRepository)
}
