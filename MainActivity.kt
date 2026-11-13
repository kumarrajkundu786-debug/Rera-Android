package com.rera.assistant

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import java.util.Locale

private data class LanguageChoice(val label: String, val tag: String, val code: String)

private val languages = listOf(
    LanguageChoice("বাংলা", "bn-BD", TranslateLanguage.BENGALI),
    LanguageChoice("English", "en-US", TranslateLanguage.ENGLISH),
    LanguageChoice("हिन्दी", "hi-IN", TranslateLanguage.HINDI),
    LanguageChoice("اردو", "ur-PK", TranslateLanguage.URDU),
    LanguageChoice("Español", "es-ES", TranslateLanguage.SPANISH),
    LanguageChoice("Français", "fr-FR", TranslateLanguage.FRENCH),
    LanguageChoice("Deutsch", "de-DE", TranslateLanguage.GERMAN),
    LanguageChoice("العربية", "ar-SA", TranslateLanguage.ARABIC),
    LanguageChoice("中文", "zh-CN", TranslateLanguage.CHINESE),
    LanguageChoice("日本語", "ja-JP", TranslateLanguage.JAPANESE),
    LanguageChoice("한국어", "ko-KR", TranslateLanguage.KOREAN),
    LanguageChoice("Português", "pt-PT", TranslateLanguage.PORTUGUESE),
    LanguageChoice("Русский", "ru-RU", TranslateLanguage.RUSSIAN),
    LanguageChoice("Italiano", "it-IT", TranslateLanguage.ITALIAN),
    LanguageChoice("Türkçe", "tr-TR", TranslateLanguage.TURKISH),
    LanguageChoice("Nederlands", "nl-NL", TranslateLanguage.DUTCH),
    LanguageChoice("ไทย", "th-TH", TranslateLanguage.THAI),
    LanguageChoice("Tiếng Việt", "vi-VN", TranslateLanguage.VIETNAMESE),
    LanguageChoice("Bahasa Indonesia", "id-ID", TranslateLanguage.INDONESIAN),
    LanguageChoice("Polski", "pl-PL", TranslateLanguage.POLISH),
    LanguageChoice("Українська", "uk-UA", TranslateLanguage.UKRAINIAN),
    LanguageChoice("ગુજરાતી", "gu-IN", TranslateLanguage.GUJARATI),
    LanguageChoice("मराठी", "mr-IN", TranslateLanguage.MARATHI),
    LanguageChoice("தமிழ்", "ta-IN", TranslateLanguage.TAMIL),
    LanguageChoice("తెలుగు", "te-IN", TranslateLanguage.TELUGU),
    LanguageChoice("ಕನ್ನಡ", "kn-IN", TranslateLanguage.KANNADA),
    LanguageChoice("മലയാളം", "ml-IN", TranslateLanguage.MALAYALAM),
    LanguageChoice("Afrikaans", "af-ZA", TranslateLanguage.AFRIKAANS),
    LanguageChoice("Filipino", "fil-PH", TranslateLanguage.TAGALOG),
    LanguageChoice("فارسی", "fa-IR", TranslateLanguage.PERSIAN),
    LanguageChoice("Svenska", "sv-SE", TranslateLanguage.SWEDISH),
    LanguageChoice("Kiswahili", "sw-KE", TranslateLanguage.SWAHILI),
    LanguageChoice("Welsh", "cy-GB", TranslateLanguage.WELSH),
    LanguageChoice("Albanian", "sq-AL", TranslateLanguage.ALBANIAN),
    LanguageChoice("Romanian", "ro-RO", TranslateLanguage.ROMANIAN),
    LanguageChoice("Greek", "el-GR", TranslateLanguage.GREEK),
    LanguageChoice("Hebrew", "he-IL", TranslateLanguage.HEBREW)
)

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        setContent {
            ReraScreen(
                onSpeak = { text, tag -> speak(text, tag) },
                onRunApproved = { command -> runApprovedCommand(command) }
            )
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setSpeechRate(0.94f)
            tts?.setPitch(1.08f)
            tts?.language = Locale("bn", "IN")
        }
    }

    private fun speak(text: String, languageTag: String = "bn-IN") {
        val locale = Locale.forLanguageTag(languageTag)
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale("en", "US"))
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "rera-voice")
    }

    private fun runApprovedCommand(command: ProposedAction) {
        try {
            when (command.kind) {
                "open" -> {
                    val launch = packageManager.getLaunchIntentForPackage(command.value)
                    if (launch != null) startActivity(launch) else speak("এই অ্যাপটি ফোনে ইনস্টল করা নেই।")
                }
                "dial" -> startActivity(Intent(Intent.ACTION_DIAL).apply {
                    data = android.net.Uri.parse("tel:${command.value}")
                })
                "settings" -> startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            }
        } catch (_: ActivityNotFoundException) {
            speak("এই কাজটি এই ফোনে করা যাচ্ছে না।")
        } catch (_: Exception) {
            speak("কাজটি সম্পন্ন করা যায়নি।")
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

data class ProposedAction(val title: String, val detail: String, val kind: String, val value: String)

private fun understand(text: String): ProposedAction? {
    val s = text.lowercase(Locale.ROOT)
    val apps = listOf(
        Triple("whatsapp", "WhatsApp", "com.whatsapp"), Triple("হোয়াটসঅ্যাপ", "WhatsApp", "com.whatsapp"),
        Triple("youtube", "YouTube", "com.google.android.youtube"), Triple("ইউটিউব", "YouTube", "com.google.android.youtube"),
        Triple("chrome", "Chrome", "com.android.chrome"), Triple("ক্রোম", "Chrome", "com.android.chrome"),
        Triple("gmail", "Gmail", "com.google.android.gm"), Triple("জিমেইল", "Gmail", "com.google.android.gm")
    )
    val openWords = listOf("খোলো", "খুলে দাও", "open", "চালু করো", "চালাও", "abrir", "ouvre")
    if (openWords.any { s.contains(it) }) {
        apps.firstOrNull { s.contains(it.first) }?.let {
            return ProposedAction("অ্যাপ খুলবে?", "${it.second} খুলতে চাও?", "open", it.third)
        }
        if (s.contains("সেটিং") || s.contains("settings"))
            return ProposedAction("Settings খুলবে?", "ফোনের Settings খুলতে চাও?", "settings", "")
    }
    val phoneMatch = Regex("\\+?[0-9][0-9 -]{7,}[0-9]").find(text)
    if ((s.contains("কল") || s.contains("call") || s.contains("ফোন কর")) && phoneMatch != null) {
        val number = phoneMatch.value.replace(" ", "").replace("-", "")
        return ProposedAction("Dialer খুলবে?", "$number নম্বরে কল করার জন্য Dialer খুলব? কলটি তুমি নিজে করবে।", "dial", number)
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageMenu(label: String, selected: LanguageChoice, onSelected: (LanguageChoice) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text(label, color = Color(0xFFB8C7D6), fontSize = 13.sp)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selected.label, onValueChange = {}, readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF31D8F2), unfocusedBorderColor = Color(0xFF476176)
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                languages.forEach { language ->
                    DropdownMenuItem(text = { Text(language.label) }, onClick = {
                        onSelected(language); expanded = false
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReraScreen(
    onSpeak: (String, String) -> Unit,
    onRunApproved: (ProposedAction) -> Unit
) {
    var transcript by remember { mutableStateOf("") }
    var translated by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("আমি প্রস্তুত। কথা বলো অথবা অনুবাদ শুরু করো।") }
    var pending by remember { mutableStateOf<ProposedAction?>(null) }
    var source by remember { mutableStateOf(LanguageChoice("ফোনের Speech ভাষা", "", "")) }
    var target by remember { mutableStateOf(languages.first()) }
    var speakingTag by remember { mutableStateOf("bn-IN") }

    fun translateText(input: String) {
        status = "ভাষা শনাক্ত ও অনুবাদ করা হচ্ছে… প্রথমবার মডেল ডাউনলোডে ইন্টারনেট লাগতে পারে।"
        val identifier = LanguageIdentification.getClient()
        identifier.identifyLanguage(input)
            .addOnSuccessListener { detected ->
                val from = if (source.code.isNotBlank()) source.code else detected
                val sourceLanguage = languages.firstOrNull { it.code == from }
                if (from == "und" || sourceLanguage == null) {
                    status = "এই ভাষাটি শনাক্ত করা যায়নি বা অনুবাদ মডেল সমর্থিত নয়।"
                    return@addOnSuccessListener
                }
                speakingTag = target.tag
                if (from == target.code) {
                    translated = input
                    status = "কথাটি ${target.label} ভাষায়ই বলা হয়েছে।"
                    onSpeak(input, target.tag)
                    return@addOnSuccessListener
                }
                val options = TranslatorOptions.Builder().setSourceLanguage(from).setTargetLanguage(target.code).build()
                val translator: Translator = Translation.getClient(options)
                translator.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        translator.translate(input)
                            .addOnSuccessListener { result ->
                                translated = result
                                status = "অনুবাদ সম্পন্ন।"
                                onSpeak(result, target.tag)
                                translator.close()
                            }
                            .addOnFailureListener {
                                status = "অনুবাদ করা যায়নি। আবার চেষ্টা করো।"
                                translator.close()
                            }
                    }
                    .addOnFailureListener {
                        status = "অনুবাদ মডেল ডাউনলোড হয়নি। প্রথমবার ইন্টারনেট সংযোগ পরীক্ষা করো।"
                        translator.close()
                    }
            }
            .addOnFailureListener { status = "ভাষা শনাক্ত করা যায়নি।" }
    }

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            transcript = spoken
            translated = ""
            val action = understand(spoken)
            if (action != null) {
                pending = action
                status = "এই ফোনের কাজটি করার আগে তোমার অনুমতি চাইছি।"
            } else if (spoken.isNotBlank()) translateText(spoken)
            else status = "কথা বোঝা যায়নি। আবার চেষ্টা করো।"
        } else status = "কথা শোনা যায়নি। আবার চেষ্টা করো।"
    }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFF31D8F2), background = Color(0xFF071321), surface = Color(0xFF102438),
        onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(containerColor = Color(0xFF071321)) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                Text("RERA", fontSize = 38.sp, color = Color(0xFF31D8F2))
                Text("তোমার বহুভাষিক ব্যক্তিগত AI সহকারী", color = Color.LightGray)
                Surface(color = Color(0xFF102438), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("RERA বলছে", color = Color(0xFF31D8F2))
                        Text(status, fontSize = 16.sp)
                    }
                }
                Button(onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        if (source.tag.isNotBlank()) putExtra(RecognizerIntent.EXTRA_LANGUAGE, source.tag)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "RERA-কে বলো। কথার ভাষা নির্দিষ্ট করে দিলে Speech Recognition সাধারণত বেশি নির্ভুল হয়।")
                    }
                    try { speechLauncher.launch(intent) }
                    catch (_: Exception) { status = "এই ফোনে Speech Recognition পরিষেবা পাওয়া যাচ্ছে না।" }
                }, modifier = Modifier.size(142.dp), shape = RoundedCornerShape(72.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Mic, contentDescription = "মাইক্রোফোন", modifier = Modifier.size(38.dp))
                        Text("কথা বলো")
                    }
                }
                LanguageMenu("কথার ভাষা (ফোনের Speech ভাষা অথবা তালিকা থেকে বেছে নাও)", source, onSelected = { source = it })
                LanguageMenu("যে ভাষায় উত্তর / অনুবাদ চাই", target, onSelected = { target = it })
                if (transcript.isNotBlank()) {
                    Surface(color = Color(0xFF102438), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("শোনা কথা", color = Color(0xFF31D8F2)); Text(transcript)
                        }
                    }
                }
                if (translated.isNotBlank()) {
                    Surface(color = Color(0xFF12352F), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("অনুবাদ", color = Color(0xFF72E0B1)); Text(translated)
                        }
                    }
                }
                Text("প্রথমবার অনুবাদ মডেল নামাতে ইন্টারনেট লাগবে। মডেল নামার পর সমর্থিত ভাষার অনুবাদ অফলাইনেও চলতে পারে।", color = Color(0xFFB8C7D6), fontSize = 12.sp)
                Text("ফোনের কাজ কেবল অনুমতি পাওয়ার পর চালু হবে।", color = Color(0xFFB8C7D6), fontSize = 13.sp)
            }
        }
    }

    pending?.let { action ->
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text(action.title) }, text = { Text(action.detail) },
            confirmButton = { TextButton(onClick = {
                pending = null; onRunApproved(action); status = "অনুমোদিত কাজটি চালু করেছি।"; onSpeak("ঠিক আছে।", "bn-IN")
            }) { Text("অনুমতি দাও") } },
            dismissButton = { TextButton(onClick = {
                pending = null; status = "তোমার অনুমতি না থাকায় কাজটি করিনি।"; onSpeak("ঠিক আছে, কাজটি করব না।", "bn-IN")
            }) { Text("বাতিল") } }
        )
    }
}
