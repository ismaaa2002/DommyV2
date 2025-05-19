package Features

import android.content.Context
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

class DoorsActivity : AppCompatActivity() {

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doors)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setupButton(R.id.btnBuildingDoor, "Building Door")
        setupButton(R.id.btnApartmentDoor, "Apartment Door")
    }

    private fun setupButton(id: Int, label: String) {
        val button = findViewById<LinearLayout>(id)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        button.setOnClickListener {
            vibrate()
            Toast.makeText(this, "$label pulsado", Toast.LENGTH_SHORT).show()
        }

        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> view.startAnimation(scaleUp)
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> view.startAnimation(scaleDown)
            }
            false
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
