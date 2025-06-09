package features

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dommyv2.R

class TvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tv)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val scaleUp = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_down)

        val btnPower = findViewById<LinearLayout>(R.id.btnPower)
        val btnVolumeUp = findViewById<LinearLayout>(R.id.btnVolumeUp)
        val btnVolumeDown = findViewById<LinearLayout>(R.id.btnVolumeDown)

        val buttons = listOf(btnPower, btnVolumeUp, btnVolumeDown)

        buttons.forEach { button ->
            button.setOnClickListener {
                val msg = when (button.id) {
                    R.id.btnPower -> "TV encendida / apagada"
                    R.id.btnVolumeUp -> "Subiendo volumen"
                    R.id.btnVolumeDown -> "Bajando volumen"
                    else -> ""
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }

            button.setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> view.startAnimation(scaleUp)
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> view.startAnimation(scaleDown)
                }
                false
            }
        }
    }

}
