package InfoFeatures

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.dommyv2.R

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val webView = findViewById<WebView>(R.id.youtubeWebView)
        webView.webViewClient = WebViewClient()
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        val videoHtml = """
            <html>
                <body style="margin:0;">
                    <iframe width="100%" height="100%" 
                        src="https://www.youtube.com/embed/X3TUu5F--UM" 
                        frameborder="0" 
                        allowfullscreen>
                    </iframe>
                </body>
            </html>
        """.trimIndent()

        webView.loadData(videoHtml, "text/html", "utf-8")
    }
}
