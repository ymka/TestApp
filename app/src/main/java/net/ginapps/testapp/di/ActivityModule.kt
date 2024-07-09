package net.ginapps.testapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import net.ginapps.testapp.core.MainCoroutineContext
import net.ginapps.testapp.screen.home.HomeNavigator
import net.ginapps.testapp.screen.home.NavHostHomeNavigator

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {

    @ActivityRetainedScoped
    @Provides
    fun provideNavHostLoginNavigator(context: MainCoroutineContext) = NavHostHomeNavigator(context)

    @ActivityRetainedScoped
    @Provides
    fun provideHomeNavigator(navigator: NavHostHomeNavigator): HomeNavigator = navigator

}
