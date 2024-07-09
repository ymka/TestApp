package net.ginapps.testapp.screen.home

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import net.ginapps.testapp.core.BaseActivity
import net.ginapps.testapp.ui.theme.MyApplicationTheme

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    @Inject
    lateinit var navigator: NavHostHomeNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                HomeNavHost(navController = rememberNavController(), navigator = navigator)
            }
        }
    }
}
