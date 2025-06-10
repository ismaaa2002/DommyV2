package features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R

class ServicesActivity : AppCompatActivity() {

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setupServiceButton(R.id.btnUberEats, "Uber Eats", "com.ubercab.eats", "https://www.ubereats.com")
        setupServiceButton(R.id.btnUberRide, "Uber Transporte", "com.ubercab", "https://m.uber.com")
        setupServiceButton(R.id.btnGlovo, "Glovo", "com.glovo", "https://glovoapp.com")
    }

    private fun setupServiceButton(id: Int, label: String, packageName: String, webUrl: String) {
        val button = findViewById<LinearLayout>(id)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        button.setOnClickListener {
            vibrate()
            openAppOrWeb(packageName, webUrl)
        }

        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> view.startAnimation(scaleUp)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> view.startAnimation(scaleDown)
            }
            false
        }
    }

    private fun openAppOrWeb(packageName: String, webUrl: String) {
        val pm = packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
            startActivity(browserIntent)
        }
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    }
}
