package com.adminapp.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.adminapp.R
import com.adminapp.prefrences.Preference
import com.adminapp.ui.employee_list_activity.EmployeeListActivity
import com.adminapp.ui.employee_login.EmployeeLoginActivity
import com.adminapp.ui.main.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("SplashActivity", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            preference.setUserEmail(token)
            // Log and toast
            /* val msg = getString(R.string.msg_token_fmt, token)
             Log.d("SplashActivity", msg)
             Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()*/
        })

        Handler(Looper.getMainLooper()).postDelayed({
            if (!preference.getUserId().isNullOrEmpty()) {
                startActivity(Intent(this@SplashActivity, EmployeeListActivity::class.java))
            } else if (preference.isAdmin())
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            else
                startActivity(Intent(this@SplashActivity, EmployeeLoginActivity::class.java))

            finish()
        }, 2000)

    }


}