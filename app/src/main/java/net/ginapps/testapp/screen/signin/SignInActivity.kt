package net.ginapps.testapp.screen.signin

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import net.ginapps.testapp.core.BaseActivity
import net.ginapps.testapp.ui.theme.MyApplicationTheme

@AndroidEntryPoint
class SignInActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                SigInScreen()
            }
        }
    }
}
