package com.voxparli.app
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.Normalizer
import java.util.Locale
import kotlin.math.floor
// Paleta de cores da interface nativa.
private val Ink = Color(0xFF20352F)
private val Leaf = Color(0xFF347567)
private val LeafDark = Color(0xFF24594F)
private val SoftGreen = Color(0xFFE7F1EB)
private val Gold = Color(0xFFE4AE4F)
private val Paper = Color(0xFFF7F8F3)
private val Muted = Color(0xFF788781)
private val Line = Color(0xFFE4E9E3)
private val VoxparliColors = lightColorScheme(
    primary = Leaf,
    onPrimary = Color.White,
    secondary = Gold,
    onSecondary = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = SoftGreen,
    onSurfaceVariant = Muted,
    outline = Line
)
// Modelos de idiomas, cursos, exercícios e progresso.
private data class Language(
    val id: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val speechTag: String
)
private data class SpeechVoice(val id: String, val label: String)
private val Languages = listOf(
    Language("en", "Inglês", "English", "🇬🇧", "en-US"),
    Language("es", "Espanhol", "Español", "🇪🇸", "es-ES"),
    Language("it", "Italiano", "Italiano", "🇮🇹", "it-IT"),
    Language("fr", "Francês", "Français", "🇫🇷", "fr-FR"),
    Language("de", "Alemão", "Deutsch", "🇩🇪", "de-DE"),
    Language("zh", "Mandarim", "中文", "🇨🇳", "zh-CN"),
    Language("ja", "Japonês", "日本語", "🇯🇵", "ja-JP"),
    Language("ru", "Russo", "Русский", "🇷🇺", "ru-RU"),
    Language("el", "Grego", "Ελληνικά", "🇬🇷", "el-GR"),
    Language("ko", "Coreano", "한국어", "🇰🇷", "ko-KR"),
    Language("la", "Latim", "Lingua Latina", "🏛️", "la"),
    Language("nb", "Norueguês", "Norsk (bokmål)", "🇳🇴", "nb-NO"),
    Language("nl", "Holandês", "Nederlands", "🇳🇱", "nl-NL"),
    Language("sv", "Sueco", "Svenska", "🇸🇪", "sv-SE"),
    Language("da", "Dinamarquês", "Dansk", "🇩🇰", "da-DK")
)
private val CefrLevels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
private val CustomLevels = mapOf(
    "zh" to listOf("HSK 1", "HSK 2", "HSK 3", "HSK 4", "HSK 5", "HSK 6", "HSK 7", "HSK 8", "HSK 9"),
    "ja" to listOf("N5", "N4", "N3", "N2", "N1"),
    "ko" to listOf("TOPIK 1", "TOPIK 2", "TOPIK 3", "TOPIK 4", "TOPIK 5", "TOPIK 6"),
    "la" to listOf("Etapa I", "Etapa II", "Etapa III", "Etapa IV", "Etapa V", "Etapa VI")
)
private val CustomScaleNames = mapOf("zh" to "HSK", "ja" to "JLPT", "ko" to "TOPIK", "la" to "Etapas de leitura")
private val CourseBands = mapOf(
    "zh" to listOf("A1", "A2", "B1", "B2", "C1", "C2", "C2", "C2", "C2"),
    "ja" to listOf("A1", "A2", "B1", "B2", "C1"),
    "ko" to listOf("A1", "A2", "B1", "B2", "C1", "C2")
)
private data class Course(val languageId: String, val levelIndex: Int, val levelStartDone: Int)
private data class Exercise(
    val id: String,
    val prompt: String,
    val correct: String,
    val kind: String,
    val options: List<String>,
    val translationReverse: Boolean = false
)
private data class ExerciseBank(
    val questions: Map<String, List<Exercise>>,
    val wordTranslations: Map<String, Map<String, String>>
)
private data class LessonSession(
    val languageId: String,
    val questions: List<Exercise>,
    val reviewMode: Boolean,
    val initialQuestionCount: Int = questions.size,
    val index: Int = 0,
    val selected: String = "",
    val checked: Boolean = false,
    val score: Int = 0,
    val speechMessage: String = ""
)
private data class PlacementSession(
    val languageId: String,
    val questions: List<Exercise>,
    val index: Int = 0,
    val selected: String = "",
    val score: Int = 0
)
private data class AppState(
    val profileName: String = "",
    val streak: Int = 0,
    val courses: List<Course> = emptyList(),
    val doneCounts: Map<String, Int> = emptyMap(),
    val seenQuestionIds: Map<String, List<String>> = emptyMap(),
    val completedQuestionIds: Map<String, List<String>> = emptyMap(),
    val audioEnabled: Boolean = true,
    val speakingEnabled: Boolean = false,
    val audioPauseUntil: Long = 0,
    val speechVoiceId: String = ""
)
private enum class Page { HOME, COURSES, REVIEW, PLACEMENT }
// Inicializa a síntese de voz e a interface principal do Android.
class MainActivity : ComponentActivity() {
    private var textToSpeech: TextToSpeech? = null
    private var availableVoices by mutableStateOf<List<SpeechVoice>>(emptyList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                availableVoices = textToSpeech?.voices.orEmpty()
                    .sortedWith(compareBy({ it.locale.displayLanguage }, { it.name }))
                    .map { voice ->
                        SpeechVoice(voice.name, "${voice.locale.getDisplayLanguage(Locale.getDefault())} · ${voice.name}")
                    }
            }
        }
        setContent {
            MaterialTheme(colorScheme = VoxparliColors) {
                VoxparliApp(voices = availableVoices, onSpeak = ::speak)
            }
        }
    }
    private fun speak(text: String, languageId: String, selectedVoiceId: String) {
        val language = Languages.find { it.id == languageId } ?: return
        textToSpeech?.let { engine ->
            val locale = Locale.forLanguageTag(language.speechTag)
            val preferredVoice = engine.voices?.firstOrNull {
                it.name == selectedVoiceId && it.locale.language == locale.language
            }
            if (preferredVoice != null) engine.voice = preferredVoice else engine.language = locale
            engine.setSpeechRate(0.92f)
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "voxparli-${System.currentTimeMillis()}")
        }
    }
    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        super.onDestroy()
    }
}
// Controla navegação, sessões, revisão, nivelamento e permissões.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoxparliApp(voices: List<SpeechVoice>, onSpeak: (String, String, String) -> Unit) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var appState by remember { mutableStateOf(AppStateStore.load(context)) }
    var bank by remember { mutableStateOf<Map<String, List<Exercise>>>(emptyMap()) }
    var wordTranslations by remember { mutableStateOf<Map<String, Map<String, String>>>(emptyMap()) }
    var bankLoading by remember { mutableStateOf(true) }
    var page by rememberSaveable { mutableStateOf(Page.HOME) }
    var lesson by remember { mutableStateOf<LessonSession?>(null) }
    var placement by remember { mutableStateOf<PlacementSession?>(null) }
    var placementLanguage by rememberSaveable { mutableStateOf("en") }
    var profileOpen by remember { mutableStateOf(false) }
    var levelPickerLanguage by remember { mutableStateOf<String?>(null) }
    var reviewLanguage by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val loadedBank = withContext(Dispatchers.IO) { loadQuestionBank(context) }
        bank = loadedBank.questions
        wordTranslations = loadedBank.wordTranslations
        bankLoading = false
    }
    fun updateState(transform: (AppState) -> AppState) {
        appState = transform(appState)
        AppStateStore.save(context, appState)
    }
    fun notify(message: String) {
        scope.launch { snackbar.showSnackbar(message) }
    }
    fun startLesson(languageId: String, reviewMode: Boolean = false) {
        if (bankLoading || bank.isEmpty()) {
            notify("O banco de exercícios ainda está carregando.")
            return
        }
        val course = appState.courses.find { it.languageId == languageId }
        val currentBand = courseBand(languageId, course?.levelIndex ?: 0)
        val source = if (reviewMode) {
            bank.filterKeys { it.startsWith("$languageId:") }.values.flatten()
                .filter { it.id in appState.completedQuestionIds[languageId].orEmpty() }
        } else {
            bank["$languageId:$currentBand"].orEmpty()
        }
        if (source.isEmpty()) {
            notify(if (reviewMode) "Conclua algumas lições para iniciar uma revisão." else "Não há questões disponíveis para este nível.")
            return
        }
        val seen = appState.seenQuestionIds[languageId].orEmpty().toSet()
        val fresh = if (reviewMode) source.shuffled().take(10) else source.filterNot { it.id in seen }.shuffled().take(10)
        val firstPass = (fresh + source.shuffled().filterNot { candidate -> fresh.any { it.id == candidate.id } }).take(10)
        val selectedQuestions = if (firstPass.size >= 10) firstPass else (firstPass + source.shuffled().take(10 - firstPass.size)).take(10)
        val addSpeakingExercises = !reviewMode && appState.speakingEnabled && System.currentTimeMillis() >= appState.audioPauseUntil
        val questions = selectedQuestions.mapIndexed { index, exercise ->
            if (addSpeakingExercises && index in listOf(3, 7)) {
                exercise.copy(kind = "speak", correct = exercise.prompt, options = emptyList())
            } else exercise
        }
        if (questions.isEmpty()) {
            notify("Não foi possível preparar a lição.")
            return
        }
        lesson = LessonSession(languageId, questions, reviewMode)
    }
    fun beginPlacement(languageId: String) {
        val courseLevels = levelsFor(languageId)
        val questions = (0 until 8).mapNotNull { index ->
            val levelIndex = (index * courseLevels.size / 8).coerceIn(0, courseLevels.lastIndex)
            bank["$languageId:${courseBand(languageId, levelIndex)}"].orEmpty()
                .filter { it.options.size >= 2 }
                .randomOrNull()
        }
        if (questions.size < 8) {
            notify("Não foi possível preparar o teste de nível.")
            return
        }
        placementLanguage = languageId
        placement = PlacementSession(languageId, questions)
    }
    fun finishLesson(session: LessonSession) {
        if (!session.reviewMode) {
            val completedQuestions = session.questions.take(session.initialQuestionCount)
            val completedIds = completedQuestions.map { it.id }.distinct()
            val done = (appState.doneCounts[session.languageId] ?: 0) + completedIds.size
            val courseIndex = appState.courses.indexOfFirst { it.languageId == session.languageId }
            val nextCourses = appState.courses.toMutableList()
            if (courseIndex >= 0) {
                val course = nextCourses[courseIndex]
                val levelCount = levelsFor(session.languageId).size
                val advanced = done - course.levelStartDone >= 1200 && course.levelIndex < levelCount - 1
                nextCourses[courseIndex] = if (advanced) Course(session.languageId, course.levelIndex + 1, done) else course
            }
            updateState { current ->
                current.copy(
                    streak = maxOf(1, current.streak),
                    courses = nextCourses,
                    doneCounts = current.doneCounts + (session.languageId to done),
                    seenQuestionIds = current.seenQuestionIds + (session.languageId to
                        (current.seenQuestionIds[session.languageId].orEmpty() + completedIds).distinct().takeLast(10000)),
                    completedQuestionIds = current.completedQuestionIds + (session.languageId to
                        (current.completedQuestionIds[session.languageId].orEmpty() + completedIds).distinct().takeLast(2000))
                )
            }
        }
        lesson = null
        notify("Lição concluída: ${session.score} de ${session.questions.size} corretas.")
    }
    LaunchedEffect(appState.audioPauseUntil) {
        val pauseUntil = appState.audioPauseUntil
        val remaining = pauseUntil - System.currentTimeMillis()
        if (remaining > 0) {
            delay(remaining)
            updateState { current ->
                if (current.audioPauseUntil == pauseUntil) current.copy(audioPauseUntil = 0) else current
            }
        } else if (pauseUntil != 0L) {
            updateState { current -> if (current.audioPauseUntil == pauseUntil) current.copy(audioPauseUntil = 0) else current }
        }
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(AppStateStore.export(appState).toString(2)) }
            }.onSuccess { notify("Progresso exportado.") }.onFailure { notify("Não foi possível exportar o progresso.") }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Arquivo vazio")
                AppStateStore.decode(json)
            }.onSuccess { imported ->
                updateState { imported }
                notify("Progresso importado.")
            }.onFailure { notify("Arquivo de progresso inválido.") }
        }
    }
    val speechPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val session = lesson
        val activity = context as? Activity
        if (granted && session != null && activity != null) {
            lesson = session.copy(speechMessage = "Escutando…")
            startSpeechRecognition(activity, session.languageId,
                onResult = { heard ->
                    val current = lesson ?: return@startSpeechRecognition
                    val expected = current.questions[current.index].prompt
                    val correct = normalizeAnswer(heard) == normalizeAnswer(expected)
                    lesson = current.copy(
                        selected = heard,
                        checked = true,
                        score = current.score + if (correct) 1 else 0,
                        speechMessage = if (correct) "Fala reconhecida!" else "Não coincidiu com a frase."
                    )
                },
                onFailure = { message -> lesson = lesson?.copy(speechMessage = message) }
            )
        } else if (!granted) {
            lesson = lesson?.copy(speechMessage = "Permita o acesso ao microfone para praticar fala.")
        }
    }
    LaunchedEffect(lesson?.index, appState.audioEnabled, appState.audioPauseUntil, appState.speechVoiceId) {
        val session = lesson
        if (session != null && appState.audioEnabled && System.currentTimeMillis() >= appState.audioPauseUntil) {
            val exercise = session.questions.getOrNull(session.index)
            if (exercise != null && exercise.kind != "write") onSpeak(exercise.prompt, session.languageId, appState.speechVoiceId)
        }
    }
    MaterialTheme(colorScheme = VoxparliColors) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbar) },
            topBar = {
                TopAppBar(
                    title = {
                        if (lesson != null) Text("Lição") else Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Voxparli", fontWeight = FontWeight.ExtraBold, color = LeafDark)
                        }
                    },
                    navigationIcon = {
                        if (lesson != null) IconButton(onClick = { lesson = null }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    actions = {
                        if (lesson == null) {
                            Text("${appState.streak} d", color = LeafDark, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { profileOpen = true }) {
                                Icon(Icons.Filled.Person, contentDescription = "Perfil")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (lesson == null) {
                    NavigationBar(containerColor = Color.White) {
                        NavigationBarItem(selected = page == Page.HOME, onClick = { page = Page.HOME }, icon = { Icon(Icons.Filled.Home, null) }, label = { Text("Início") })
                        NavigationBarItem(selected = page == Page.COURSES, onClick = { page = Page.COURSES }, icon = { Icon(Icons.Filled.Language, null) }, label = { Text("Idiomas") })
                        NavigationBarItem(selected = page == Page.REVIEW, onClick = { page = Page.REVIEW }, icon = { Icon(Icons.Filled.Refresh, null) }, label = { Text("Revisão") })
                        NavigationBarItem(selected = page == Page.PLACEMENT, onClick = { page = Page.PLACEMENT }, icon = { Icon(Icons.Filled.Tune, null) }, label = { Text("Nível") })
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding).background(Paper)) {
                val activeLesson = lesson
                if (activeLesson != null) {
                    LessonPage(
                        session = activeLesson,
                        appState = appState,
                        wordTranslations = wordTranslations,
                        onSelect = { value -> lesson = lesson?.copy(selected = value, speechMessage = "") },
                        onSpeak = { text, language -> onSpeak(text, language, appState.speechVoiceId) },
                        onPauseAudio = {
                            updateState { it.copy(audioPauseUntil = System.currentTimeMillis() + 15 * 60 * 1000L) }
                            notify("Áudio e fala desativados por 15 minutos.")
                        },
                        onCheck = {
                            val current = lesson ?: return@LessonPage
                            val exercise = current.questions[current.index]
                            val correct = normalizeAnswer(current.selected) == normalizeAnswer(exercise.correct)
                            lesson = current.copy(checked = true, score = current.score + if (correct) 1 else 0)
                        },
                        onNext = {
                            val current = lesson ?: return@LessonPage
                            val exercise = current.questions[current.index]
                            val correct = normalizeAnswer(current.selected) == normalizeAnswer(exercise.correct)
                            if (correct && current.index == current.questions.lastIndex) {
                                finishLesson(current)
                            } else {
                                val questions = if (correct) current.questions else current.questions + exercise
                                lesson = current.copy(questions = questions, index = current.index + 1, selected = "", checked = false, speechMessage = "")
                            }
                        },
                        onSpeech = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                speechPermission.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                speechPermission.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    )
                } else {
                    when (page) {
                        Page.HOME -> HomePage(
                            state = appState,
                            bankLoading = bankLoading,
                            onCourses = { page = Page.COURSES },
                            onStart = { startLesson(it) },
                            onLevel = { levelPickerLanguage = it }
                        )
                        Page.COURSES -> CoursesPage(
                            state = appState,
                            onAdd = { language ->
                                if (appState.courses.none { it.languageId == language.id }) {
                                    updateState { it.copy(courses = it.courses + Course(language.id, 0, it.doneCounts[language.id] ?: 0)) }
                                    notify("${language.name} adicionado aos seus idiomas.")
                                }
                            },
                            onStart = { startLesson(it) },
                            onLevel = { levelPickerLanguage = it }
                        )
                        Page.REVIEW -> ReviewPage(
                            state = appState,
                            selectedLanguage = reviewLanguage.ifBlank {
                                appState.courses.firstOrNull { !appState.completedQuestionIds[it.languageId].isNullOrEmpty() }?.languageId
                                    ?: appState.courses.firstOrNull()?.languageId.orEmpty()
                            },
                            onLanguage = { reviewLanguage = it },
                            onStart = { language -> startLesson(language, reviewMode = true) },
                            onCourses = { page = Page.COURSES }
                        )
                        Page.PLACEMENT -> PlacementPage(
                            session = placement,
                            selectedLanguage = placementLanguage,
                            onLanguage = { placementLanguage = it },
                            onStart = { beginPlacement(placementLanguage) },
                            onSelect = { value -> placement = placement?.copy(selected = value) },
                            onNext = {
                                val current = placement ?: return@PlacementPage
                                val correct = current.questions[current.index].correct == current.selected
                                placement = current.copy(index = current.index + 1, selected = "", score = current.score + if (correct) 1 else 0)
                            },
                            onRestart = { placement = null },
                            onApply = { languageId, levelIndex ->
                                val existingIndex = appState.courses.indexOfFirst { it.languageId == languageId }
                                val nextCourses = appState.courses.toMutableList()
                                val startDone = appState.doneCounts[languageId] ?: 0
                                if (existingIndex < 0) nextCourses.add(Course(languageId, levelIndex, startDone))
                                else nextCourses[existingIndex] = Course(languageId, levelIndex, startDone)
                                updateState { it.copy(courses = nextCourses) }
                                placement = null
                                page = Page.COURSES
                                notify("Nível definido.")
                            }
                        )
                    }
                }
            }
        }
    }
    if (profileOpen) {
        ProfileDialog(
            state = appState,
            voices = voices,
            onDismiss = { profileOpen = false },
            onSave = { name, audio, speaking, voiceId ->
                updateState { it.copy(profileName = name.trim(), audioEnabled = audio, speakingEnabled = speaking, speechVoiceId = voiceId) }
                profileOpen = false
            },
            onExport = { exportLauncher.launch("voxparli-progresso.json") },
            onImport = { importLauncher.launch(arrayOf("application/json", "text/json")) }
        )
    }
    val languageToEdit = levelPickerLanguage
    if (languageToEdit != null) {
        val course = appState.courses.find { it.languageId == languageToEdit }
        if (course != null) {
            LevelPickerDialog(
                languageId = languageToEdit,
                selectedIndex = course.levelIndex,
                onDismiss = { levelPickerLanguage = null },
                onSelect = { levelIndex ->
                    val nextCourses = appState.courses.map {
                        if (it.languageId == languageToEdit) Course(it.languageId, levelIndex, appState.doneCounts[languageToEdit] ?: 0) else it
                    }
                    updateState { it.copy(courses = nextCourses) }
                    levelPickerLanguage = null
                }
            )
        }
    }
}
// Página inicial com acesso aos cursos e ao progresso.
@Composable
private fun HomePage(
    state: AppState,
    bankLoading: Boolean,
    onCourses: () -> Unit,
    onStart: (String) -> Unit,
    onLevel: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("SUA JORNADA DE IDIOMAS", color = Leaf, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
                Text("Bom dia${state.profileName.takeIf { it.isNotBlank() }?.let { ", $it"} ?: ""}", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                Text("Pequenos passos, grandes conversas.", color = Muted)
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = LeafDark), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                    Text("SUA CONSTÂNCIA", color = Color(0xFFC9DFD3), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("${state.streak} ${if (state.streak == 1) "dia" else "dias"} de sequência", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                    Text("Cinco minutos de prática já fazem diferença.", color = Color(0xFFE1EEE7), fontSize = 13.sp)
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Continue aprendendo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onCourses) { Text("Meus idiomas") }
            }
        }
        if (state.courses.isEmpty()) {
            item {
                EmptyPanel(
                    title = "Seu próximo idioma começa aqui",
                    message = if (bankLoading) "Preparando os exercícios offline…" else "Escolha um idioma e comece sua primeira lição.",
                    action = "Explorar idiomas",
                    onAction = onCourses
                )
            }
        } else {
            items(state.courses.take(3), key = { it.languageId }) { course ->
                CourseCard(
                    course = course,
                    done = state.doneCounts[course.languageId] ?: 0,
                    onStart = { onStart(course.languageId) },
                    onLevel = { onLevel(course.languageId) }
                )
            }
            if (state.courses.size > 3) {
                item { OutlinedButton(onClick = onCourses, modifier = Modifier.fillMaxWidth()) { Text("Ver todos os idiomas") } }
            }
        }
        item {
            Text("Seu caminho de hoje", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
        }
        item { UnitRow("01", "Olá! Prazer em conhecer", "Cumprimentos e apresentações", if (state.courses.isEmpty()) "Escolha um idioma" else "Disponível") }
        item { UnitRow("02", "Palavras do cotidiano", "Pessoas, lugares e objetos", "A seguir") }
        item { UnitRow("03", "Uma conversa simples", "Perguntas e respostas", "A seguir") }
    }
}
@Composable
private fun CoursesPage(
    state: AppState,
    onAdd: (Language) -> Unit,
    onStart: (String) -> Unit,
    onLevel: (String) -> Unit
) {
    val enrolled = state.courses.map { it.languageId }.toSet()
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { PageHeader("SUA JORNADA", "Meus idiomas", "Aprenda mais de um idioma no seu ritmo.") }
        if (state.courses.isNotEmpty()) {
            items(state.courses, key = { it.languageId }) { course ->
                CourseCard(course, state.doneCounts[course.languageId] ?: 0, { onStart(course.languageId) }, { onLevel(course.languageId) })
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(top = 5.dp)) {
                Text("Escolha um idioma", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("15 cursos", color = Muted, fontSize = 12.sp)
            }
        }
        items(Languages, key = { it.id }) { language ->
            LanguageRow(language, enrolled = language.id in enrolled, onClick = { onAdd(language) })
        }
    }
}
// Página de revisão com seleção do idioma do curso.
@Composable
private fun ReviewPage(
    state: AppState,
    selectedLanguage: String,
    onLanguage: (String) -> Unit,
    onStart: (String) -> Unit,
    onCourses: () -> Unit
) {
    val availableLanguages = state.courses.mapNotNull { course ->
        Languages.find { it.id == course.languageId }
    }
    val selectedCourse = state.courses.find { it.languageId == selectedLanguage }
    val hasHistory = !state.completedQuestionIds[selectedLanguage].isNullOrEmpty()
    LazyColumn(
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { PageHeader("FIXE O QUE APRENDEU", "Revisão", "Recupere palavras e frases das suas lições.") }
        if (availableLanguages.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Idioma para revisar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    LanguageChooser(selectedLanguage, onLanguage, availableLanguages)
                }
            }
        }
        item {
            if (selectedCourse != null && hasHistory) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Uma revisão rápida", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Pratique questões já concluídas e fortaleça sua memória.", color = Muted)
                        Button(onClick = { onStart(selectedCourse.languageId) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Começar revisão")
                        }
                    }
                }
            } else if (availableLanguages.isNotEmpty()) {
                EmptyPanel("Ainda não há exercícios para revisar", "Conclua uma lição neste idioma para iniciar uma revisão.", "Escolher um idioma", onCourses)
            } else {
                EmptyPanel("Suas revisões aparecem aqui", "Conclua algumas lições para montar seu treino personalizado.", "Escolher um idioma", onCourses)
            }
        }
    }
}
// Páginas do teste de nível e prática dos exercícios.
@Composable
private fun PlacementPage(
    session: PlacementSession?,
    selectedLanguage: String,
    onLanguage: (String) -> Unit,
    onStart: () -> Unit,
    onSelect: (String) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onApply: (String, Int) -> Unit
) {
    val language = Languages.find { it.id == (session?.languageId ?: selectedLanguage) } ?: Languages.first()
    val levels = levelsFor(language.id)
    val completed = session != null && session.index >= session.questions.size
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { PageHeader("NIVELAMENTO", "Descubra seu nível", "Um ponto de partida para adaptar as próximas lições.") }
        if (session == null) {
            item { LanguageChooser(selectedLanguage, onLanguage) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Teste de nível", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Responda oito questões no idioma escolhido. O resultado sugere um nível inicial.", color = Muted)
                        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Começar teste") }
                    }
                }
            }
        } else if (completed) {
            val suggested = floor(session.score.toDouble() * levels.size / session.questions.size).toInt().coerceIn(0, levels.lastIndex)
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(language.flag, fontSize = 36.sp)
                        Text("${levels[suggested]} · ${session.score}/${session.questions.size} corretas", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Text("Você pode alterar o nível depois. O resultado é uma sugestão para começar.", color = Muted)
                        Button(onClick = { onApply(language.id, suggested) }, modifier = Modifier.fillMaxWidth()) { Text("Começar em ${levels[suggested]}") }
                        OutlinedButton(onClick = onRestart, modifier = Modifier.fillMaxWidth()) { Text("Refazer teste") }
                    }
                }
            }
        } else {
            val question = session.questions[session.index]
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${language.flag} ${language.name}", color = LeafDark, fontWeight = FontWeight.Bold)
                            Text("${session.index + 1} / ${session.questions.size}", color = Muted)
                        }
                        LinearProgressIndicator(progress = (session.index.toFloat() / session.questions.size), modifier = Modifier.fillMaxWidth(), color = Leaf)
                        Text(question.prompt, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        question.options.forEach { option ->
                            AnswerOption(option, selected = session.selected == option, correct = false, enabled = true) { onSelect(option) }
                        }
                        Button(onClick = onNext, enabled = session.selected.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                            Text(if (session.index == session.questions.lastIndex) "Ver resultado" else "Continuar")
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun clickablePrompt(value: String): AnnotatedString {
    val words = Regex("""[\p{IsHan}\p{IsHiragana}\p{IsKatakana}\p{IsHangul}]+|[\p{L}\p{M}]+(?:['’\-][\p{L}\p{M}]+)*""").findAll(value)
    return buildAnnotatedString {
        var cursor = 0
        words.forEach { match ->
            append(value.substring(cursor, match.range.first))
            pushStringAnnotation(tag = "prompt-word", annotation = match.value)
            withStyle(SpanStyle(color = LeafDark, textDecoration = TextDecoration.Underline)) {
                append(match.value)
            }
            pop()
            cursor = match.range.last + 1
        }
        append(value.substring(cursor))
    }
}
@Composable
private fun LessonPage(
    session: LessonSession,
    appState: AppState,
    wordTranslations: Map<String, Map<String, String>>,
    onSelect: (String) -> Unit,
    onSpeak: (String, String) -> Unit,
    onPauseAudio: () -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
    onSpeech: () -> Unit
) {
    val language = Languages.find { it.id == session.languageId } ?: Languages.first()
    val question = session.questions[session.index]
    val lessonLevel = courseBand(session.languageId, appState.courses.find { it.languageId == session.languageId }?.levelIndex ?: 0)
    var selectedWord by remember(session.index) { mutableStateOf<String?>(null) }
    val speechPaused = System.currentTimeMillis() < appState.audioPauseUntil
    val writeMode = question.kind == "write" || (question.kind == "read" && question.options.isEmpty()) ||
        (question.kind == "speak" && speechPaused)
    val speakingMode = question.kind == "speak" && !speechPaused
    val correct = if (speakingMode) {
        normalizeAnswer(session.selected) == normalizeAnswer(question.prompt)
    } else {
        normalizeAnswer(session.selected) == normalizeAnswer(question.correct)
    }
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("${language.flag} ${language.name.uppercase()} · ${levelName(language.id, appState.courses.find { it.languageId == language.id }?.levelIndex ?: 0)}", color = Leaf, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("${session.index + 1} / ${session.questions.size}", color = Muted, fontSize = 12.sp)
            }
            LinearProgressIndicator(progress = (session.index.toFloat() / session.questions.size), modifier = Modifier.fillMaxWidth().padding(top = 9.dp), color = Leaf)
        }
        item {
            Text(if (session.index % 2 == 0) "Vamos praticar!" else "Você consegue!", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                when {
                    speakingMode -> "Diga em voz alta a frase no idioma estudado."
                    writeMode -> "Escreva sua resposta no idioma estudado."
                    else -> "Leia a frase e escolha a tradução correta."
                },
                color = Muted
            )
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SoftGreen), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val prompt = clickablePrompt(question.prompt)
                    ClickableText(
                        text = prompt,
                        style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Ink),
                        onClick = { offset ->
                            val word = prompt.getStringAnnotations("prompt-word", offset, offset).firstOrNull()?.item
                            if (word != null) selectedWord = if (selectedWord == word) null else word
                        }
                    )
                    selectedWord?.let { word ->
                        val translationKey = "${session.languageId}:$lessonLevel${if (question.translationReverse) ":reverse" else ""}"
                        val translation = wordTranslations[translationKey]?.get(normalizeAnswer(word))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Line),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 10.dp)) {
                                Text("Tradução de “$word”", color = Muted, fontSize = 11.sp)
                                Text(translation ?: "Tradução indisponível para esta palavra.", color = LeafDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (appState.audioEnabled && !speechPaused && !writeMode) {
                        TextButton(onClick = { onSpeak(question.prompt, session.languageId) }) {
                            Icon(Icons.Filled.VolumeUp, null); Spacer(Modifier.width(6.dp)); Text("Ouvir")
                        }
                    }
                }
            }
        }
        if (question.kind != "write" && !(question.kind == "read" && question.options.isEmpty())) {
            item {
                OutlinedButton(onClick = onPauseAudio, modifier = Modifier.fillMaxWidth()) {
                    Text("Desativar áudio e fala por 15 minutos")
                }
            }
        }
        if (!session.checked) {
            if (speakingMode) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(14.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = onSpeech, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Filled.Mic, null); Spacer(Modifier.width(8.dp)); Text("Falar agora")
                            }
                            if (session.speechMessage.isNotBlank()) Text(session.speechMessage, color = Muted, fontSize = 13.sp)
                        }
                    }
                }
            } else if (writeMode) {
                item {
                    OutlinedTextField(
                        value = session.selected,
                        onValueChange = onSelect,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Sua resposta") },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }
            } else {
                items(question.options, key = { it }) { option ->
                    AnswerOption(option, selected = session.selected == option, correct = false, enabled = true) { onSelect(option) }
                }
            }
        } else {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = if (correct) SoftGreen else Color(0xFFFFF1E8)), shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(if (correct) "Muito bem!" else "Resposta", fontWeight = FontWeight.Bold, color = if (correct) LeafDark else Color(0xFF9A5739))
                        if (!correct) Text(if (speakingMode) question.prompt else question.correct, color = Ink)
                        if (speakingMode && session.selected.isNotBlank()) Text("Você disse: ${session.selected}", color = Muted, fontSize = 13.sp)
                        if (session.speechMessage.isNotBlank()) Text(session.speechMessage, color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            Button(
                onClick = if (session.checked) onNext else onCheck,
                enabled = session.checked || session.selected.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(when {
                    !session.checked -> "Conferir"
                    session.index == session.questions.lastIndex -> "Concluir lição"
                    else -> "Continuar"
                })
            }
        }
    }
}
// Cartões e componentes compartilhados pelas telas.
@Composable
private fun CourseCard(course: Course, done: Int, onStart: () -> Unit, onLevel: () -> Unit) {
    val language = Languages.find { it.id == course.languageId } ?: Languages.first()
    val progress = ((done - course.levelStartDone).coerceAtLeast(0) / 1200f).coerceIn(0f, 1f)
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(language.flag, fontSize = 27.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(language.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("${language.nativeName} · ${CustomScaleNames[language.id] ?: "CEFR"}", fontSize = 11.sp, color = Muted)
                }
                Surface(color = SoftGreen, shape = RoundedCornerShape(20.dp)) {
                    Text(levelName(language.id, course.levelIndex), color = LeafDark, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
            }
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth(), color = Leaf)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$done exercícios concluídos", fontSize = 11.sp, color = Muted)
                Text("${(progress * 100).toInt()}%", fontSize = 11.sp, color = Muted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onLevel, modifier = Modifier.weight(1f)) { Text("Nível") }
                Button(onClick = onStart, modifier = Modifier.weight(1.3f)) {
                    Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Continuar")
                }
            }
        }
    }
}
@Composable
private fun LanguageRow(language: Language, enrolled: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        enabled = !enrolled,
        colors = CardDefaults.cardColors(containerColor = Color.White, disabledContainerColor = Color.White),
        border = BorderStroke(1.dp, Line),
        shape = RoundedCornerShape(13.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(language.flag, fontSize = 26.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(language.name, fontWeight = FontWeight.Bold)
                Text("${language.nativeName} · ${CustomScaleNames[language.id] ?: "CEFR"}", color = Muted, fontSize = 11.sp)
            }
            if (enrolled) {
                Icon(Icons.Filled.Check, contentDescription = "Adicionado", tint = Leaf)
            } else {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar", tint = Leaf)
            }
        }
    }
}
@Composable
private fun AnswerOption(option: String, selected: Boolean, correct: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val color = when {
        correct -> SoftGreen
        selected -> Color(0xFFE8F0EC)
        else -> Color.White
    }
    Card(
        onClick = onClick,
        enabled = enabled,
        colors = CardDefaults.cardColors(containerColor = color, disabledContainerColor = color),
        border = BorderStroke(if (selected || correct) 1.5.dp else 1.dp, if (selected || correct) Leaf else Line),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(option, modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 13.dp), fontSize = 14.sp, color = Ink)
    }
}
@Composable
private fun UnitRow(number: String, title: String, subtitle: String, status: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(13.dp)) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = SoftGreen, shape = CircleShape, modifier = Modifier.size(37.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(number, color = LeafDark, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, color = Muted, fontSize = 11.sp)
            }
            Text(status, color = Muted, fontSize = 10.sp)
        }
    }
}
@Composable
private fun PageHeader(eyebrow: String, title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(bottom = 4.dp)) {
        Text(eyebrow, color = Leaf, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(title, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, color = Muted, fontSize = 13.sp)
    }
}
@Composable
private fun EmptyPanel(title: String, message: String, action: String, onAction: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(19.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(message, color = Muted, fontSize = 13.sp)
            Button(onClick = onAction) { Text(action) }
        }
    }
}
// Seleção de idioma e preferências do perfil, incluindo a voz do sistema.
@Composable
private fun LanguageChooser(selectedId: String, onSelect: (String) -> Unit) {
    LanguageChooser(selectedId, onSelect, Languages)
}
@Composable
private fun LanguageChooser(selectedId: String, onSelect: (String) -> Unit, languages: List<Language>) {
    var expanded by remember { mutableStateOf(false) }
    val selected = languages.find { it.id == selectedId } ?: languages.firstOrNull() ?: Languages.first()
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("${selected.flag}  ${selected.name}  ·  ${scaleName(selected.id)}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text("${language.flag}  ${language.name}") },
                    onClick = { onSelect(language.id); expanded = false }
                )
            }
        }
    }
}
@Composable
private fun ProfileDialog(
    state: AppState,
    voices: List<SpeechVoice>,
    onDismiss: () -> Unit,
    onSave: (String, Boolean, Boolean, String) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    var name by remember(state.profileName) { mutableStateOf(state.profileName) }
    var audio by remember(state.audioEnabled) { mutableStateOf(state.audioEnabled) }
    var speaking by remember(state.speakingEnabled) { mutableStateOf(state.speakingEnabled) }
    var voiceId by remember(state.speechVoiceId) { mutableStateOf(state.speechVoiceId) }
    var voiceMenuExpanded by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (state.profileName.isBlank()) "Criar perfil" else "Seu perfil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Seu progresso fica salvo neste dispositivo.", color = Muted, fontSize = 13.sp)
                OutlinedTextField(value = name, onValueChange = { name = it.take(40) }, label = { Text("Como quer ser chamado(a)?") }, singleLine = true)
                SettingSwitch("Áudio automático", audio) { audio = it }
                SettingSwitch("Exercícios de fala", speaking) { speaking = it }
                Text("Voz do sistema", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Box {
                    OutlinedButton(onClick = { voiceMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            voices.firstOrNull { it.id == voiceId }?.label ?: "Automática por idioma",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    DropdownMenu(expanded = voiceMenuExpanded, onDismissRequest = { voiceMenuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Automática por idioma") },
                            onClick = { voiceId = ""; voiceMenuExpanded = false }
                        )
                        voices.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text(voice.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                onClick = { voiceId = voice.id; voiceMenuExpanded = false }
                            )
                        }
                    }
                }
                Text("A voz escolhida será usada quando estiver disponível no idioma do exercício.", color = Muted, fontSize = 11.sp)
                Divider(color = Line)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onExport, modifier = Modifier.weight(1f)) { Text("Exportar") }
                    TextButton(onClick = onImport, modifier = Modifier.weight(1f)) { Text("Importar") }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(name, audio, speaking, voiceId) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
@Composable
private fun SettingSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
// Configuração do nível do curso.
@Composable
private fun LevelPickerDialog(languageId: String, selectedIndex: Int, onDismiss: () -> Unit, onSelect: (Int) -> Unit) {
    val language = Languages.find { it.id == languageId } ?: Languages.first()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nível de ${language.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                levelsFor(languageId).forEachIndexed { index, level ->
                    TextButton(onClick = { onSelect(index) }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (index == selectedIndex) "✓  $level" else level, modifier = Modifier.fillMaxWidth(), color = if (index == selectedIndex) LeafDark else Ink)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fechar") } }
    )
}
private fun levelsFor(languageId: String): List<String> = CustomLevels[languageId] ?: CefrLevels
private fun levelName(languageId: String, index: Int): String = levelsFor(languageId)[index.coerceIn(0, levelsFor(languageId).lastIndex)]
private fun scaleName(languageId: String): String = CustomScaleNames[languageId] ?: "CEFR"
private fun courseBand(languageId: String, index: Int): String {
    val levels = levelsFor(languageId)
    val level = levels[index.coerceIn(0, levels.lastIndex)]
    val bandIndex = levels.indexOf(level)
    return CourseBands[languageId]?.getOrNull(bandIndex)
        ?: if (languageId == "la") CefrLevels.getOrElse(bandIndex) { level } else level
}
// Carrega questões offline e oferece suporte à fala e ao progresso salvo.
private fun loadQuestionBank(context: Context): ExerciseBank {
    val json = context.assets.open("question-bank.json").bufferedReader().use { it.readText() }
    val root = JSONObject(json)
    val result = mutableMapOf<String, List<Exercise>>()
    val keys = root.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val array = root.optJSONArray(key) ?: JSONArray()
        result[key] = (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val options = item.optJSONArray("options") ?: JSONArray()
            Exercise(
                id = item.optString("id"),
                prompt = item.optString("prompt"),
                correct = item.optString("correct"),
                kind = item.optString("kind", "translate"),
                options = (0 until options.length()).map { options.optString(it) },
                translationReverse = item.optString("kind") == "write"
            )
        }
    }
    val translationJson = context.assets.open("word-translations.json").bufferedReader().use { it.readText() }
    val translationRoot = JSONObject(translationJson)
    val translations = mutableMapOf<String, Map<String, String>>()
    val translationKeys = translationRoot.keys()
    while (translationKeys.hasNext()) {
        val key = translationKeys.next()
        val words = translationRoot.optJSONObject(key) ?: continue
        val entries = mutableMapOf<String, String>()
        val wordKeys = words.keys()
        while (wordKeys.hasNext()) {
            val word = wordKeys.next()
            entries[word] = words.optString(word)
        }
        translations[key] = entries
    }
    return ExerciseBank(result, translations)
}
private fun normalizeAnswer(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
    .replace(Regex("\\p{Mn}+"), "")
    .lowercase(Locale.ROOT)
    .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
    .trim()
    .replace(Regex("\\s+"), " ")
private fun startSpeechRecognition(activity: Activity, languageId: String, onResult: (String) -> Unit, onFailure: (String) -> Unit) {
    if (!SpeechRecognizer.isRecognitionAvailable(activity)) {
        onFailure("Reconhecimento de voz indisponível neste dispositivo.")
        return
    }
    val language = Languages.find { it.id == languageId } ?: Languages.first()
    val recognizer = SpeechRecognizer.createSpeechRecognizer(activity)
    recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit
        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit
        override fun onError(error: Int) {
            recognizer.destroy()
            onFailure(if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) "Permita o uso do microfone." else "Não consegui ouvir. Tente novamente.")
        }
        override fun onResults(results: Bundle?) {
            recognizer.destroy()
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
            if (text.isNullOrBlank()) onFailure("Não consegui ouvir. Tente novamente.") else onResult(text)
        }
    })
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.speechTag)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
    }
    runCatching { recognizer.startListening(intent) }.onFailure {
        recognizer.destroy()
        onFailure("Não foi possível iniciar o reconhecimento de voz.")
    }
}
// Lê, grava, importa e exporta o progresso local do usuário.
private object AppStateStore {
    private const val Preferences = "voxparli-native"
    private const val StateKey = "state"
    fun load(context: Context): AppState {
        val raw = context.getSharedPreferences(Preferences, Context.MODE_PRIVATE).getString(StateKey, null) ?: return AppState()
        return runCatching { decode(raw) }.getOrDefault(AppState())
    }
    fun save(context: Context, state: AppState) {
        context.getSharedPreferences(Preferences, Context.MODE_PRIVATE).edit().putString(StateKey, export(state).toString()).apply()
    }
    fun export(state: AppState): JSONObject {
        val courses = JSONArray()
        state.courses.forEach { course ->
            courses.put(JSONObject().apply {
                put("lang", course.languageId)
                put("level", levelName(course.languageId, course.levelIndex))
                put("levelStartDone", course.levelStartDone)
                put("unit", "Olá! Prazer em conhecer")
            })
        }
        val done = JSONObject()
        state.doneCounts.forEach { (languageId, count) ->
            repeat(count.coerceAtMost(10000)) { done.put("$languageId:$it", 1) }
        }
        val seen = JSONObject()
        state.seenQuestionIds.forEach { (languageId, ids) -> seen.put(languageId, JSONArray(ids)) }
        val completed = JSONObject()
        state.completedQuestionIds.forEach { (languageId, ids) -> completed.put(languageId, JSONArray(ids)) }
        return JSONObject().apply {
            put("app", "Voxparli")
            put("version", 1)
            put("data", JSONObject().apply {
                put("profile", JSONObject().put("name", state.profileName))
                put("courses", courses)
                put("done", done)
                put("streak", state.streak)
                put("audioEnabled", state.audioEnabled)
                put("speakingEnabled", state.speakingEnabled)
                put("audioPauseUntil", state.audioPauseUntil)
                put("speechVoiceId", state.speechVoiceId)
                put("localSeen", seen)
                put("nativeCompletedQuestionIds", completed)
            })
        }
    }
    fun decode(raw: String): AppState {
        val root = JSONObject(raw)
        val data = root.optJSONObject("data") ?: root
        val profile = data.optJSONObject("profile")
        val coursesJson = data.optJSONArray("courses") ?: JSONArray()
        val courses = (0 until coursesJson.length()).mapNotNull { index ->
            val item = coursesJson.optJSONObject(index) ?: return@mapNotNull null
            val languageId = item.optString("lang")
            if (Languages.none { it.id == languageId }) return@mapNotNull null
            val levels = levelsFor(languageId)
            val savedLevel = item.optString("level")
            val oldBand = CefrLevels.indexOf(savedLevel).coerceAtLeast(0)
            val mappedIndex = levels.indexOf(savedLevel).takeIf { it >= 0 }
                ?: if (levels.size == 6) oldBand else floor(oldBand * (levels.lastIndex / 5.0)).toInt()
            Course(languageId, mappedIndex.coerceIn(0, levels.lastIndex), item.optInt("levelStartDone", 0))
        }.distinctBy { it.languageId }
        val doneJson = data.optJSONObject("done") ?: JSONObject()
        val counts = Languages.associate { language ->
            val count = doneJson.keys().asSequence().count { it.startsWith("${language.id}:") }
            language.id to count
        }.filterValues { it > 0 }
        val localSeen = data.optJSONObject("localSeen") ?: JSONObject()
        val nativeCompleted = data.optJSONObject("nativeCompletedQuestionIds") ?: JSONObject()
        val seen = Languages.mapNotNull { language ->
            val value = localSeen.optJSONArray(language.id) ?: nativeCompleted.optJSONArray(language.id)
            if (value == null) null else language.id to (0 until value.length()).map { value.optString(it) }
        }.toMap()
        val completed = Languages.mapNotNull { language ->
            val value = nativeCompleted.optJSONArray(language.id)
            if (value == null) null else language.id to (0 until value.length()).map { value.optString(it) }
        }.toMap()
        return AppState(
            profileName = profile?.optString("name").orEmpty(),
            streak = data.optInt("streak", 0),
            courses = courses,
            doneCounts = counts,
            seenQuestionIds = seen,
            completedQuestionIds = completed,
            audioEnabled = data.optBoolean("audioEnabled", true),
            speakingEnabled = data.optBoolean("speakingEnabled", false),
            audioPauseUntil = data.optLong("audioPauseUntil", 0),
            speechVoiceId = data.optString("speechVoiceId", "")
        )
    }
}
