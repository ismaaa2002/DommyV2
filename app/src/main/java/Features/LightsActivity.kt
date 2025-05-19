package Features

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.animation.AnimationUtils
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R

class LightsActivity : AppCompatActivity() {

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setupSwitch(R.id.switchEntrada, "Entrada")
        setupSwitch(R.id.switchSalon, "Salón")
        setupSwitch(R.id.switchCocina, "Cocina")
        setupSwitch(R.id.switchBano, "Baño")
        setupSwitch(R.id.switchDorm1, "Dormitorio 1")
        setupSwitch(R.id.switchDorm2, "Dormitorio 2")
    }

    private fun setupSwitch(id: Int, nombre: String) {
        val sw = findViewById<Switch>(id)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)

        sw.setOnCheckedChangeListener { buttonView, isChecked ->
            vibrate()
            buttonView.startAnimation(if (isChecked) scaleUp else scaleDown)

            val estado = if (isChecked) "encendida" else "apagada"
            Toast.makeText(this, "Luz de $nombre $estado", Toast.LENGTH_SHORT).show()
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
