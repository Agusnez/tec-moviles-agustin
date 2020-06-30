package us.example.entregable1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import us.example.entregable1.ui.login.LoginActivity


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
