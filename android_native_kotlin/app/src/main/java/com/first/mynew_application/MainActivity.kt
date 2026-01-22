package com.first.mynew_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

// --- CONFIGURATION ---
const val API_URL = "https://script.google.com/macros/s/AKfycbw55vbkDoPQmCkP1Uhh52FqqiAxHjbLOHw6WXYl8vjmv0O35PJhQZ1oxTOfsmwERuE09g/exec"

// --- DESIGN SYSTEM (Theming Engine) ---
data class AppColors(
    val Background: Color,
    val Surface: Color,
    val Primary: Color,
    val TextPrimary: Color,
    val TextSecondary: Color,
    val Border: Color,
    val IconTint: Color,
    val InputBg: Color,
    val SuccessContainer: Color,
    val SuccessContent: Color,
    val ErrorContainer: Color,
    val ErrorContent: Color,
    val InfoContainer: Color,
    val InfoContent: Color
)

val LightPalette = AppColors(
    Background = Color(0xFFF8F9FB),
    Surface = Color(0xFFFFFFFF),
    Primary = Color(0xFF0F515F), // Teal/Petrol
    TextPrimary = Color(0xFF1E293B),
    TextSecondary = Color(0xFF64748B),
    Border = Color(0xFFE2E8F0),
    IconTint = Color(0xFF475569),
    InputBg = Color(0xFFF1F5F9),
    SuccessContainer = Color(0xFFDCFCE7),
    SuccessContent = Color(0xFF166534),
    ErrorContainer = Color(0xFFFEF2F2),
    ErrorContent = Color(0xFFEF4444),
    InfoContainer = Color(0xFFE0F2FE),
    InfoContent = Color(0xFF0369A1)
)

val DarkPalette = AppColors(
    Background = Color(0xFF0F172A), // Slate 900
    Surface = Color(0xFF1E293B),    // Slate 800
    Primary = Color(0xFF38BDF8),    // Sky 400
    TextPrimary = Color(0xFFF8FAFC),
    TextSecondary = Color(0xFF94A3B8),
    Border = Color(0xFF334155),
    IconTint = Color(0xFFCBD5E1),
    InputBg = Color(0xFF020617),
    SuccessContainer = Color(0xFF064E3B),
    SuccessContent = Color(0xFF34D399),
    ErrorContainer = Color(0xFF450A0A),
    ErrorContent = Color(0xFFF87171),
    InfoContainer = Color(0xFF0C4A6E),
    InfoContent = Color(0xFF7DD3FC)
)

val LocalAppColors = staticCompositionLocalOf { LightPalette }

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkPalette else LightPalette
    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
            content = content
        )
    }
}

object AppTypography {
    val H1 @Composable get() = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.TextPrimary, letterSpacing = (-0.5).sp)
    val SectionHeader @Composable get() = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.TextSecondary, letterSpacing = 1.sp)
    val CardTitle @Composable get() = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.TextPrimary)
    val Subtitle @Composable get() = TextStyle(fontSize = 14.sp, color = AppTheme.colors.TextSecondary)
    val Value @Composable get() = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppTheme.colors.TextPrimary)
}

// --- COMPONENTS ---

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = AppTheme.colors.Surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.Border),
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = AppTypography.SectionHeader,
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
    )
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, variant: String = "FILLED", modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (variant == "FILLED") AppTheme.colors.Primary else Color.Transparent,
            contentColor = if (variant == "FILLED") AppTheme.colors.Surface else AppTheme.colors.TextPrimary // Correct contrast text
        ),
        border = if (variant == "OUTLINE") androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.Border) else null,
        modifier = modifier.height(44.dp).fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun SegmentedControl(
    items: List<String>,
    defaultSelectedItemIndex: Int = 0,
    onItemSelection: (selectedItemIndex: Int) -> Unit
) {
    val selectedIndex = remember { mutableIntStateOf(defaultSelectedItemIndex) }
    Row(
        modifier = Modifier.fillMaxWidth().background(AppTheme.colors.InputBg, RoundedCornerShape(8.dp)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, item ->
            Box(
                modifier = Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(6.dp))
                    .background(if (selectedIndex.intValue == index) AppTheme.colors.Surface else Color.Transparent)
                    .clickable { selectedIndex.intValue = index; onItemSelection(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(item, fontSize = 13.sp, fontWeight = if (selectedIndex.intValue == index) FontWeight.SemiBold else FontWeight.Normal, color = if (selectedIndex.intValue == index) AppTheme.colors.TextPrimary else AppTheme.colors.TextSecondary)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: ImageVector? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, tint = AppTheme.colors.TextSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(label, style = AppTypography.Value)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = AppTheme.colors.Border, modifier = Modifier.size(14.dp))
    }
}

@Composable
fun ThemeToggleRow(isDark: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(if(isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode, null, tint = AppTheme.colors.TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Dark Mode", style = AppTypography.Value)
        }
        Switch(
            checked = isDark,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppTheme.colors.Primary,
                uncheckedThumbColor = AppTheme.colors.TextSecondary,
                uncheckedTrackColor = AppTheme.colors.InputBg
            )
        )
    }
}

@Composable
fun InsightCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    AppCard {
        Row(crossAxisAlignment = Alignment.Start) {
            Icon(icon, null, tint = AppTheme.colors.Primary, modifier = Modifier.size(24.dp).padding(top = 2.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = AppTypography.CardTitle, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
fun HistoryItem(icon: ImageVector, color: Color, title: String, subtitle: String) {
    AppCard(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = AppTypography.CardTitle)
                Text(subtitle, style = AppTypography.Subtitle, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = AppTheme.colors.Border, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun IconBox(icon: ImageVector, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(40.dp).background(AppTheme.colors.InputBg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = AppTheme.colors.Primary, modifier = Modifier.size(20.dp))
    }
}

// --- SCREENS ---

@Composable
fun HistoryScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = AppTheme.colors.Background
    ) { p ->
        LazyColumn(modifier = Modifier.padding(p).padding(horizontal = 24.dp)) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("October 2023", style = AppTypography.H1)
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = AppTheme.colors.Primary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.Search, null, tint = AppTheme.colors.TextPrimary)
                        Icon(Icons.Default.Tune, null, tint = AppTheme.colors.TextPrimary)
                    }
                }
            }
            item { SectionHeader("TODAY, OCT 24") }
            item { HistoryItem(Icons.Default.MonitorWeight, Color(0xFF0EA5E9), "145 lbs", "08:30 AM") }
            item { HistoryItem(Icons.Default.Favorite, Color(0xFFEF4444), "120/80 mmHg", "09:15 AM") }
            item { SectionHeader("YESTERDAY, OCT 23") }
            item { HistoryItem(Icons.Outlined.MedicalServices, Color(0xFF10B981), "Vitamin D3 (2000 IU)", "10:00 AM") }
            item { HistoryItem(Icons.Default.SentimentVerySatisfied, Color(0xFFF59E0B), "Feeling Great", "08:00 PM") }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun TrendsScreen(navController: NavController) {
    Scaffold(bottomBar = { BottomNavBar(navController) }, containerColor = AppTheme.colors.Background) { p ->
        LazyColumn(modifier = Modifier.padding(p).padding(24.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Column { Text("Health Trends", style = AppTypography.H1); Text("Analytics & Insights", style = AppTypography.Subtitle) }
                    Box(modifier = Modifier.size(40.dp).border(1.dp, AppTheme.colors.Border, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CalendarToday, null, tint = AppTheme.colors.TextPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
            item { SegmentedControl(listOf("Week", "Month", "Year"), 0) {}; Spacer(modifier = Modifier.height(24.dp)) }
            item {
                AppCard {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("WEIGHT TREND", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.TextSecondary))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("62.4 kg", style = AppTypography.H1)
                                Text(" Average", style = AppTypography.Subtitle, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                        Surface(color = AppTheme.colors.SuccessContainer, shape = RoundedCornerShape(4.dp)) {
                            Text("-0.8 kg", color = AppTheme.colors.SuccessContent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { i, day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(8.dp).background(if (i == 3) AppTheme.colors.Primary else AppTheme.colors.Border, androidx.compose.foundation.shape.CircleShape))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(day, style = TextStyle(fontSize = 12.sp, color = AppTheme.colors.TextSecondary))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(AppTheme.colors.InputBg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.FavoriteBorder, null, tint = AppTheme.colors.Primary) }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) { Text("Blood Pressure", style = AppTypography.CardTitle); Text("Average: 118/76 mmHg", style = AppTypography.Subtitle) }
                        Surface(color = AppTheme.colors.SuccessContainer, shape = RoundedCornerShape(4.dp)) { Text("● NORMAL", color = AppTheme.colors.SuccessContent, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item { SectionHeader("HEALTH INSIGHTS") }
            item {
                 InsightCard("BLOOD PRESSURE PATTERN", Icons.Default.Lightbulb) { Text("• Systolic readings are consistently 5% higher in the morning (6AM-9AM).", style = AppTypography.Subtitle); Text("• Stability improved following consistent medication adherence last week.", style = AppTypography.Subtitle, modifier = Modifier.padding(top = 8.dp)) }
                 Spacer(modifier = Modifier.height(12.dp))
                 InsightCard("WEIGHT CORRELATION", Icons.AutoMirrored.Filled.TrendingDown) { Text("• Downward trend correlates with 15% increase in tracked physical activity.", style = AppTypography.Subtitle) }
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, isDark: Boolean, userEmail: String, userName: String, onToggleTheme: (Boolean) -> Unit) {
    Scaffold(bottomBar = { BottomNavBar(navController) }, containerColor = AppTheme.colors.Background) { p ->
        Column(modifier = Modifier.padding(p).padding(24.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(100.dp).background(AppTheme.colors.InputBg, androidx.compose.foundation.shape.CircleShape), contentAlignment = Alignment.Center) {
                Text(userName.take(1).uppercase(), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.TextSecondary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(userName, style = AppTypography.H1)
            Text(userEmail, style = AppTypography.Subtitle)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AppCard {
                InfoRow("Personal Information", "", Icons.Default.Person)
                HorizontalDivider(color = AppTheme.colors.Border, thickness = 0.5.dp)
                // Theme Toggle
                ThemeToggleRow(isDark, onToggleTheme)
                HorizontalDivider(color = AppTheme.colors.Border, thickness = 0.5.dp)
                InfoRow("Health Goals", "", Icons.Default.TrackChanges)
                HorizontalDivider(color = AppTheme.colors.Border, thickness = 0.5.dp)
                InfoRow("Notifications", "", Icons.Default.Notifications)
                HorizontalDivider(color = AppTheme.colors.Border, thickness = 0.5.dp)
                InfoRow("Security", "", Icons.Default.Security)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Sign Out */ },
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.ErrorContainer, contentColor = AppTheme.colors.ErrorContent),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.ErrorContainer)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null, modifier = Modifier.padding(end = 8.dp))
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Text("VERSION 2.4.0", style = TextStyle(fontSize = 10.sp, color = AppTheme.colors.TextSecondary, letterSpacing = 1.sp))
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(bottomBar = { BottomNavBar(navController) }, containerColor = AppTheme.colors.Background) { p ->
        LazyColumn(modifier = Modifier.padding(p).padding(24.dp)) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text("Health Dashboard", style = AppTypography.H1); Text("Monday, May 22", style = AppTypography.Subtitle) }
                    IconButton(onClick = { navController.navigate("profile") }, modifier = Modifier.border(1.dp, AppTheme.colors.Border, RoundedCornerShape(8.dp)).size(40.dp)) { Icon(Icons.Outlined.Settings, null, tint = AppTheme.colors.IconTint, modifier = Modifier.size(20.dp)) }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppCard(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Outlined.MonitorWeight, null, tint = AppTheme.colors.Primary, modifier = Modifier.size(24.dp)); Text("WEIGHT", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.TextSecondary)) }
                        Spacer(modifier = Modifier.height(16.dp)); Text("Last: 62.4 kg", style = AppTypography.Value); Spacer(modifier = Modifier.height(16.dp)); ActionButton("Log Entry", onClick = { navController.navigate("log/WEIGHT") }, variant = "OUTLINE")
                    }
                    AppCard(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Outlined.FavoriteBorder, null, tint = AppTheme.colors.Primary, modifier = Modifier.size(24.dp)); Text("BP", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.TextSecondary)) }
                        Spacer(modifier = Modifier.height(16.dp)); Text("Last: 118/76", style = AppTypography.Value); Spacer(modifier = Modifier.height(16.dp)); ActionButton("Log Entry", onClick = { navController.navigate("log/BP") }, variant = "OUTLINE")
                    }
                }
            }
            item {
                SectionHeader("Well-being")
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconBox(Icons.Outlined.Mood); Spacer(modifier = Modifier.width(16.dp)); Column(modifier = Modifier.weight(1f)) { Text("Mood & Symptoms", style = AppTypography.CardTitle); Text("No data logged today", style = AppTypography.Subtitle) }; Button(onClick = { /* TODO */ }, colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.Primary), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), modifier = Modifier.height(36.dp)) { Text("+ Log", fontSize = 12.sp) } }
                    Spacer(modifier = Modifier.height(16.dp)); Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(AppTheme.colors.InputBg, RoundedCornerShape(2.dp))) { Box(modifier = Modifier.fillMaxWidth(0.3f).height(4.dp).background(AppTheme.colors.Primary, RoundedCornerShape(2.dp))) }
                }
            }
            item {
                SectionHeader("Medication")
                AppCard {
                    Row(verticalAlignment = Alignment.Top) { IconBox(Icons.Outlined.MedicalServices); Spacer(modifier = Modifier.width(16.dp)); Column(modifier = Modifier.weight(1f)) { Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Text("Active Prescription", style = AppTypography.CardTitle); Surface(color = AppTheme.colors.InputBg, shape = RoundedCornerShape(4.dp)) { Text("1 Remaining", fontSize = 10.sp, color = AppTheme.colors.TextSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) } }; Text("Prenatal Vitamin • 8:00 AM", style = AppTypography.Subtitle, modifier = Modifier.padding(top = 4.dp)) } }
                    Spacer(modifier = Modifier.height(16.dp)); ActionButton("Mark as Taken", onClick = { /* TODO */ }, variant = "FILLED"); Spacer(modifier = Modifier.height(16.dp)); Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(AppTheme.colors.InputBg, RoundedCornerShape(2.dp))) { Box(modifier = Modifier.fillMaxWidth(0.5f).height(4.dp).background(AppTheme.colors.Primary, RoundedCornerShape(2.dp))) }
                }
            }
            item {
                SectionHeader("Clinical Reports")
                AppCard { Column { ReportItem("Monthly Health Summary"); HorizontalDivider(color = AppTheme.colors.Border, thickness = 0.5.dp); ReportItem("Export Clinical Data (PDF)") } }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ReportItem(title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { }, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Description, null, tint = AppTheme.colors.TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = AppTypography.CardTitle, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = AppTheme.colors.Border, modifier = Modifier.size(14.dp))
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        "Home" to Icons.Default.Home,
        "Meds" to Icons.Filled.Medication,
        "Vitals" to Icons.Default.BarChart,
        "Track" to Icons.Default.CalendarToday, 
        "Settings" to Icons.Default.Settings
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = AppTheme.colors.Surface, tonalElevation = 10.dp) {
        items.forEach { (label, icon) ->
            val route = when(label) {
                "Home" -> "home"
                "Meds" -> "meds"
                "Vitals" -> "trends"
                "Track" -> "calendar"
                "Settings" -> "profile"
                else -> "home"
            }
            
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp)) },
                label = { Text(label, fontSize = 10.sp) },
                selected = currentRoute == route,
                onClick = { 
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppTheme.colors.Primary,
                    selectedTextColor = AppTheme.colors.Primary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = AppTheme.colors.TextSecondary,
                    unselectedTextColor = AppTheme.colors.TextSecondary
                )
            )
        }
    }
}

@Composable
fun SignInScreen(navController: NavController, onSignIn: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    // Force Dark Theme for Auth Screens as requested "Dark mode version" or inherit
    // For now, we assume the app is in dark mode or these screens suggest a dark aesthetic.
    // We will use the Surface color which is dark in DarkMode.
    
    Scaffold(containerColor = AppTheme.colors.Background) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(100.dp) // Larger for logo
                    , 
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Sign In", style = AppTypography.H1, fontSize = 28.sp)
            Text("Welcome back", style = AppTypography.Subtitle, modifier = Modifier.padding(top = 8.dp))
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Name (Requested Feature)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0E7490),
                    unfocusedBorderColor = AppTheme.colors.Border,
                    focusedContainerColor = AppTheme.colors.InputBg,
                    unfocusedContainerColor = AppTheme.colors.InputBg
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0E7490),
                    unfocusedBorderColor = AppTheme.colors.Border,
                    focusedContainerColor = AppTheme.colors.InputBg, // Darker input bg
                    unfocusedContainerColor = AppTheme.colors.InputBg
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0E7490),
                    unfocusedBorderColor = AppTheme.colors.Border,
                    focusedContainerColor = AppTheme.colors.InputBg,
                    unfocusedContainerColor = AppTheme.colors.InputBg
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("Forgot Password?", color = AppTheme.colors.TextSecondary, fontSize = 12.sp, modifier = Modifier.clickable { })
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    onSignIn(if(email.isNotBlank()) email else "user@example.com", if(name.isNotBlank()) name else "User")
                    navController.navigate("home") 
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490)) // Teal
            ) {
                Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ", color = AppTheme.colors.TextSecondary, fontSize = 14.sp)
                Text("Create Account", color = AppTheme.colors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { navController.navigate("signup") })
            }
        }
    }
}

@Composable
fun SignUpScreen(navController: NavController, onSignUp: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(containerColor = AppTheme.colors.Background) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp), 
                contentAlignment = Alignment.Center
            ) {
               Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Create Account", style = AppTypography.H1, fontSize = 28.sp)
            Text("Start your health journey", style = AppTypography.Subtitle, modifier = Modifier.padding(top = 8.dp))
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0E7490),
                    unfocusedBorderColor = AppTheme.colors.Border,
                    focusedContainerColor = AppTheme.colors.InputBg,
                    unfocusedContainerColor = AppTheme.colors.InputBg
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0E7490),
                    unfocusedBorderColor = AppTheme.colors.Border,
                    focusedContainerColor = AppTheme.colors.InputBg,
                    unfocusedContainerColor = AppTheme.colors.InputBg
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
             OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0E7490),
                    unfocusedBorderColor = AppTheme.colors.Border,
                    focusedContainerColor = AppTheme.colors.InputBg,
                    unfocusedContainerColor = AppTheme.colors.InputBg
                )
            )
             Spacer(modifier = Modifier.height(16.dp))
             OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0E7490),
                    unfocusedBorderColor = AppTheme.colors.Border,
                    focusedContainerColor = AppTheme.colors.InputBg,
                    unfocusedContainerColor = AppTheme.colors.InputBg
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    onSignUp(if(email.isNotBlank()) email else "new_user@example.com", if(name.isNotBlank()) name else "New User")
                    navController.navigate("account_created") 
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490))
            ) {
                Text("Sign Up", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
             Spacer(modifier = Modifier.weight(1f))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", color = AppTheme.colors.TextSecondary, fontSize = 14.sp)
                Text("Sign In", color = AppTheme.colors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun AccountCreatedScreen(navController: NavController) {
    Scaffold(containerColor = AppTheme.colors.Background) { p ->
        Column(
            modifier = Modifier.padding(p).padding(32.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF0E7490).copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFF0E7490), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text("Account Created", style = AppTypography.H1, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Your account has been successfully set up. You can now start tracking your vitals and managing reminders.",
                style = AppTypography.Subtitle,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490))
            ) {
                Text("Get Started", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var pin by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().background(AppTheme.colors.Background).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Filled.Lock, null, tint = AppTheme.colors.Primary, modifier = Modifier.size(48.dp)); Spacer(modifier = Modifier.height(32.dp))
        Text("Welcome Back", style = AppTypography.H1); Text("Enter PIN to continue", style = AppTypography.Subtitle); Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = pin, onValueChange = { if (it.length <= 4) pin = it },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppTheme.colors.Surface,
                unfocusedContainerColor = AppTheme.colors.Surface,
                focusedBorderColor = AppTheme.colors.Primary,
                unfocusedBorderColor = AppTheme.colors.Border,
                focusedTextColor = AppTheme.colors.TextPrimary, // Fix: Text was invisible in dark mode default
                unfocusedTextColor = AppTheme.colors.TextPrimary,
                cursorColor = AppTheme.colors.Primary
            )
        )
        Spacer(modifier = Modifier.height(24.dp)); ActionButton("Unlock Account", onClick = { if (pin.length == 4) navController.navigate("home") }, variant = "FILLED")
    }
}

@Composable
fun LogScreen(type: String, navController: NavController) {
    var val1 by remember { mutableStateOf("") }
    var val2 by remember { mutableStateOf("") }
    val title = if (type == "WEIGHT") "Log Weight" else "Log BP"
    Scaffold(containerColor = AppTheme.colors.Background) { p ->
        Column(modifier = Modifier.padding(p).padding(24.dp)) {
            Row(modifier = Modifier.padding(bottom = 24.dp).clickable { navController.popBackStack() }, verticalAlignment = Alignment.CenterVertically) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppTheme.colors.TextPrimary); Text("Back", modifier = Modifier.padding(start = 8.dp), color = AppTheme.colors.TextPrimary) }
            Text(title, style = AppTypography.H1); Spacer(modifier = Modifier.height(24.dp))
            AppCard {
                if (type == "WEIGHT") { 
                    Text("Weight (kg)", style = AppTypography.Subtitle, modifier = Modifier.padding(bottom = 8.dp)); 
                    OutlinedTextField(
                        value = val1, onValueChange = { val1 = it }, 
                        modifier = Modifier.fillMaxWidth(), 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = AppTheme.colors.TextPrimary, unfocusedTextColor = AppTheme.colors.TextPrimary, focusedBorderColor = AppTheme.colors.Primary, unfocusedBorderColor = AppTheme.colors.Border)
                    ) 
                } 
                else { 
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { 
                        Column(modifier = Modifier.weight(1f)) { 
                            Text("Systolic", style = AppTypography.Subtitle); 
                            OutlinedTextField(value = val1, onValueChange = { val1 = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = AppTheme.colors.TextPrimary, unfocusedTextColor = AppTheme.colors.TextPrimary, focusedBorderColor = AppTheme.colors.Primary, unfocusedBorderColor = AppTheme.colors.Border)) 
                        }; 
                        Column(modifier = Modifier.weight(1f)) { 
                            Text("Diastolic", style = AppTypography.Subtitle); 
                            OutlinedTextField(value = val2, onValueChange = { val2 = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = AppTheme.colors.TextPrimary, unfocusedTextColor = AppTheme.colors.TextPrimary, focusedBorderColor = AppTheme.colors.Primary, unfocusedBorderColor = AppTheme.colors.Border)) 
                        } 
                    } 
                }
                Spacer(modifier = Modifier.height(24.dp)); ActionButton("Save Record", onClick = { navController.popBackStack() }, variant = "FILLED")
            }
        }
    }
}

@Composable
fun MedicationDetailScreen(navController: NavController) {
    Scaffold(containerColor = AppTheme.colors.Background, bottomBar = { BottomNavBar(navController) }) { p ->
        Column(modifier = Modifier.padding(p).padding(24.dp).verticalScroll(rememberScrollState())) {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(24.dp).clickable { navController.popBackStack() }, tint = AppTheme.colors.TextPrimary)
                Text("Metformin", style = AppTypography.H1, modifier = Modifier.weight(1f).padding(start = 16.dp), fontSize = 20.sp)
                Row(verticalAlignment = Alignment.CenterVertically) { Text("ACTIVE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.TextSecondary, modifier = Modifier.padding(end = 8.dp)); Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppTheme.colors.Primary)) }
            }
            AppCard {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Text("TODAY'S PROGRESS", style = AppTypography.SectionHeader, color = AppTheme.colors.TextSecondary); Text("2 of 2 completed", color = AppTheme.colors.Primary, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                Spacer(modifier = Modifier.height(12.dp)); LinearProgressIndicator(progress = { 1f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = AppTheme.colors.Primary, trackColor = AppTheme.colors.InputBg)
            }
            Spacer(modifier = Modifier.height(24.dp)); Text("Frequency", style = AppTypography.CardTitle); Spacer(modifier = Modifier.height(12.dp)); SegmentedControl(listOf("Daily", "Weekly", "Custom"), 0) {}
            Spacer(modifier = Modifier.height(24.dp)); Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Text("Schedule", style = AppTypography.CardTitle); Text("2 Reminders Set", style = AppTypography.Subtitle, fontSize = 12.sp) }; Spacer(modifier = Modifier.height(12.dp))
            ReminderItem("08:00 AM", "1 tablet • Before meal", true); Spacer(modifier = Modifier.height(12.dp)); ReminderItem("08:00 PM", "1 tablet • Before meal", true); Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.Primary)) { Icon(Icons.Default.Add, null, tint = Color.White); Spacer(modifier = Modifier.width(8.dp)); Text("Add New Time", color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun ReminderItem(time: String, desc: String, enabled: Boolean) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(AppTheme.colors.InfoContainer, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.AccessTime, null, tint = AppTheme.colors.InfoContent, modifier = Modifier.size(24.dp)) }
            Spacer(modifier = Modifier.width(16.dp)); Column(modifier = Modifier.weight(1f)) { Text(time, style = AppTypography.CardTitle); Text(desc, style = AppTypography.Subtitle, fontSize = 12.sp) }
            Icon(Icons.Default.Edit, null, tint = AppTheme.colors.TextSecondary, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(16.dp))
            Switch(checked = enabled, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AppTheme.colors.Primary))
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Theme State
            var isDarkTheme by remember { mutableStateOf(true) } // Default to Dark for Auth screens flow as requested
            var userEmail by remember { mutableStateOf("guest@momhealth.app") } // Default generic email if bypassed
            var userName by remember { mutableStateOf("Guest User") }
            
            AppTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "signin") {
                    composable("signin") { SignInScreen(navController) { email, name -> userEmail = email; userName = name } }
                    composable("signup") { SignUpScreen(navController) { email, name -> userEmail = email; userName = name } }
                    composable("account_created") { AccountCreatedScreen(navController) }
                    
                    composable("login") { LoginScreen(navController) } // Keep old PIN login if needed
                    composable("home") { HomeScreen(navController) }
                    composable("calendar") { HistoryScreen(navController) }
                    composable("trends") { TrendsScreen(navController) }
                    composable("profile") { ProfileScreen(navController, isDarkTheme, userEmail, userName) { isDarkTheme = it } }
                    composable("meds") { MedicationDetailScreen(navController) }
                    composable("settings") { ProfileScreen(navController, isDarkTheme, userEmail, userName) { isDarkTheme = it } } 
                    composable("log/{type}") { bs -> LogScreen(bs.arguments?.getString("type") ?: "WEIGHT", navController) }
                }
            }
        }
    }
}
