package com.shitu.pickpic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shitu.pickpic.ui.screens.HomeScreen
import com.shitu.pickpic.ui.screens.PhotoPickerScreen
import com.shitu.pickpic.ui.screens.ResultScreen
import com.shitu.pickpic.ui.theme.ShiTuTheme
import com.shitu.pickpic.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShiTuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onNavigateToPicker = { navController.navigate("picker") })
        }
        composable("picker") {
            PhotoPickerScreen(
                onStartAnalysis = { photos ->
                    viewModel.analyzePhotos(photos)
                    navController.navigate("result")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("result") {
            ResultScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack("home", false) }
            )
        }
    }
}
