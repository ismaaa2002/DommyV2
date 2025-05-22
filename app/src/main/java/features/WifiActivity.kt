package features

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import utils.SnackbarUtil


class WifiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        val btnCopy = findViewById<TextView>(R.id.btnCopyPassword)
        val qrImage = findViewById<ImageView>(R.id.qrCodeImage)
        val steps = findViewById<TextView>(R.id.wifiSteps)

        // Animaciones
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        steps.startAnimation(fadeIn)
        qrImage.startAnimation(scaleIn)
        btnCopy.startAnimation(scaleUp)

        // Acción copiar contraseña
        btnCopy.setOnClickListener {
            val password = "welcome1"
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("WiFi Password", password)
            clipboard.setPrimaryClip(clip)

            val snackbar = Snackbar.make(btnCopy, "Contraseña copiada ✅", Snackbar.LENGTH_SHORT)

// Personalización del estilo
            snackbar.view.setBackgroundResource(R.drawable.rounded_dark_background)

            val snackbarText = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            snackbarText.setTextColor(Color.WHITE)
            snackbarText.textSize = 16f
            snackbarText.typeface = ResourcesCompat.getFont(this, R.font.poppins_regular)
            snackbarText.textAlignment = View.TEXT_ALIGNMENT_CENTER

            snackbar.show()

        }


        // Acción del código QR
        qrImage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }

        val btnHelp = findViewById<ImageView>(R.id.btnHelp)
        btnHelp.startAnimation(scaleUp)
        btnHelp.setOnClickListener {
            SnackbarUtil.showCustomSnackbar(btnHelp, this, "Pronto estará disponible")
        }

    }


}
