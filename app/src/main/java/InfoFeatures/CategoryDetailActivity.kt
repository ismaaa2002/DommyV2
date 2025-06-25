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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R
import android.view.animation.AnimationUtils

class CategoryDetailActivity : AppCompatActivity() {

    private val categoryUrls = mapOf(
        "food" to "https://botin.es",  // Solo se usa si hay un botón único
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

        // Si es categoría de comida, configuramos los 10 botones por separado
        if (category == "food") {
            contentView.findViewById<Button>(R.id.visitWebsiteButton1)?.setOnClickListener {
                openUrl("https://botin.es")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton2)?.setOnClickListener {
                openUrl("https://www.casalucio.es")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton3)?.setOnClickListener {
                openUrl("https://www.streetxo.com")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton4)?.setOnClickListener {
                openUrl("https://www.mercadodesanmiguel.es")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton5)?.setOnClickListener {
                openUrl("https://www.lhardy.com")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton6)?.setOnClickListener {
                openUrl("https://elcluballard.com")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton7)?.setOnClickListener {
                openUrl("https://www.saladedespiece.com")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton8)?.setOnClickListener {
                openUrl("https://www.casadani.es")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton9)?.setOnClickListener {
                openUrl("https://www.tabernasanlúcar.com")
            }
            contentView.findViewById<Button>(R.id.visitWebsiteButton10)?.setOnClickListener {
                openUrl("https://www.ramenkagura.com")
            }
        } else {
            // Otras categorías con un solo botón
            categoryUrls[category]?.let { url ->
                contentView.findViewById<Button>(R.id.visitWebsiteButton1)?.setOnClickListener {
                    openUrl(url)
                }
            }
        }

        // Botón de volver atrás
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

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                showError("No se pudo abrir el enlace")
            }
        } catch (e: Exception) {
            showError("Error al abrir el enlace")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
