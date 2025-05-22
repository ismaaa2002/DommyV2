package InfoFeatures

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R

class CategoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        val category = intent.getStringExtra("category") ?: "unknown"
        val textView = findViewById<TextView>(R.id.categoryTitle)

        textView.text = when (category) {
            "food" -> "Lugares para comer"
            "bars" -> "Bares recomendados"
            "culture" -> "Lugares culturales"
            "activities" -> "Actividades disponibles"
            "excursion" -> "Excursiones de un día"
            "parks" -> "Parques en Madrid"
            "transport" -> "Transporte público"
            "shopping" -> "Zonas de compras"
            "health" -> "Centros de salud"
            else -> "Categoría no reconocida"
        }
    }
}
