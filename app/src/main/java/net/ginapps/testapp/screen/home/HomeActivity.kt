package net.ginapps.testapp.screen.home

import android.os.Bundle
import androidx.activity.compose.setContent
import net.ginapps.testapp.core.BaseActivity
import net.ginapps.testapp.screen.home.account.AccountScreen
import net.ginapps.testapp.ui.theme.MyApplicationTheme

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                AccountScreen()
            }
        }
    }
}
