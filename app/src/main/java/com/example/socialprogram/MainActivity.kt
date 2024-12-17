package com.example.socialprogram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.socialprogram.ui.theme.SocialprogramTheme
import com.example.socialprogram.ui.theme.screens.AppDatabase
import com.example.socialprogram.ui.theme.screens.HomeScreen
import com.example.socialprogram.ui.theme.screens.LoginScreen
import com.example.socialprogram.ui.theme.screens.LoginViewModel
import com.example.socialprogram.ui.theme.screens.UserDao

class MainActivity : ComponentActivity() {
    private lateinit var appDatabase: AppDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Room Database
        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()

        // Obtém o UserDao
        userDao = appDatabase.userDao()

        setContent {
            SocialprogramTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    // Passa o ViewModel com o UserDao para a tela de Login
                    NavigationGraph(navController = navController, userDao = userDao)
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, userDao: UserDao) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(viewModel = LoginViewModel(userDao), navController = navController)
        }
        composable("home") {
            HomeScreen(rememberNavController())
        }
    }
}
