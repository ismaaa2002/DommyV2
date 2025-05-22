package utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.dommyv2.R
import com.google.android.material.snackbar.Snackbar

object SnackbarUtil {
    fun showCustomSnackbar(view: View, context: Context, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)

        // Fondo personalizado
        snackbar.view.setBackgroundResource(R.drawable.rounded_dark_background)

        // Texto personalizado
        val snackbarText = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        snackbarText.setTextColor(Color.WHITE)
        snackbarText.textSize = 16f
        snackbarText.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
        snackbarText.textAlignment = View.TEXT_ALIGNMENT_CENTER

        snackbar.show()
    }
}
