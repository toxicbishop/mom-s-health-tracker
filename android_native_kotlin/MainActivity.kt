package com.example.momhealthtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.text.SimpleDateFormat
import java.util.*

// --- Constants & Config ---
const val API_URL = "https://script.google.com/macros/s/AKfycbw7..." // Replace with actual URL from Constants.js

// --- Data Models ---
data class AppTheme(val name: String, val primary: Color, val secondary: Color, val bgColors: List<Color>)
data class User(val pin: String, val q1: String, val a1: String, val q2: String, val a2: String)
data class LogEntry(
    val timestamp: String,
    val date: String,
    val time: String,
    val type: String,
    val weight: String? = null,
    val bp_systolic: String? = null,
    val bp_diastolic: String? = null,
    val med_name: String? = null,
    val mood: String? = null
)
data class ApiRequest(
    val action: String? = null,
    val pin: String? = null,
    val type: String? = null,
    val weight: Float? = null,
    val bp_sys: Int? = null,
    val bp_dia: Int? = null,
    val mood: String? = null,
    val symptoms: String? = null,
    val notes: String? = null,
    val timestamp: String = "",
    val q1: String? = null,
    val a1: String? = null,
    val q2: String? = null,
    val a2: String? = null
)
data class ApiResponse(val status: String? = null, val initialized: Boolean = false)

// --- Themes ---
val THEMES = mapOf(
    "PREMIUM" to AppTheme("Modern Indigo", Color(0xFF4f46e5), Color(0xFF1f2937), listOf(Color(0xFF030712), Color(0xFF111827))),
    "SAKURA" to AppTheme("Slate Rose", Color(0xFFdb2777), Color(0xFF4c0519), listOf(Color(0xFF0f0505), Color(0xFF2a0a12))),
    "EMERALD" to AppTheme("Forest Green", Color(0xFF059669), Color(0xFF064e3b), listOf(Color(0xFF022c22), Color(0xFF064e3b)))
)

// --- Networking ---
interface ApiService {
    @GET("exec")
    suspend fun getLogs(): List<LogEntry>

    @POST("exec")
    suspend fun postAction(@Body body: ApiRequest): ApiResponse
}

object NetworkClient {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL.substringBeforeLast("/") + "/") // Hackous for GAS
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// --- ViewModel ---
class AppViewModel : ViewModel() {
    private val _theme = MutableStateFlow(THEMES["PREMIUM"]!!)
    val theme = _theme.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs = _logs.asStateFlow()

    fun setTheme(key: String) { _theme.value = THEMES[key] ?: THEMES["PREMIUM"]!! }
    fun setAuthenticated(auth: Boolean) { _isAuthenticated.value = auth }

    fun fetchLogs() {
        viewModelScope.launch {
            try {
                // In real app, handle loading state
                val data = NetworkClient.api.getLogs()
                _logs.value = data
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun submitLog(req: ApiRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                NetworkClient.api.postAction(req)
                onSuccess()
                fetchLogs()
            } catch (e: Exception) { 
                // Handle offline queue here in detailed implementation
                onSuccess() 
            }
        }
    }
    
    fun verifyPin(pin: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkClient.api.postAction(ApiRequest(action = "VERIFY_PIN", pin = pin))
                if (res.status == "success") onSuccess() else onError()
            } catch (e: Exception) { onError() }
        }
    }
}

// --- UI Components ---

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.05f),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun ScreenHeader(title: String, navController: NavController, showBack: Boolean = true) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
        if (showBack) {
            Row(
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                Text("Back", color = Color.White, modifier = Modifier.padding(start = 4.dp))
            }
        }
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun MainButton(
    text: String, 
    icon: ImageVector? = null, 
    onClick: () -> Unit, 
    color: Color,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp),
        enabled = !loading
    ) {
        if (loading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            }
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// --- Screens ---

@Composable
fun LoginScreen(viewModel: AppViewModel, navController: NavController) {
    val theme by viewModel.theme.collectAsState()
    var pin by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(theme.primary.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, theme.primary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Lock, contentDescription = null, tint = theme.primary, modifier = Modifier.size(40.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Health Tracker", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Enter security PIN", fontSize = 16.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4) pin = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = theme.primary,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 32.sp, letterSpacing = 10.sp)
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        MainButton(text = "Unlock", onClick = {
            loading = true
            viewModel.verifyPin(pin, 
                onSuccess = { 
                    loading = false
                    viewModel.setAuthenticated(true)
                    navController.navigate("home")
                },
                onError = {
                    loading = false
                    // Show Snackbar or error
                }
            )
        }, color = if(pin.length == 4) theme.primary else Color.Gray.copy(alpha = 0.5f), loading = loading)
    }
}

@Composable
fun DashboardScreen(viewModel: AppViewModel, navController: NavController) {
    val theme by viewModel.theme.collectAsState()
    
    // Simulate animation effects with simple placement
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader("Health Dashboard", navController, showBack = false)
        
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Overview", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                // Progress Card Placeholder
                GlassCard(modifier = Modifier.border(width=0.dp, color = Color.Transparent).background(Color.Transparent)) {
                     Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                         Text("Weight Goal", color = Color.Gray)
                         Text("60kg", color = Color.White, fontWeight = FontWeight.Bold)
                     }
                     Spacer(modifier = Modifier.height(8.dp))
                     LinearProgressIndicator(progress = { 0.7f }, color = theme.primary, trackColor = Color.White.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth())
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Log Weight
                    GlassCard(modifier = Modifier.weight(1f).clickable { navController.navigate("log/WEIGHT") }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.MonitorWeight, null, tint = theme.primary, modifier = Modifier.size(32.dp))
                            Text("Log Weight", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                    // Log BP
                    GlassCard(modifier = Modifier.weight(1f).clickable { navController.navigate("log/BP") }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Favorite, null, tint = Color(0xFFf43f5e), modifier = Modifier.size(32.dp))
                            Text("Log BP", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
            
            item {
                GlassCard(modifier = Modifier.clickable { navController.navigate("history") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.History, null, tint = Color.Yellow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("View History", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
            
            item {
                 GlassCard(modifier = Modifier.clickable { navController.navigate("settings") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Settings, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Settings", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LogScreen(type: String, viewModel: AppViewModel, navController: NavController) {
    val theme by viewModel.theme.collectAsState()
    var weight by remember { mutableStateOf("") }
    var sys by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader("Log $type", navController)
        
        Column(modifier = Modifier.padding(24.dp)) {
            GlassCard {
                if (type == "WEIGHT" || type == "BOTH") {
                    Text("Weight (kg)", color = Color.Gray, fontSize = 14.sp)
                    OutlinedTextField(
                        value = weight, onValueChange = { weight = it },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = theme.primary),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                
                if (type == "BP" || type == "BOTH") {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                           Text("Systolic", color = Color.Gray, fontSize = 14.sp)
                           OutlinedTextField(value = sys, onValueChange = { sys = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                               colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = theme.primary))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                           Text("Diastolic", color = Color.Gray, fontSize = 14.sp)
                           OutlinedTextField(value = dia, onValueChange = { dia = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                               colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = theme.primary))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                MainButton("Save Entry", Icons.Filled.Add, color = theme.primary, onClick = {
                    val req = ApiRequest(
                        type = type,
                        weight = weight.toFloatOrNull(),
                        bp_sys = sys.toIntOrNull(),
                        bp_dia = dia.toIntOrNull(),
                        timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(Date())
                    )
                    viewModel.submitLog(req) { navController.popBackStack() }
                })
            }
        }
    }
}

// --- Main Activity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = AppViewModel() // In real app, use Hilt or ViewModelProvider
        
        setContent {
            val theme by viewModel.theme.collectAsState()
            val navController = rememberNavController()
            
            // Global Background Gradient
            Box(
                 modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = theme.bgColors))
            ) {
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(viewModel, navController) }
                    composable("home") { DashboardScreen(viewModel, navController) }
                    composable("log/{type}") { bs -> LogScreen(bs.arguments?.getString("type") ?: "WEIGHT", viewModel, navController) }
                    // Add other screens (History, Mood, etc.) similarly
                }
            }
        }
    }
}
