package features

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat


class ACActivity : AppCompatActivity() {

    private lateinit var textTemperature: TextView
    private lateinit var buttonIncrease: ImageView
    private lateinit var buttonDecrease: ImageView
    private lateinit var buttonResetTemp: ImageView

    private lateinit var buttonTimerCard: LinearLayout
    private lateinit var buttonAutoModeCard: LinearLayout

    private lateinit var powerBox: LinearLayout
    private lateinit var powerIcon: ImageView
    private lateinit var powerText: TextView

    private lateinit var timerContainer: LinearLayout
    private lateinit var inputMinutes: EditText
    private lateinit var inputSeconds: EditText
    private lateinit var buttonStartTimer: Button
    private lateinit var buttonPauseTimer: Button
    private lateinit var buttonStopTimer: Button



    private var isPowerOn = false
    private var temperature = 22
    private var countdown: CountDownTimer? = null
    private var remainingTime: Long = 0
    private var isPaused = false

    private val PREFS = "TimerPrefs"
    private val KEY_REMAINING = "remaining_time"
    private val KEY_RUNNING = "timer_running"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acactivity)

        textTemperature = findViewById(R.id.textTemperature)
        buttonIncrease = findViewById(R.id.buttonIncrease)
        buttonDecrease = findViewById(R.id.buttonDecrease)
        buttonResetTemp = findViewById(R.id.buttonResetTemp)

        powerBox = findViewById(R.id.powerControlBox)
        powerIcon = findViewById(R.id.powerIcon)
        powerText = findViewById(R.id.powerText)

        buttonTimerCard = findViewById(R.id.buttonTimerCard)
        buttonAutoModeCard = findViewById(R.id.buttonAutoModeCard)

        timerContainer = findViewById(R.id.timerContainer)
        inputMinutes = findViewById(R.id.inputMinutes)
        inputSeconds = findViewById(R.id.inputSeconds)
        buttonStartTimer = findViewById(R.id.buttonStartTimer)
        buttonPauseTimer = findViewById(R.id.buttonPauseTimer)
        buttonStopTimer = findViewById(R.id.buttonStopTimer)

        actualizarTemperaturaConAnimacion()

        powerBox.setOnClickListener { togglePower() }

        buttonIncrease.setOnClickListener {
            animarBoton(it)
            if (temperature < 30) {
                temperature++
                animarCambioTemperatura()
            }
        }

        buttonDecrease.setOnClickListener {
            animarBoton(it)
            if (temperature > 16) {
                temperature--
                animarCambioTemperatura()
            }
        }

        buttonResetTemp.setOnClickListener {
            animarBoton(it)
            temperature = 22
            actualizarTemperaturaConAnimacion()
            Toast.makeText(this, "Temperatura reiniciada", Toast.LENGTH_SHORT).show()
        }


        buttonTimerCard.setOnClickListener {
            if (timerContainer.visibility == View.GONE) {
                timerContainer.visibility = View.VISIBLE
                timerContainer.alpha = 0f
                timerContainer.animate().alpha(1f).setDuration(300).start()
            } else {
                timerContainer.animate().alpha(0f).setDuration(300).withEndAction {
                    timerContainer.visibility = View.GONE
                }.start()
            }
        }



        buttonAutoModeCard.setOnClickListener {
            mostrarMensaje("Modo automático activado")
        }

        buttonStartTimer.setOnClickListener {
            val min = inputMinutes.text.toString().toIntOrNull() ?: 0
            val sec = inputSeconds.text.toString().toIntOrNull() ?: 0
            val totalMillis = (min * 60 + sec) * 1000L

            if (totalMillis > 0) {
                iniciarTemporizador(totalMillis)

                // ✅ GUARDAMOS TIEMPO DE FINALIZACIÓN
                val endTime = System.currentTimeMillis() + totalMillis
                val prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE).edit()
                prefs.putLong("end_time", endTime)
                prefs.putBoolean("running", true)
                prefs.apply()

                isPaused = false
                buttonPauseTimer.text = "Pausar"
                Toast.makeText(this, "Temporizador iniciado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Introduce un tiempo válido", Toast.LENGTH_SHORT).show()
            }
        }


        buttonPauseTimer.setOnClickListener {
            if (!isPaused) {
                countdown?.cancel()
                isPaused = true
                buttonPauseTimer.text = "Reanudar"
                Toast.makeText(this, "Temporizador pausado", Toast.LENGTH_SHORT).show()
            } else {
                iniciarTemporizador(remainingTime)
                isPaused = false
                buttonPauseTimer.text = "Pausar"
            }
        }

        buttonStopTimer.setOnClickListener {
            countdown?.cancel()
            inputMinutes.setText("")
            inputSeconds.setText("")
            buttonPauseTimer.text = "Pausar"
            isPaused = false
            remainingTime = 0
            Toast.makeText(this, "Temporizador detenido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun iniciarTemporizador(duration: Long) {
        countdown?.cancel()
        countdown = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                val totalSecs = millisUntilFinished / 1000
                val mins = (totalSecs / 60).toString().padStart(2, '0')
                val secs = (totalSecs % 60).toString().padStart(2, '0')
                inputMinutes.setText(mins)
                inputSeconds.setText(secs)
            }

            override fun onFinish() {
                if (isPowerOn) togglePower()
                mostrarNotificacionFinal()
                Toast.makeText(this@ACActivity, "Aire apagado por temporizador", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }


    private fun togglePower() {
        isPowerOn = !isPowerOn
        powerText.text = if (isPowerOn) "Encendido" else "Apagado"
        powerIcon.setImageResource(
            if (isPowerOn) R.drawable.ic_power_on
            else R.drawable.ic_power_off
        )
        animarBotonEncendido(powerBox)
        mostrarMensaje(if (isPowerOn) "Aire encendido" else "Aire apagado")
    }

    private fun actualizarTemperaturaConAnimacion() {
        textTemperature.text = "$temperature°C"
        val scale = ScaleAnimation(0.8f, 1f, 0.8f, 1f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f)
        scale.duration = 300
        scale.interpolator = BounceInterpolator()
        textTemperature.startAnimation(scale)
    }

    private fun animarCambioTemperatura() {
        val animator = ValueAnimator.ofInt(temperature - 1, temperature)
        animator.duration = 200
        animator.addUpdateListener { animation ->
            val temp = animation.animatedValue as Int
            textTemperature.text = "$temp°C"
        }
        animator.start()
        actualizarTemperaturaConAnimacion()
    }

    private fun animarBotonEncendido(view: View) {
        val anim = ObjectAnimator.ofFloat(view, "alpha", 0.6f, 1f)
        anim.duration = 300
        anim.start()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        val anim = AlphaAnimation(0.7f, 1f)
        anim.duration = 200
        anim.repeatMode = AlphaAnimation.REVERSE
        anim.repeatCount = 1
        powerBox.startAnimation(anim)
    }

    private fun animarBoton(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.85f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.85f, 1f)
        scaleX.duration = 150
        scaleY.duration = 150
        scaleX.start()
        scaleY.start()
    }

    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE).edit()
        prefs.putLong(KEY_REMAINING, remainingTime)
        prefs.putBoolean(KEY_RUNNING, !isPaused && remainingTime > 0)
        prefs.apply()
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val savedRemaining = prefs.getLong(KEY_REMAINING, 0L)
        val wasRunning = prefs.getBoolean(KEY_RUNNING, false)

        if (wasRunning && savedRemaining > 0) {
            iniciarTemporizador(savedRemaining)
        }
    }

    private fun mostrarNotificacionFinal() {
        val channelId = "air_timer_channel"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Temporizador Aire", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_power_off)
            .setContentTitle("Aire acondicionado")
            .setContentText("El temporizador ha terminado. El aire se apagará.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(1001, notification)
    }







}
