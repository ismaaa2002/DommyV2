package WelcomeActivity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
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
    super.onCreate(savedInstanceState)
        applySavedLanguage()
        Log.d("IDIOMA", "applySavedLanguage ejecutado")

        enableEdgeToEdge()
    setContentView(R.layout.activity_welcome)




        // Configura el padding para evitar solapar los elementos con la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Carga animaciones desde /res/anim/
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        val scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        val fadeSlideDown = AnimationUtils.loadAnimation(this, R.anim.fade_slide_down)

        // Asigna vistas
        val logo = findViewById<ImageView>(R.id.logoImageView)
        val slogan = findViewById<TextView>(R.id.sloganText)
        val date = findViewById<TextView>(R.id.dateTimeText)
        val continueButton: Button = findViewById(R.id.continueButton)
        val weather = findViewById<TextView>(R.id.weatherText)
        val languageInput = findViewById<TextInputLayout>(R.id.languageInputLayout)
        val continueBtn = findViewById<Button>(R.id.continueButton)
        val helpBtn = findViewById<FloatingActionButton>(R.id.helpButton)
        val tipBtn = findViewById<FloatingActionButton>(R.id.tipButton)

        // Aplica animaciones secuenciales
        logo?.startAnimation(fadeSlideDown)
        slogan.postDelayed({ slogan.startAnimation(fadeIn) }, 200)
        date.postDelayed({ date.startAnimation(fadeIn) }, 300)
        weather.postDelayed({ weather.startAnimation(fadeIn) }, 400)
        languageInput.postDelayed({ languageInput.startAnimation(slideIn) }, 500)
        continueBtn.postDelayed({ continueBtn.startAnimation(slideIn) }, 600)
        helpBtn.postDelayed({ helpBtn.startAnimation(scaleIn) }, 700)
        tipBtn.postDelayed({ tipBtn.startAnimation(scaleIn) }, 700)

        // Muestra la fecha y hora actual
        val dateTimeText: TextView = findViewById(R.id.dateTimeText)
        val formatter = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
        val now = Date()
        dateTimeText.text = formatter.format(now)

        // Inicializa el servicio de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            getUserLocation()
        }

        // Configura el selector de idioma
        val languageSelector: MaterialAutoCompleteTextView = findViewById(R.id.languageSelector)
        val languages = listOf("Español", "English", "Français", "Русский", "中文", "العربية")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        languageSelector.setAdapter(adapter)

    // Acción al pulsar CONTINUAR
    continueButton.setOnClickListener {
        val selectedLanguage = languageSelector.text.toString()

        if (selectedLanguage.isBlank()) {
            Toast.makeText(this, getString(R.string.please_select_language), Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        val languageCode = when (selectedLanguage.lowercase()) {
            "español" -> "es"
            "english" -> "en"
            "français" -> "fr"
            "русский" -> "ru"
            "中文" -> "zh"
            "العربية" -> "ar"
            else -> "en"
        }


        saveLanguageToPreferences(languageCode)
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        // reinicia la actividad

    }


        // Botón de ayuda: muestra diálogo personalizado con fondo oscuro
        val helpButton: FloatingActionButton = findViewById(R.id.helpButton)
        helpButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.custom_help_dialog, null)
            val dialog = AlertDialog.Builder(this).setView(dialogView).create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val okButton = dialogView.findViewById<Button>(R.id.help_ok_button)
            okButton.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

        // Botón de tip del día: muestra consejo diario en diálogo estilizado
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

    /** Obtiene la ubicación actual del usuario o usa Madrid por defecto si falla. */
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

    /** Llama a la API de clima y muestra la temperatura, descripción y un emoji correspondiente. */
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

    // Lista de consejos diarios que rotan automáticamente
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

    /** Devuelve el tip del día según el día del año */
    private fun getTipOfDay(): String {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return dailyTips[dayOfYear % dailyTips.size]
    }

    /** Devuelve un emoji según la descripción del clima */
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


    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("selected_language", "es") ?: "es"

        Log.d("IDIOMA", "attachBaseContext → idioma guardado: $lang")

        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }


    fun saveLanguageToPreferences(languageCode: String) {
        Log.d("IDIOMA", "Guardando idioma: $languageCode")
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putString("selected_language", languageCode).apply()
    }



    private fun getSavedLanguage(): String? {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return prefs.getString("selected_language", null)
    }

    private fun applySavedLanguage() {
        val langCode = getSavedLanguage() ?: return
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }










}
