package com.first.mynew_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

// --- DESIGN SYSTEM ---
object AppColors {
    val Background = Color(0xFFF8F9FB)
    val Surface = Color(0xFFFFFFFF)
    val Primary = Color(0xFF0F515F) // Teal/Petrol
    val Accent = Color(0xFF0EA5E9)   // Sky Blue
    val TextPrimary = Color(0xFF1E293B)
    val TextSecondary = Color(0xFF64748B)
    val Border = Color(0xFFE2E8F0)
    val IconTint = Color(0xFF475569)
}

object AppTypography {
    val H1 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary, letterSpacing = (-0.5).sp)
    val SectionHeader = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary, letterSpacing = 1.sp)
    val CardTitle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
    val Subtitle = TextStyle(fontSize = 14.sp, color = AppColors.TextSecondary)
    val Value = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
}

// --- COMPONENTS ---

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = AppColors.Surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Border),
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
            containerColor = if (variant == "FILLED") AppColors.Primary else Color.Transparent,
            contentColor = if (variant == "FILLED") Color.White else AppColors.TextPrimary
        ),
        border = if (variant == "OUTLINE") androidx.compose.foundation.BorderStroke(1.dp, AppColors.Border) else null,
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
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, item ->
            Box(
                modifier = Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(6.dp))
                    .background(if (selectedIndex.intValue == index) Color.White else Color.Transparent)
                    .clickable { selectedIndex.intValue = index; onItemSelection(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(item, fontSize = 13.sp, fontWeight = if (selectedIndex.intValue == index) FontWeight.SemiBold else FontWeight.Normal, color = AppColors.TextPrimary)
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
                Icon(icon, null, tint = AppColors.TextSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(label, style = AppTypography.Value)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = AppColors.Border, modifier = Modifier.size(14.dp))
    }
}

@Composable
fun InsightCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    AppCard {
        Row(crossAxisAlignment = Alignment.Start) {
            Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(24.dp).padding(top = 2.dp))
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
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = AppColors.Border, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun IconBox(icon: ImageVector, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(40.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
    }
}

// --- SCREENS ---

@Composable
fun HistoryScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = AppColors.Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("log/WEIGHT") },
                containerColor = Color(0xFF0E7490),
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.size(64.dp).offset(y = 48.dp) // Visual hack for bottom nav overlap
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center
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
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = AppColors.Primary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.Search, null, tint = AppColors.TextPrimary)
                        Icon(Icons.Default.Tune, null, tint = AppColors.TextPrimary)
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
    Scaffold(bottomBar = { BottomNavBar(navController) }, containerColor = AppColors.Background) { p ->
        LazyColumn(modifier = Modifier.padding(p).padding(24.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Column {
                        Text("Health Trends", style = AppTypography.H1)
                        Text("Analytics & Insights", style = AppTypography.Subtitle)
                    }
                    Box(modifier = Modifier.size(40.dp).border(1.dp, AppColors.Border, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CalendarToday, null, tint = AppColors.TextPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }

            item {
                SegmentedControl(listOf("Week", "Month", "Year"), 0) {}
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                AppCard {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("WEIGHT TREND", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("62.4 kg", style = AppTypography.H1)
                                Text(" Average", style = AppTypography.Subtitle, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                            Text("-0.8 kg", color = Color(0xFF166534), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    // Simple Chart Placeholder
                    Row(modifier = Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { i, day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(8.dp).background(if (i == 3) AppColors.Primary else AppColors.Border, androidx.compose.foundation.shape.CircleShape))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(day, style = TextStyle(fontSize = 12.sp, color = AppColors.TextSecondary))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.FavoriteBorder, null, tint = AppColors.Primary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Blood Pressure", style = AppTypography.CardTitle)
                            Text("Average: 118/76 mmHg", style = AppTypography.Subtitle)
                        }
                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                            Text("● NORMAL", color = Color(0xFF166534), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item { SectionHeader("HEALTH INSIGHTS") }
            
            item {
                 InsightCard("BLOOD PRESSURE PATTERN", Icons.Default.Lightbulb) {
                     Text("• Systolic readings are consistently 5% higher in the morning (6AM-9AM).", style = AppTypography.Subtitle)
                     Text("• Stability improved following consistent medication adherence last week.", style = AppTypography.Subtitle, modifier = Modifier.padding(top = 8.dp))
                 }
                 Spacer(modifier = Modifier.height(12.dp))
                 InsightCard("WEIGHT CORRELATION", Icons.AutoMirrored.Filled.TrendingDown) {
                      Text("• Downward trend correlates with 15% increase in tracked physical activity.", style = AppTypography.Subtitle)
                 }
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(bottomBar = { BottomNavBar(navController) }, containerColor = AppColors.Background) { p ->
        Column(modifier = Modifier.padding(p).padding(24.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(100.dp).background(Color(0xFFE2E8F0), androidx.compose.foundation.shape.CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Sarah Jenkins", style = AppTypography.H1)
            Text("sarah.j@example.com", style = AppTypography.Subtitle)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AppCard {
                InfoRow("Personal Information", "", Icons.Default.Person)
                HorizontalDivider(color = AppColors.Border, thickness = 0.5.dp)
                InfoRow("Health Goals", "", Icons.Default.TrackChanges)
                HorizontalDivider(color = AppColors.Border, thickness = 0.5.dp)
                InfoRow("Notifications", "", Icons.Default.Notifications)
                HorizontalDivider(color = AppColors.Border, thickness = 0.5.dp)
                InfoRow("Security", "", Icons.Default.Security)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Sign Out */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2), contentColor = Color(0xFFEF4444)),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null, modifier = Modifier.padding(end = 8.dp))
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Text("VERSION 2.4.0", style = TextStyle(fontSize = 10.sp, color = AppColors.TextSecondary, letterSpacing = 1.sp))
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(bottomBar = { BottomNavBar(navController) }, containerColor = AppColors.Background) { p ->
        LazyColumn(modifier = Modifier.padding(p).padding(24.dp)) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text("Health Dashboard", style = AppTypography.H1); Text("Monday, May 22", style = AppTypography.Subtitle) }
                    IconButton(onClick = { navController.navigate("settings") }, modifier = Modifier.border(1.dp, AppColors.Border, RoundedCornerShape(8.dp)).size(40.dp)) { Icon(Icons.Outlined.Settings, null, tint = AppColors.IconTint, modifier = Modifier.size(20.dp)) }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppCard(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Outlined.MonitorWeight, null, tint = AppColors.Primary, modifier = Modifier.size(24.dp)); Text("WEIGHT", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)) }
                        Spacer(modifier = Modifier.height(16.dp)); Text("Last: 62.4 kg", style = AppTypography.Value); Spacer(modifier = Modifier.height(16.dp)); ActionButton("Log Entry", onClick = { navController.navigate("log/WEIGHT") }, variant = "OUTLINE")
                    }
                    AppCard(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Outlined.FavoriteBorder, null, tint = AppColors.Primary, modifier = Modifier.size(24.dp)); Text("BP", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)) }
                        Spacer(modifier = Modifier.height(16.dp)); Text("Last: 118/76", style = AppTypography.Value); Spacer(modifier = Modifier.height(16.dp)); ActionButton("Log Entry", onClick = { navController.navigate("log/BP") }, variant = "OUTLINE")
                    }
                }
            }
            item {
                SectionHeader("Well-being")
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) { IconBox(Icons.Outlined.Mood); Spacer(modifier = Modifier.width(16.dp)); Column(modifier = Modifier.weight(1f)) { Text("Mood & Symptoms", style = AppTypography.CardTitle); Text("No data logged today", style = AppTypography.Subtitle) }; Button(onClick = { /* TODO */ }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), modifier = Modifier.height(36.dp)) { Text("+ Log", fontSize = 12.sp) } }
                    Spacer(modifier = Modifier.height(16.dp)); Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))) { Box(modifier = Modifier.fillMaxWidth(0.3f).height(4.dp).background(AppColors.Primary, RoundedCornerShape(2.dp))) }
                }
            }
            item {
                SectionHeader("Medication")
                AppCard {
                    Row(verticalAlignment = Alignment.Top) { IconBox(Icons.Outlined.MedicalServices); Spacer(modifier = Modifier.width(16.dp)); Column(modifier = Modifier.weight(1f)) { Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { Text("Active Prescription", style = AppTypography.CardTitle); Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) { Text("1 Remaining", fontSize = 10.sp, color = AppColors.TextSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) } }; Text("Prenatal Vitamin • 8:00 AM", style = AppTypography.Subtitle, modifier = Modifier.padding(top = 4.dp)) } }
                    Spacer(modifier = Modifier.height(16.dp)); ActionButton("Mark as Taken", onClick = { /* TODO */ }, variant = "FILLED"); Spacer(modifier = Modifier.height(16.dp)); Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))) { Box(modifier = Modifier.fillMaxWidth(0.5f).height(4.dp).background(AppColors.Primary, RoundedCornerShape(2.dp))) }
                }
            }
            item {
                SectionHeader("Clinical Reports")
                AppCard { Column { ReportItem("Monthly Health Summary"); HorizontalDivider(color = AppColors.Border, thickness = 0.5.dp); ReportItem("Export Clinical Data (PDF)") } }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ReportItem(title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { }, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Description, null, tint = AppColors.TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = AppTypography.CardTitle, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, null, tint = AppColors.Border, modifier = Modifier.size(14.dp))
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    // Updated list to match screenshot: Home, Meds, Vitals (Trends), Track (History), Settings (Profile)
    // Using 5 items based on the visual layout preference
    val items = listOf(
        "Home" to Icons.Default.Home,
        "Meds" to Icons.Filled.Medication,
        "Vitals" to Icons.Default.BarChart,
        "Track" to Icons.Default.CalendarToday, 
        "Settings" to Icons.Default.Settings
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = AppColors.Surface, tonalElevation = 10.dp) {
        items.forEach { (label, icon) ->
            val route = when(label) {
                "Home" -> "home"
                "Meds" -> "meds"
                "Vitals" -> "trends"
                "Track" -> "calendar" // History
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
                    selectedIconColor = Color(0xFF0EA5E9), // Sky Blue for selection
                    selectedTextColor = Color(0xFF0EA5E9),
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = AppColors.TextSecondary,
                    unselectedTextColor = AppColors.TextSecondary
                )
            )
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var pin by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().background(AppColors.Background).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Filled.Lock, null, tint = AppColors.Primary, modifier = Modifier.size(48.dp)); Spacer(modifier = Modifier.height(32.dp))
        Text("Welcome Back", style = AppTypography.H1); Text("Enter PIN to continue", style = AppTypography.Subtitle); Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = pin, onValueChange = { if (it.length <= 4) pin = it }, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = AppColors.Surface, unfocusedContainerColor = AppColors.Surface, focusedBorderColor = AppColors.Primary, unfocusedBorderColor = AppColors.Border))
        Spacer(modifier = Modifier.height(24.dp)); ActionButton("Unlock Account", onClick = { if (pin.length == 4) navController.navigate("home") }, variant = "FILLED")
    }
}

@Composable
fun LogScreen(type: String, navController: NavController) {
    var val1 by remember { mutableStateOf("") }
    var val2 by remember { mutableStateOf("") }
    val title = if (type == "WEIGHT") "Log Weight" else "Log BP"
    Scaffold(containerColor = AppColors.Background) { p ->
        Column(modifier = Modifier.padding(p).padding(24.dp)) {
            Row(modifier = Modifier.padding(bottom = 24.dp).clickable { navController.popBackStack() }, verticalAlignment = Alignment.CenterVertically) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.TextPrimary); Text("Back", modifier = Modifier.padding(start = 8.dp), color = AppColors.TextPrimary) }
            Text(title, style = AppTypography.H1); Spacer(modifier = Modifier.height(24.dp))
            AppCard {
                if (type == "WEIGHT") { Text("Weight (kg)", style = AppTypography.Subtitle, modifier = Modifier.padding(bottom = 8.dp)); OutlinedTextField(value = val1, onValueChange = { val1 = it }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) } 
                else { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { Column(modifier = Modifier.weight(1f)) { Text("Systolic", style = AppTypography.Subtitle); OutlinedTextField(value = val1, onValueChange = { val1 = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }; Column(modifier = Modifier.weight(1f)) { Text("Diastolic", style = AppTypography.Subtitle); OutlinedTextField(value = val2, onValueChange = { val2 = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) } } }
                Spacer(modifier = Modifier.height(24.dp)); ActionButton("Save Record", onClick = { navController.popBackStack() }, variant = "FILLED")
            }
        }
    }
}

@Composable
fun MedicationDetailScreen(navController: NavController) {
    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = { BottomNavBar(navController) }
    ) { p ->
        Column(modifier = Modifier.padding(p).padding(24.dp).verticalScroll(rememberScrollState())) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    modifier = Modifier.size(24.dp).clickable { navController.popBackStack() },
                    tint = AppColors.TextPrimary
                )
                Text(
                    "Metformin", 
                    style = AppTypography.H1, 
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    fontSize = 20.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ACTIVE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary, modifier = Modifier.padding(end = 8.dp))
                    Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0EA5E9)))
                }
            }

            // Progress Card
            AppCard {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("TODAY'S PROGRESS", style = AppTypography.SectionHeader, color = AppColors.TextSecondary)
                    Text("2 of 2 completed", color = Color(0xFF0EA5E9), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF0EA5E9),
                    trackColor = Color(0xFFE2E8F0),
                )
            }
            
            // Frequency Section
            Spacer(modifier = Modifier.height(24.dp))
            Text("Frequency", style = AppTypography.CardTitle)
            Spacer(modifier = Modifier.height(12.dp))
            SegmentedControl(listOf("Daily", "Weekly", "Custom"), 0) {}

            // Schedule Section
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Schedule", style = AppTypography.CardTitle)
                Text("2 Reminders Set", style = AppTypography.Subtitle, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Reminder Items
            ReminderItem("08:00 AM", "1 tablet • Before meal", true)
            Spacer(modifier = Modifier.height(12.dp))
            ReminderItem("08:00 PM", "1 tablet • Before meal", true)

            Spacer(modifier = Modifier.height(24.dp))
            
            // Add Button
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490)) // Teal
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Time", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReminderItem(time: String, desc: String, enabled: Boolean) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFE0F2FE), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccessTime, null, tint = Color(0xFF0284C7), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(time, style = AppTypography.CardTitle)
                Text(desc, style = AppTypography.Subtitle, fontSize = 12.sp)
            }
            Icon(Icons.Default.Edit, null, tint = AppColors.TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Switch(checked = enabled, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0EA5E9)))
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("home") { HomeScreen(navController) }
                composable("calendar") { HistoryScreen(navController) }
                composable("trends") { TrendsScreen(navController) }
                composable("profile") { ProfileScreen(navController) }
                composable("meds") { MedicationDetailScreen(navController) } // Added route
                composable("settings") { /* Placeholder */ }
                composable("log/{type}") { bs -> LogScreen(bs.arguments?.getString("type") ?: "WEIGHT", navController) }
            }
        }
    }
}
