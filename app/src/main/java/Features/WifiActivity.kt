package Features

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R

class WifiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        val btnCopy = findViewById<Button>(R.id.btnCopyPassword)
        val qrImage = findViewById<ImageView>(R.id.qrCodeImage)

        btnCopy.setOnClickListener {
            val password = "welcome1"
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("WiFi Password", password)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Contraseña copiada al portapapeles", Toast.LENGTH_SHORT).show()
        }

        qrImage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
    }
}
