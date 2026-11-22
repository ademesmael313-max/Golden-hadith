package com.goldenhadith.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.goldenhadith.app.data.Hadith
import com.goldenhadith.app.ui.screens.BookmarksScreen
import com.goldenhadith.app.ui.screens.BrowseScreen
import com.goldenhadith.app.ui.screens.HadithDetailScreen
import com.goldenhadith.app.ui.screens.HomeScreen
import com.goldenhadith.app.ui.theme.GoldenHadithTheme
import com.goldenhadith.app.viewmodel.HadithViewModel

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Browse : Screen("browse", "Browse", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    data object Bookmarks : Screen("bookmarks", "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoldenHadithTheme {
                GoldenHadithApp()
            }
        }
    }
}

@Composable
fun GoldenHadithApp(viewModel: HadithViewModel = viewModel()) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val allHadiths by viewModel.allHadiths.collectAsState()
    val filteredHadiths by viewModel.filteredHadiths.collectAsState()
    val bookmarkedHadiths by viewModel.bookmarkedHadiths.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomScreens = listOf(Screen.Home, Screen.Browse, Screen.Bookmarks)
    val showBottomBar = currentRoute in bottomScreens.map { it.route }

    fun shareHadith(hadith: Hadith) {
        val text = buildString {
            appendLine(hadith.arabic)
            appendLine()
            appendLine("“${hadith.english}”")
            appendLine()
            appendLine("— ${hadith.narrator} (${hadith.source})")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Hadith"))
    }

    fun copyHadith(hadith: Hadith) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = "${hadith.arabic}\n\n“${hadith.english}”\n— ${hadith.narrator} (${hadith.source})"
        clipboard.setPrimaryClip(ClipData.newPlainText("Hadith", text))
        Toast.makeText(context, "Hadith copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomScreens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    dailyHadith = viewModel.getDailyHadith(),
                    allHadiths = allHadiths,
                    categories = categories,
                    bookmarkedIds = bookmarkedIds,
                    onHadithClick = { id -> navController.navigate("detail/$id") },
                    onBookmarkClick = { id -> viewModel.toggleBookmark(id) },
                    onShareClick = { hadith -> shareHadith(hadith) },
                    onCategoryClick = { category ->
                        viewModel.selectCategory(category)
                        navController.navigate(Screen.Browse.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.Browse.route) {
                BrowseScreen(
                    hadiths = filteredHadiths,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    searchQuery = searchQuery,
                    bookmarkedIds = bookmarkedIds,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onHadithClick = { id -> navController.navigate("detail/$id") },
                    onBookmarkClick = { id -> viewModel.toggleBookmark(id) },
                    onShareClick = { hadith -> shareHadith(hadith) }
                )
            }

            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    bookmarkedHadiths = bookmarkedHadiths,
                    onHadithClick = { id -> navController.navigate("detail/$id") },
                    onBookmarkClick = { id -> viewModel.toggleBookmark(id) },
                    onShareClick = { hadith -> shareHadith(hadith) }
                )
            }

            composable(
                route = "detail/{hadithId}",
                arguments = listOf(navArgument("hadithId") { type = NavType.IntType })
            ) { backStackEntry ->
                val hadithId = backStackEntry.arguments?.getInt("hadithId") ?: 1
                val hadith = viewModel.getHadithById(hadithId)
                if (hadith != null) {
                    HadithDetailScreen(
                        hadith = hadith,
                        isBookmarked = bookmarkedIds.contains(hadith.id),
                        fontScale = fontScale,
                        onBackClick = { navController.popBackStack() },
                        onBookmarkClick = { viewModel.toggleBookmark(hadith.id) },
                        onShareClick = { shareHadith(hadith) },
                        onCopyClick = { copyHadith(hadith) },
                        onIncreaseFont = { viewModel.increaseFontScale() },
                        onDecreaseFont = { viewModel.decreaseFontScale() }
                    )
                }
            }
        }
    }
}
