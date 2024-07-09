package net.ginapps.testapp.screen.signin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import net.ginapps.testapp.core.BaseActivity
import net.ginapps.testapp.ui.theme.MyApplicationTheme

@AndroidEntryPoint
class SignInActivity : BaseActivity() {

    @Inject
    lateinit var navigator: NavHostSigInNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                SignInNavHost(navController = rememberNavController(), navigator = navigator)
            }
        }
    }
}
