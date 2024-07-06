package net.ginapps.testapp.screen.account

import android.os.Bundle
import androidx.activity.compose.setContent
import net.ginapps.testapp.core.BaseActivity
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
