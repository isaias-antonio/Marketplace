package com.example.socialprogram.authenticationRepository


//import android.content.Context
//import com.example.socialprogram.database.User
//import com.example.socialprogram.database.UserDatabase
//
//
//class AuthRepository(context: Context) {
//    private val userDao = UserDatabase.getDatabase(context).userDao()
//
//    // Função para adicionar usuários predefinidos ao banco de dados
//    suspend fun initializeUsers() {
//        // Verificar se já existem usuários no banco de dados
//        val userCount = userDao.getUserCount()
//        if (userCount == 0) {
//            val user1 = User(email = "user1@example.com", password = "password123")
//            val user2 = User(email = "user2@example.com", password = "password456")
//            userDao.insert(user1)
//            userDao.insert(user2)
//        }
//    }
//
//    // Função de login para verificar se o usuário existe no banco de dados
//    suspend fun loginUser(email: String, password: String): Boolean {
//        val user = userDao.getUserByEmailAndPassword(email, password)
//        return user != null
//    }
//}

