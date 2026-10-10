package br.com.gestos.acessibilidade

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.app.ActivityCompat
import java.util.Locale

class MainActivity : Activity() {
    private var speech: SpeechRecognizer? = null
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 13, 24)
        window.navigationBarColor = Color.rgb(8, 13, 24)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 38, 28, 28)
            setBackgroundColor(Color.rgb(8, 13, 24))
        }
        val title = TextView(this).apply {
            text = "Gestos"
            textSize = 30f
            setTextColor(Color.rgb(85, 230, 193))
        }
        val intro = TextView(this).apply {
            text = "Controle acessível por gestos e voz"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(0, 8, 0, 20)
        }
        status = TextView(this).apply {
            text = "Ative o serviço de acessibilidade para usar os comandos do sistema."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 8, 0, 18)
        }
        root.addView(title)
        root.addView(intro)
        root.addView(status)
        root.addView(makeButton("Ativar acessibilidade") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        })
        root.addView(makeButton("Voltar") {
            val service = GestosAccessibilityService.instance
            if (service == null) toast("Ative o serviço Gestos nas configurações.")
            else service.voltar()
        })
        root.addView(makeButton("Ir para início") {
            val service = GestosAccessibilityService.instance
            if (service == null) toast("Ative o serviço Gestos nas configurações.")
            else service.inicio()
        })
        root.addView(makeButton("Ativar comandos de voz") {
            startVoice()
        })
        val note = TextView(this).apply {
            text = "Comandos: “voltar”, “início” e “notificações”. A câmera e o reconhecimento de mão do seu HTML ainda precisam ser integrados nesta versão inicial."
            textSize = 12f
            setTextColor(Color.rgb(155, 169, 189))
            setPadding(0, 22, 0, 0)
        }
        root.addView(note)
        setContentView(root)

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 42)
        }
    }

    private fun makeButton(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.rgb(6, 21, 16))
            backgroundTintList = ColorStateList.valueOf(Color.rgb(85, 230, 193))
            setOnClickListener { action() }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 7, 0, 7)
            layoutParams = params
        }

    private fun startVoice() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            toast("O reconhecimento de voz não está disponível neste aparelho.")
            return
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 42)
            return
        }
        speech?.destroy()
        speech = SpeechRecognizer.createSpeechRecognizer(this)
        speech?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { status.text = "Estou ouvindo… diga “voltar”, “início” ou “notificações”." }
            override fun onResults(results: Bundle?) {
                val phrase = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()?.lowercase(Locale("pt", "BR")).orEmpty()
                status.text = "Comando reconhecido: $phrase"
                val service = GestosAccessibilityService.instance
                when {
                    phrase.contains("voltar") -> service?.voltar() ?: toast("Ative a acessibilidade primeiro.")
                    phrase.contains("início") || phrase.contains("inicio") || phrase.contains("tela inicial") ->
                        service?.inicio() ?: toast("Ative a acessibilidade primeiro.")
                    phrase.contains("notificação") || phrase.contains("notificações") ->
                        service?.notificacoes() ?: toast("Ative a acessibilidade primeiro.")
                    else -> toast("Comando não reconhecido. Tente “voltar”, “início” ou “notificações”.")
                }
                speech?.destroy()
                speech = null
            }
            override fun onError(error: Int) {
                status.text = "Não consegui entender. Toque em “Ativar comandos de voz” e tente novamente."
                speech?.destroy()
                speech = null
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga: voltar, início ou notificações")
        }
        speech?.startListening(intent)
    }

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    override fun onDestroy() {
        speech?.destroy()
        speech = null
        super.onDestroy()
    }
}
