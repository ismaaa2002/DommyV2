package com.tuapp.dommy

import android.app.Dialog
import android.app.NotificationChannel
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
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.view.Window
import android.widget.Button
import androidx.core.app.NotificationCompat



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
                R.id.btnDoors -> android.content.Intent(this, features.DoorsActivity::class.java)
                R.id.btnLights -> android.content.Intent(this, features.LightsActivity::class.java)
                R.id.btnWifi -> android.content.Intent(this, features.WifiActivity::class.java)
                R.id.btnAC -> android.content.Intent(this, features.ACActivity::class.java)
                R.id.btnInfo -> android.content.Intent(this, features.InfoActivity::class.java)
                R.id.btnMap -> android.content.Intent(this, features.MapActivity::class.java)
                R.id.btnTV -> android.content.Intent(this, features.TvActivity::class.java)

                // 🚨 Aquí es donde cambiamos el comportamiento
                R.id.btnChat -> {
                    showChatConfirmationDialog()
                    null
                }

                else -> null
            }


            if (id != R.id.btnChat) {
                intent?.let {
                    startActivity(it)
                }
                Toast.makeText(this, "$label pulsado", Toast.LENGTH_SHORT).show()
            }
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



        val prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE)
        val endTime = prefs.getLong("end_time", 0)
        val isRunning = prefs.getBoolean("running", false)

        if (isRunning && endTime > 0) {
            val timeLeft = endTime - System.currentTimeMillis()
            if (timeLeft <= 0) {
                // ya terminó
                apagarAire() // tu función
                mostrarNotificacion()
                prefs.edit().putBoolean("running", false).apply()
            } else {
                // todavía le queda tiempo — podrías mostrarlo
            }
        }


    }

    private fun apagarAire() {
        Toast.makeText(this, "Aire apagado automáticamente", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarNotificacion() {
        val channelId = "air_timer_channel"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Temporizador Aire",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_power_off)
            .setContentTitle("Aire acondicionado")
            .setContentText("El temporizador ha finalizado. El aire se apagará.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(1001, notification)
    }

    private fun showChatConfirmationDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_chat)
        dialog.setCancelable(false)

        val btnYes = dialog.findViewById<Button>(R.id.confirm_yes_button)
        val btnNo = dialog.findViewById<Button>(R.id.confirm_no_button)

        btnYes.setOnClickListener {
            val phone = "+34123456789" // Sustituye por el número real
            val uri = Uri.parse("https://wa.me/${phone.replace("+", "")}")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


    }








}
