package net.ginapps.testapp.screen.launch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import net.ginapps.testapp.core.ViewState
import net.ginapps.testapp.screen.signin.SignInActivity

class LaunchActivity : ComponentActivity() {

    private val viewModel: LaunchViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.launch()
        installSplashScreen()
            .setKeepOnScreenCondition {
                when (val value = viewModel.state.value) {
                    is ViewState.Loading -> true
                    is ViewState.Success -> {
                        val intent = when (value.data) {
                            is Route.Sign -> {
                                Intent(this, SignInActivity::class.java)
                            }
                            is Route.Home -> {
                                Intent(this, SignInActivity::class.java)
                            }
                        }

                        startActivity(intent)

                        false
                    }
                    is ViewState.Error -> {
                        false
                    }
                }
            }
    }
}
