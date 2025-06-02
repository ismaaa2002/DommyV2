package InfoFeatures

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R
import com.google.android.material.button.MaterialButton

class StayInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stay_info) // ✅ Primero se carga el layout

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        val buttons = listOf(
            R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
            R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn10
        )

        buttons.forEach { id ->
            findViewById<MaterialButton>(id)?.startAnimation(fadeIn)
        }

        val btnAccept = findViewById<MaterialButton>(R.id.btnAcceptRules)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        btnAccept.setOnClickListener {
            prefs.edit().putBoolean("rulesAccepted", true).apply()
            Toast.makeText(this, "Gracias por aceptar las normas.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onBackPressed() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val accepted = prefs.getBoolean("rulesAccepted", false)

        if (accepted) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Debes aceptar las normas para continuar.", Toast.LENGTH_SHORT).show()
        }
    }
}
