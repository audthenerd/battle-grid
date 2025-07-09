package com.example.battlegrid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.battlegrid.database.BattleGridDatabase
import com.example.battlegrid.database.entities.User 
import com.example.battlegrid.repository.UserRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val database = remember { BattleGridDatabase.getDatabase(context) }
    val repository = remember { UserRepository(database.userDao()) }
    
    // State for users list
    val users by repository.getAllActiveUsers().collectAsState(initial = emptyList())
    
    // State for form
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "User Management",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // User creation form
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Create New User",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    supportingText = { Text("Enter a unique username (3-20 characters)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    supportingText = { Text("Enter a valid email address") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                
                Button(
                    onClick = {
                        if (username.isNotBlank() && email.isNotBlank()) {
                            isLoading = true
                            message = "" // Clear previous messages
                            scope.launch {
                                try {
                                    val userId = repository.createUser(username, email)
                                    message = "User created successfully! ID: $userId"
                                    username = ""
                                    email = ""
                                } catch (e: IllegalArgumentException) {
                                    message = e.message ?: "Invalid input"
                                } catch (e: Exception) {
                                    message = "Error creating user: ${e.message}"
                                    android.util.Log.e("UserScreen", "Error creating user", e)
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            message = "Please fill in all fields"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Create User")
                    }
                }
                
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (message.contains("Error") || message.contains("exists")) 
                               MaterialTheme.colorScheme.error 
                               else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Users list
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Active Users (${users.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (users.isEmpty()) {
                    Text(
                        text = "No users found. Create one above!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users) { user ->
                            UserItem(
                                user = user,
                                onRemove = {
                                    scope.launch {
                                        try {
                                            repository.deactivateUser(user.id)
                                            message = "User ${user.username} deactivated"
                                        } catch (e: Exception) {
                                            message = "Error removing user: ${e.message}"
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserItem(
    user: User,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Level ${user.level} • ${user.experience} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            TextButton(onClick = onRemove) {
                Text("Remove")
            }
        }
    }
} 