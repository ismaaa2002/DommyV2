package features

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dommyv2.R

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val logo = findViewById<ImageView>(R.id.logoMinimal)
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.scale_fade_in)
        logo.startAnimation(logoAnim)




        // Ajuste de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los botones (LinearLayout)
        val btnVideo = findViewById<LinearLayout>(R.id.btnVideo)
        val btnSeeMadrid = findViewById<LinearLayout>(R.id.btnSeeMadrid)
        val btnStayInfo = findViewById<LinearLayout>(R.id.btnStayInfo)

        // Animación fade-in
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        btnVideo.startAnimation(fadeIn)
        btnSeeMadrid.startAnimation(fadeIn)
        btnStayInfo.startAnimation(fadeIn)

        // Lógica de botones (a completar más adelante)
        btnVideo.setOnClickListener {
            startActivity(Intent(this, InfoFeatures.VideoActivity::class.java))
        }

        btnSeeMadrid.setOnClickListener {
            startActivity(Intent(this, InfoFeatures.MadridActivity::class.java))
        }

        btnStayInfo.setOnClickListener {
            startActivity(Intent(this, InfoFeatures.StayInfoActivity::class.java))
        }






    }
}
