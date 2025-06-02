package InfoFeatures

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R
import android.view.animation.AnimationUtils

class CategoryDetailActivity : AppCompatActivity() {

    private val categoryUrls = mapOf(
        "food" to "https://botin.es",
        "bars" to "https://circulobellasartes.com/azotea",
        "parks" to "https://esmadrid.com/informacion-turistica/parque-del-retiro",
        "health" to "https://comunidad.madrid/centros",
        "excursion" to "https://toledomonumental.com",
        "shopping" to "https://granviademadrid.es",
        "transport" to "https://metromadrid.es",
        "culture" to "https://museodelprado.es",
        "activities" to "https://madridsecreto.co/tours/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        val category = intent.getStringExtra("category") ?: "unknown"
        val contentContainer = findViewById<LinearLayout>(R.id.contentContainer)
        val titleView = findViewById<TextView>(R.id.categoryTitle)

        titleView.text = getCategoryTitle(category)

        val layoutId = when (category) {
            "food" -> R.layout.content_food
            "bars" -> R.layout.content_bars
            "parks" -> R.layout.content_parks
            "health" -> R.layout.content_health
            "excursion" -> R.layout.content_excursion
            "shopping" -> R.layout.content_shopping
            "transport" -> R.layout.content_transport
            "culture" -> R.layout.content_culture
            "activities" -> R.layout.content_activities
            else -> R.layout.content_empty
        }

        val contentView: View = LayoutInflater.from(this).inflate(layoutId, contentContainer, false)
        contentContainer.addView(contentView)

        // Configurar botón si hay URL disponible
        categoryUrls[category]?.let { url ->
            val button = contentView.findViewById<Button>(R.id.visitWebsiteButton1)
            button?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }



// Animaciones
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)

        findViewById<ImageButton>(R.id.backButton)?.startAnimation(fadeIn)
        findViewById<TextView>(R.id.categoryTitle)?.startAnimation(fadeIn)
        findViewById<LinearLayout>(R.id.contentContainer)?.startAnimation(slideIn)


    }

    private fun getCategoryTitle(category: String): String {
        return when (category) {
            "food" -> "Dónde comer en Madrid"
            "bars" -> "Dónde beber en Madrid"
            "parks" -> "Parques de Madrid"
            "health" -> "Salud en Madrid"
            "excursion" -> "Excursiones de un día"
            "shopping" -> "Zonas de compras"
            "transport" -> "Transporte público"
            "culture" -> "Lugares culturales"
            "activities" -> "Actividades para hacer"
            else -> "Categoría desconocida"
        }
    }
}
