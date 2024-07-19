package net.ginapps.testapp.usecase

import net.ginapps.testapp.repository.SettingsRepository
import net.ginapps.testapp.repository.UserRepository
import net.ginapps.testapp.repository.SignInRepository

interface CompleteSignInUseCase {
    suspend fun run()
}

class DefaultCompleteSignInUseCase(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val signInRepository: SignInRepository,
) : CompleteSignInUseCase {
    override suspend fun run() {
        signInRepository.lastSentMessage = null
        settingsRepository.signInCompleted = true
        signInRepository.resetState()
        userRepository.fetchData()
    }
}
