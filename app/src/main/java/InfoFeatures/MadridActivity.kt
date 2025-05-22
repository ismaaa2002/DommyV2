package InfoFeatures

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R

class MadridActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_madrid)

        setButtonAction(R.id.btnFood, "food")
        setButtonAction(R.id.btnBars, "bars")
        setButtonAction(R.id.btnCulture, "culture")
        setButtonAction(R.id.btnActivities, "activities")
        setButtonAction(R.id.btnExcursion, "excursion")
        setButtonAction(R.id.btnParks, "parks")
        setButtonAction(R.id.btnTransport, "transport")
        setButtonAction(R.id.btnShopping, "shopping")
        setButtonAction(R.id.btnHealth, "health")
    }

    private fun setButtonAction(buttonId: Int, category: String) {
        findViewById<LinearLayout>(buttonId).setOnClickListener {
            val intent = Intent(this, CategoryDetailActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
        }
    }
}
