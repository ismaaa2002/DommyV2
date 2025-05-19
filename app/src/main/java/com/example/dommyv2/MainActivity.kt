package com.tuapp.dommy

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R
import android.view.animation.AnimationUtils
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import android.os.Build
import kotlin.jvm.java


class MainActivity : AppCompatActivity() {

    private fun setupButton(id: Int, label: String) {
        val button = findViewById<LinearLayout>(id)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(30)
            }

            // NAVEGACIÓN A OTRAS ACTIVIDADES
            val intent = when (id) {
                R.id.btnDoors -> android.content.Intent(this, Features.DoorsActivity::class.java)
                R.id.btnLights -> android.content.Intent(this, Features.LightsActivity::class.java)
                R.id.btnWifi -> android.content.Intent(this, Features.WifiActivity::class.java)
                R.id.btnAC -> android.content.Intent(this, Features.ACActivity::class.java)
                R.id.btnInfo -> android.content.Intent(this, Features.InfoActivity::class.java)
                R.id.btnChat -> android.content.Intent(this, Features.ChatActivity::class.java)
                R.id.btnMap -> android.content.Intent(this, Features.MapActivity::class.java)
                R.id.btnTV -> android.content.Intent(this, Features.TvActivity::class.java)
                else -> null
            }

            intent?.let {
                startActivity(it)
            }

            Toast.makeText(this, "$label pulsado", Toast.LENGTH_SHORT).show()
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





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val anim = AnimationUtils.loadAnimation(this, R.anim.button_fade_in)

        val buttons = listOf(
            R.id.btnDoors, R.id.btnLights, R.id.btnWifi, R.id.btnAC,
            R.id.btnInfo, R.id.btnChat, R.id.btnMap, R.id.btnTV
        )

        buttons.forEachIndexed { index, id ->
            val button = findViewById<LinearLayout>(id)

            // Delay animación escalonada
            button.postDelayed({
                button.startAnimation(anim)
            }, index * 100L) // 100ms de diferencia entre cada botón

            // También aplica la lógica de pulsado
            setupButton(id, resources.getResourceEntryName(id).removePrefix("btn").uppercase())
        }
    }


}
