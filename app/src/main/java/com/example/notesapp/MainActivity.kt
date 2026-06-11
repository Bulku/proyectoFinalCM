package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.notesapp.preferences.AppPreferencesManager
import com.example.notesapp.screens.AiAssistantScreen
import com.example.notesapp.screens.CalendarScreen
import com.example.notesapp.screens.NotesScreen
import com.example.notesapp.screens.SettingsScreen
import com.example.notesapp.ui.theme.AppColorOption
import com.example.notesapp.ui.theme.AppFontOption
import com.example.notesapp.ui.theme.NotesAppTheme
import com.example.notesapp.viewmodel.AiViewModel
import com.example.notesapp.viewmodel.NotesViewModel

data class BottomNavItem(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {

    private val viewModel: NotesViewModel by viewModels()
    private val aiViewModel: AiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = AppPreferencesManager(this)

        setContent {
            var colorOption by remember { mutableStateOf(preferencesManager.getColorOption()) }
            var fontOption by remember { mutableStateOf(preferencesManager.getFontOption()) }

            NotesAppTheme(
                colorOption = colorOption,
                fontOption = fontOption
            ) {
                MainScreen(
                    viewModel = viewModel,
                    aiViewModel = aiViewModel,
                    colorOption = colorOption,
                    fontOption = fontOption,
                    onColorOptionChange = { option ->
                        colorOption = option
                        preferencesManager.saveColorOption(option)
                    },
                    onFontOptionChange = { option ->
                        fontOption = option
                        preferencesManager.saveFontOption(option)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: NotesViewModel,
    aiViewModel: AiViewModel,
    colorOption: AppColorOption,
    fontOption: AppFontOption,
    onColorOptionChange: (AppColorOption) -> Unit,
    onFontOptionChange: (AppFontOption) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        BottomNavItem(R.string.tab_notes, Icons.Filled.StickyNote2, Icons.Outlined.StickyNote2),
        BottomNavItem(R.string.tab_calendar, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
        BottomNavItem(R.string.tab_ai, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
        BottomNavItem(R.string.tab_settings, Icons.Filled.Palette, Icons.Outlined.Palette)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(tabs[selectedTab].labelRes),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(item.labelRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                0 -> NotesScreen(viewModel)
                1 -> CalendarScreen(viewModel)
                2 -> AiAssistantScreen(aiViewModel, viewModel)
                3 -> SettingsScreen(
                    colorOption = colorOption,
                    fontOption = fontOption,
                    onColorOptionChange = onColorOptionChange,
                    onFontOptionChange = onFontOptionChange
                )
            }
        }
    }
}
