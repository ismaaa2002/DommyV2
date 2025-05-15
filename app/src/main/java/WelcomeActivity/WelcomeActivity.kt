package WelcomeActivity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dommyv2.R
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.widget.ImageView
import com.google.android.material.textfield.TextInputLayout


class WelcomeActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)

        val logo = findViewById<ImageView>(R.id.logoImageView)
        val slogan = findViewById<TextView>(R.id.sloganText)
        val date = findViewById<TextView>(R.id.dateTimeText)
        val weather = findViewById<TextView>(R.id.weatherText)
        val languageInput = findViewById<TextInputLayout>(R.id.languageInputLayout)
        val continueBtn = findViewById<Button>(R.id.continueButton)
        val helpBtn = findViewById<FloatingActionButton>(R.id.helpButton)
        val tipBtn = findViewById<FloatingActionButton>(R.id.tipButton)

        logo.startAnimation(fadeIn)

        slogan.postDelayed({ slogan.startAnimation(fadeIn) }, 200)
        date.postDelayed({ date.startAnimation(fadeIn) }, 300)
        weather.postDelayed({ weather.startAnimation(fadeIn) }, 400)
        languageInput.postDelayed({ languageInput.startAnimation(slideIn) }, 500)
        continueBtn.postDelayed({ continueBtn.startAnimation(slideIn) }, 600)
        helpBtn.postDelayed({ helpBtn.startAnimation(scaleIn) }, 700)
        tipBtn.postDelayed({ tipBtn.startAnimation(scaleIn) }, 700)



        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fecha y hora actual
        val dateTimeText: TextView = findViewById(R.id.dateTimeText)
        val formatter = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
        val now = Date()
        dateTimeText.text = formatter.format(now)

        // Clima
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            getUserLocation()
        }

        // Selector de idioma
        val languageSelector: MaterialAutoCompleteTextView = findViewById(R.id.languageSelector)
        val languages = listOf("Español", "English", "Deutsch", "Français")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        languageSelector.setAdapter(adapter)

        // Botón continuar
        val continueButton: Button = findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val selectedLanguage = languageSelector.text.toString()
            Toast.makeText(this, "Idioma seleccionado: $selectedLanguage", Toast.LENGTH_SHORT).show()
        }

        // Botón ayuda
        val helpButton: FloatingActionButton = findViewById(R.id.helpButton)
        helpButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.custom_help_dialog, null)
            val dialog = AlertDialog.Builder(this).setView(dialogView).create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val okButton = dialogView.findViewById<Button>(R.id.help_ok_button)
            okButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        // Botón tip
        val tipButton: FloatingActionButton = findViewById(R.id.tipButton)
        tipButton.setOnClickListener {
            val tipText = getTipOfDay()
            val dialogView = layoutInflater.inflate(R.layout.custom_tip_dialog, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialogView.findViewById<TextView>(R.id.tip_message).text = tipText

            dialogView.findViewById<Button>(R.id.tip_ok_button).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }


    }

    private fun getUserLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)

                val location = locationResult.lastLocation
                val lat: Double
                val lon: Double
                val isDefaultLocation: Boolean

                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    isDefaultLocation = false
                    Log.d("CLIMA", "📍 Ubicación real obtenida: $lat, $lon")
                } else {
                    lat = 40.4168
                    lon = -3.7038
                    isDefaultLocation = true
                    Log.d("CLIMA", "⚠️ Ubicación no disponible. Usando Madrid por defecto: $lat, $lon")
                }

                getWeatherFromAPI(lat, lon, isDefaultLocation)
            }
        }, Looper.getMainLooper())
    }

    private fun getWeatherFromAPI(lat: Double, lon: Double, isDefaultLocation: Boolean) {
        val apiKey = "877bc5be5d214009503712f69d1e1510"
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&lang=es&appid=$apiKey"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    findViewById<TextView>(R.id.weatherText).text = "Clima no disponible"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    try {
                        val json = JSONObject(it)
                        if (json.has("main") && json.has("weather")) {
                            val temp = json.getJSONObject("main").getDouble("temp").toInt()
                            val desc = json.getJSONArray("weather").getJSONObject(0).getString("description")
                            runOnUiThread {
                                val emoji = getWeatherEmoji(desc)
                                val citySuffix = if (isDefaultLocation) " • Clima en Madrid" else ""
                                findViewById<TextView>(R.id.weatherText).text =
                                    "$temp°C • ${desc.replaceFirstChar { it.uppercase() }} $emoji$citySuffix"

                            }
                        } else {
                            runOnUiThread {
                                findViewById<TextView>(R.id.weatherText).text = "No se pudo cargar el clima"
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            findViewById<TextView>(R.id.weatherText).text = "Error al leer clima"
                        }
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private val dailyTips = listOf(
        "Recuerda cerrar bien la puerta al salir.",
        "Explora los restaurantes cercanos recomendados en recepción.",
        "Hoy puede ser buen día para visitar el centro histórico.",
        "Aprovecha el WiFi gratuito disponible en todo el alojamiento.",
        "¿Sabías que puedes extender tu estancia desde la app?",
        "Consulta los horarios de check-out en la sección de ayuda.",
        "El transporte público está a solo unos minutos a pie.",
        "Si necesitas algo, contáctanos desde el menú de soporte.",
        "No olvides llevar tu tarjeta llave si sales por la noche.",
        "Revisa el tiempo en la app antes de salir.",
        "Puedes solicitar limpieza adicional desde la configuración.",
        "¿Llegas tarde? Avísanos por mensaje desde la app.",
        "Disfruta de descuentos exclusivos en actividades cercanas.",
        "Revisa nuestras recomendaciones culturales en la sección Info.",
        "Guarda tu idioma preferido: lo recordaremos para la próxima.",
        "Activa las notificaciones para no perderte actualizaciones.",
        "Consulta el manual del alojamiento en el apartado 'Mi Estancia'.",
        "¿Viajas con niños? Disponemos de kits especiales bajo petición.",
        "Los supermercados más cercanos están indicados en el mapa.",
        "¿Problemas con la calefacción o A/C? Repórtalo desde la app.",
        "Haz check-out más rápido usando el botón en la pantalla principal.",
        "Puedes dejar tu opinión desde la app. ¡Nos importa mucho!",
        "Revisa las normas del alojamiento para evitar cargos extra.",
        "Evita molestias: respeta el horario de descanso del edificio.",
        "Haz una foto de tu plaza de parking si tienes una asignada.",
        "Sácale provecho a tu estancia: descubre qué hacer hoy en la app."
    )


    private fun getTipOfDay(): String {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return dailyTips[dayOfYear % dailyTips.size]
    }
    private fun getWeatherEmoji(description: String): String {
        return when {
            "lluvia" in description -> "🌧️"
            "tormenta" in description -> "⛈️"
            "nieve" in description -> "❄️"
            "nublado" in description || "nuboso" in description -> "⛅"
            "cielo claro" in description || "despejado" in description -> "☀️"
            "niebla" in description || "bruma" in description -> "🌫️"
            else -> "🌤️"
        }
    }


}
