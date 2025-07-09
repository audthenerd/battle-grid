package com.example.battlegrid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.battlegrid.database.BattleGridDatabase
import com.example.battlegrid.repository.NineLinerRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class NineLiner(
    val line1: String = "", // IP/BP (Initial Point/Battle Position)
    val line2: String = "", // Heading/Offset
    val line3: String = "", // Distance
    val line4: String = "", // Target Elevation
    val line5: String = "", // Target Description
    val line6: String = "", // Target Location (Grid)
    val line7: String = "", // Type Mark
    val line8: String = "", // Location of Friendlies
    val line9: String = ""  // Egress
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val database = remember { BattleGridDatabase.getDatabase(context) }
    val repository = remember { NineLinerRepository(database.nineLinerDao()) }
    
    var nineLiner by remember { mutableStateOf(NineLiner()) }
    var requestType by remember { mutableStateOf("CAS") }
    var isTransmitting by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var transmissionResult by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }
    var currentDraftId by remember { mutableStateOf<Long?>(null) }
    
    // Load saved requests
    val draftRequests by repository.getDraftNineLiners().collectAsState(initial = emptyList())
    val transmittedRequests by repository.getTransmittedNineLiners().collectAsState(initial = emptyList())
    
    val scope = rememberCoroutineScope()
    
    val requestTypes = listOf("CAS", "MEDEVAC", "FIRE SUPPORT")
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "9 LINER REQUEST",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            // Request Type Selector
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Request Type",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        requestTypes.forEach { type ->
                            FilterChip(
                                onClick = { requestType = type },
                                label = { Text(type) },
                                selected = requestType == type
                            )
                        }
                    }
                }
            }
        }
        
        item {
            // 9 Liner Form
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "9 LINE REQUEST - $requestType",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Line 1: IP/BP
                    OutlinedTextField(
                        value = nineLiner.line1,
                        onValueChange = { nineLiner = nineLiner.copy(line1 = it) },
                        label = { Text("Line 1: IP/BP") },
                        supportingText = { Text("Initial Point/Battle Position") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isTransmitting && !isSaving
                    )
                    
                    // Line 2: Heading/Offset
                    OutlinedTextField(
                        value = nineLiner.line2,
                        onValueChange = { nineLiner = nineLiner.copy(line2 = it) },
                        label = { Text("Line 2: Heading/Offset") },
                        supportingText = { Text("Heading and offset from IP") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isTransmitting && !isSaving
                    )
                    
                    // Line 3: Distance
                    OutlinedTextField(
                        value = nineLiner.line3,
                        onValueChange = { nineLiner = nineLiner.copy(line3 = it) },
                        label = { Text("Line 3: Distance") },
                        supportingText = { Text("Distance from IP in meters/kilometers") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isTransmitting && !isSaving
                    )
                    
                    // Line 4: Target Elevation
                    OutlinedTextField(
                        value = nineLiner.line4,
                        onValueChange = { nineLiner = nineLiner.copy(line4 = it) },
                        label = { Text("Line 4: Target Elevation") },
                        supportingText = { Text("Target elevation in feet/meters") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isTransmitting && !isSaving
                    )
                    
                    // Line 5: Target Description
                    OutlinedTextField(
                        value = nineLiner.line5,
                        onValueChange = { nineLiner = nineLiner.copy(line5 = it) },
                        label = { Text("Line 5: Target Description") },
                        supportingText = { Text("Description of target") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        enabled = !isTransmitting && !isSaving
                    )
                    
                    // Line 6: Target Location
                    OutlinedTextField(
                        value = nineLiner.line6,
                        onValueChange = { nineLiner = nineLiner.copy(line6 = it) },
                        label = { Text("Line 6: Target Location") },
                        supportingText = { Text("Grid coordinates (MGRS)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isTransmitting && !isSaving
                    )
                    
                    // Line 7: Type Mark
                    OutlinedTextField(
                        value = nineLiner.line7,
                        onValueChange = { nineLiner = nineLiner.copy(line7 = it) },
                        label = { Text("Line 7: Type Mark") },
                        supportingText = { Text("Mark type (WP, IR, Laser, etc.)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isTransmitting && !isSaving
                    )
                    
                    // Line 8: Location of Friendlies
                    OutlinedTextField(
                        value = nineLiner.line8,
                        onValueChange = { nineLiner = nineLiner.copy(line8 = it) },
                        label = { Text("Line 8: Location of Friendlies") },
                        supportingText = { Text("Friendly forces location") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isTransmitting && !isSaving
                    )
                    
                    // Line 9: Egress
                    OutlinedTextField(
                        value = nineLiner.line9,
                        onValueChange = { nineLiner = nineLiner.copy(line9 = it) },
                        label = { Text("Line 9: Egress") },
                        supportingText = { Text("Egress direction/instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isTransmitting && !isSaving
                    )
                }
            }
        }
        
        item {
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Save Draft Button
                OutlinedButton(
                    onClick = {
                        if (nineLiner.line1.isNotBlank()) {
                            isSaving = true
                            scope.launch {
                                try {
                                    val draftId = repository.saveDraft(
                                        requestType = requestType,
                                        line1 = nineLiner.line1,
                                        line2 = nineLiner.line2,
                                        line3 = nineLiner.line3,
                                        line4 = nineLiner.line4,
                                        line5 = nineLiner.line5,
                                        line6 = nineLiner.line6,
                                        line7 = nineLiner.line7,
                                        line8 = nineLiner.line8,
                                        line9 = nineLiner.line9
                                    )
                                    currentDraftId = draftId
                                    transmissionResult = "Draft saved successfully!"
                                } catch (e: Exception) {
                                    transmissionResult = "Error saving draft: ${e.message}"
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isTransmitting && !isSaving && nineLiner.line1.isNotBlank()
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isSaving) "SAVING..." else "SAVE DRAFT")
                }
                
                // Transmit Button
                Button(
                    onClick = {
                        if (nineLiner.line1.isNotBlank()) {
                            isTransmitting = true
                            scope.launch {
                                try {
                                    // Save as transmitted request
                                    val requestId = currentDraftId ?: repository.saveDraft(
                                        requestType = requestType,
                                        line1 = nineLiner.line1,
                                        line2 = nineLiner.line2,
                                        line3 = nineLiner.line3,
                                        line4 = nineLiner.line4,
                                        line5 = nineLiner.line5,
                                        line6 = nineLiner.line6,
                                        line7 = nineLiner.line7,
                                        line8 = nineLiner.line8,
                                        line9 = nineLiner.line9
                                    )
                                    
                                    // Simulate transmission
                                    kotlinx.coroutines.delay(2000)
                                    
                                    // Mark as transmitted
                                    repository.transmitNineLiner(requestId)
                                    
                                    transmissionResult = "9 LINER TRANSMITTED SUCCESSFULLY\nRequest ID: $requestId\nTimestamp: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}"
                                    
                                    // Clear form after successful transmission
                                    nineLiner = NineLiner()
                                    currentDraftId = null
                                } catch (e: Exception) {
                                    transmissionResult = "Transmission failed: ${e.message}"
                                } finally {
                                    isTransmitting = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isTransmitting && !isSaving && nineLiner.line1.isNotBlank()
                ) {
                    if (isTransmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (isTransmitting) "TRANSMITTING..." else "TRANSMIT")
                }
            }
        }
        
        item {
            // Secondary Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        nineLiner = NineLiner()
                        transmissionResult = ""
                        currentDraftId = null
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isTransmitting && !isSaving
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CLEAR")
                }
                
                OutlinedButton(
                    onClick = { showHistory = !showHistory },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("HISTORY")
                }
            }
        }
        
        item {
            // Transmission Result
            if (transmissionResult.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "TRANSMISSION STATUS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = transmissionResult,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        item {
            // Quick Reference
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "QUICK REFERENCE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val referenceText = when (requestType) {
                        "CAS" -> """
                            CAS (Close Air Support):
                            • Ensure deconfliction with friendlies
                            • Provide accurate target description
                            • Confirm egress route is clear
                            • Have backup comms ready
                        """.trimIndent()
                        
                        "MEDEVAC" -> """
                            MEDEVAC (Medical Evacuation):
                            • Line 4: Number of patients
                            • Line 5: Precedence (URGENT/PRIORITY/ROUTINE)
                            • Line 6: Special equipment needed
                            • Line 8: Security at pickup site
                        """.trimIndent()
                        
                        "FIRE SUPPORT" -> """
                            FIRE SUPPORT:
                            • Line 4: Target altitude/elevation
                            • Line 5: Target description and size
                            • Line 7: Method of engagement
                            • Line 8: Danger close distance
                        """.trimIndent()
                        
                        else -> "Select request type for specific guidance"
                    }
                    
                    Text(
                        text = referenceText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        // History Section
        item {
            if (showHistory) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "REQUEST HISTORY",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // All Requests Section
                        val allRequests = (draftRequests + transmittedRequests).sortedByDescending { it.createdAt }
                        
                        if (allRequests.isNotEmpty()) {
                            Text(
                                text = "All 9-Liner Requests (${allRequests.size})",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            
                            // Show all requests
                            allRequests.forEach { request ->
                                SavedRequestItem(
                                    request = request,
                                    onLoad = { loadedRequest ->
                                        nineLiner = NineLiner(
                                            line1 = loadedRequest.line1,
                                            line2 = loadedRequest.line2,
                                            line3 = loadedRequest.line3,
                                            line4 = loadedRequest.line4,
                                            line5 = loadedRequest.line5,
                                            line6 = loadedRequest.line6,
                                            line7 = loadedRequest.line7,
                                            line8 = loadedRequest.line8,
                                            line9 = loadedRequest.line9
                                        )
                                        requestType = loadedRequest.requestType
                                        currentDraftId = if (request.status == "DRAFT") request.id else null
                                        showHistory = false
                                        transmissionResult = if (request.status == "DRAFT") "Draft loaded" else "Request template loaded"
                                    },
                                    onDelete = if (request.status == "DRAFT") { requestToDelete ->
                                        scope.launch {
                                            repository.deleteNineLiner(requestToDelete)
                                        }
                                    } else null, // Can't delete transmitted requests
                                    isTransmitted = request.status == "TRANSMITTED"
                                )
                            }
                        }
                        
                        if (allRequests.isEmpty()) {
                            Text(
                                text = "No saved 9-liner requests found.\nCreate and save a request to see it here!",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedRequestItem(
    request: com.example.battlegrid.database.entities.NineLiner,
    onLoad: (com.example.battlegrid.database.entities.NineLiner) -> Unit,
    onDelete: ((com.example.battlegrid.database.entities.NineLiner) -> Unit)? = null,
    isTransmitted: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
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
                    text = "${request.requestType} - ${request.line5.take(30)}${if (request.line5.length > 30) "..." else ""}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (isTransmitted) {
                        "Transmitted: ${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(request.transmittedAt ?: request.createdAt))}"
                    } else {
                        "Saved: ${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(request.createdAt))}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Grid: ${request.line6}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Row {
                TextButton(onClick = { onLoad(request) }) {
                    Text("Load")
                }
                
                if (onDelete != null) {
                    TextButton(onClick = { onDelete(request) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
} 