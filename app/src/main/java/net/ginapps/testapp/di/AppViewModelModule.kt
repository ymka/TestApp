package net.ginapps.testapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import net.ginapps.testapp.repository.Web3InRepository
import net.ginapps.testapp.repository.SettingsRepository
import net.ginapps.testapp.repository.UserRepository
import net.ginapps.testapp.repository.SignInRepository
import net.ginapps.testapp.usecase.AppInitUseCase
import net.ginapps.testapp.usecase.CompleteSignInUseCase
import net.ginapps.testapp.usecase.DefaultAppInitUseCase
import net.ginapps.testapp.usecase.DefaultCompleteSignInUseCase
import net.ginapps.testapp.usecase.DefaultLogOutUseCase
import net.ginapps.testapp.usecase.LogOutUseCase
import net.ginapps.testapp.usecase.MsgSignatureVerifyUseCase
import net.ginapps.testapp.usecase.SIWERequestUseCase
import net.ginapps.testapp.usecase.Web3ModalSIWERequestUseCase
import net.ginapps.testapp.usecase.WebMsgSignatureVerifyUseCase

@Module
@InstallIn(ViewModelComponent::class)
object AppViewModelModule {
    @ViewModelScoped
    @Provides
    fun provideInitUseCase(
        repository: Web3InRepository,
        userRepository: UserRepository,
    ): AppInitUseCase = DefaultAppInitUseCase(repository, userRepository)

    @ViewModelScoped
    @Provides
    fun provideSIWEUseCase(): SIWERequestUseCase =
        Web3ModalSIWERequestUseCase()

    @ViewModelScoped
    @Provides
    fun provideMsgSignatureVerifyUseCase(): MsgSignatureVerifyUseCase =
        WebMsgSignatureVerifyUseCase()

    @ViewModelScoped
    @Provides
    fun provideCompleteSignInUseCase(
        settingsRepository: SettingsRepository,
        userRepository: UserRepository,
        web3UserRepository: SignInRepository,
    ): CompleteSignInUseCase =
        DefaultCompleteSignInUseCase(settingsRepository, userRepository, web3UserRepository)

    @ViewModelScoped
    @Provides
    fun provideLogOutUseCase(
        userRepository: UserRepository,
        settingsRepository: SettingsRepository,
        signInRepository: SignInRepository,
    ): LogOutUseCase = DefaultLogOutUseCase(userRepository, settingsRepository, signInRepository)
}
