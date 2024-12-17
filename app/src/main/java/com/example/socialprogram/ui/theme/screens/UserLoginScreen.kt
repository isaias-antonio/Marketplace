package com.example.socialprogram.ui.theme.screens


import android.content.Context
import androidx.room.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// 1. Modelo de Dados
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String
)

// 2. DAO
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUserByCredentials(username: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)
}

// 3. Banco de Dados
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        private val applicationScope = CoroutineScope(SupervisorJob())
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                applicationScope.launch {
                    val dao = database.userDao()
                    dao.insert(User(username = "usuario1", password = "senha1"))
                    dao.insert(User(username = "usuario2", password = "senha2"))
                }
            }
        }
    }
}

// 4. ViewModel
class LoginViewModel(private val userDao: UserDao) : ViewModel() {
    var loginSuccess: ((Boolean) -> Unit)? = null
    var errorMessage: ((String) -> Unit)? = null

    fun validateCredentials(username: String, password: String) {
        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    userDao.getUserByCredentials(username, password)
                }
                loginSuccess?.invoke(user != null)
            } catch (e: Exception) {
                errorMessage?.invoke("Erro ao acessar o banco de dados")
            }
        }
    }
}

// 5. Tela de Login
@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    viewModel.loginSuccess = { success ->
        if (success) {
            navController.navigate("home")
        } else {
            errorMessage = "Usuário ou senha incorretos!"
        }
    }

    viewModel.errorMessage = {
        errorMessage = it
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuário") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Senha") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (username.isBlank() || password.isBlank()) {
                errorMessage = "Preencha todos os campos"
            } else {
                viewModel.validateCredentials(username, password)
            }
        }) {
            Text("Entrar")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val mockDao = object : UserDao {
        override suspend fun getUserByCredentials(username: String, password: String): User? {
            return null
        }

        override suspend fun insert(user: User) {}
    }
    val viewModel = LoginViewModel(mockDao)
    val navController = rememberNavController()
    LoginScreen(viewModel = viewModel, navController = navController)
}













