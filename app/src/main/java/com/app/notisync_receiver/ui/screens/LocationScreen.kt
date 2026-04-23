// ============================================================
// FILE: app/src/main/java/com/app/notisync_receiver/ui/screens/LocationScreen.kt
// Purpose: Displays real-time location and history for a device
// ============================================================

package com.app.notisync_receiver.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.notisync_receiver.domain.model.DeviceLocation
import com.app.notisync_receiver.viewmodel.LocationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    onNavigateBack: () -> Unit,
    viewModel: LocationViewModel = hiltViewModel()
) {
    val locations by viewModel.locations.collectAsStateWithLifecycle()
    val isRequesting by viewModel.isRequesting.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val latestLocation = locations.firstOrNull()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val openInMaps: (DeviceLocation) -> Unit = { location ->
        val uri = "geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(${location.deviceName})"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Location Tracking",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = viewModel.deviceName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.requestLocationUpdate() },
                        enabled = !isRequesting
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Request Update")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
        ) {
            // Error banner if any
            if (errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearError() }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                }
            }

            // Latest Location Card
            item {
                LatestLocationCard(
                    location = latestLocation,
                    isRequesting = isRequesting,
                    onUpdateClick = { viewModel.requestLocationUpdate() },
                    onOpenInMaps = { latestLocation?.let { openInMaps(it) } }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // History List
                Text(
                    text = "Location History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (locations.isEmpty()) {
                item {
                    EmptyHistoryState()
                }
            } else {
                items(locations) { location ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        LocationHistoryItem(
                            location = location,
                            onDelete = { viewModel.deleteLocation(location.locationId) },
                            onOpenInMaps = { openInMaps(location) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun LatestLocationCard(
    location: DeviceLocation?,
    isRequesting: Boolean,
    onUpdateClick: () -> Unit,
    onOpenInMaps: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Current Status",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (location != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = onOpenInMaps) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Open in Maps",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Open in Map",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 0.dp) // Reset padding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (location != null) {
                Text(text = "Latitude: ${location.latitude}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Longitude: ${location.longitude}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Accuracy: ${location.accuracy}m", style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProviderBadge(location.provider)
                    val statusText = when (location.provider) {
                        DeviceLocation.LocationProvider.NETWORK -> "(GPS may be OFF)"
                        DeviceLocation.LocationProvider.CELL_ID -> "(Estimate only)"
                        DeviceLocation.LocationProvider.IP -> "(Estimate only)"
                        else -> null
                    }
                    
                    if (statusText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Text(
                    text = "Last Updated: ${formatTime(location.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            } else {
                Text(text = "No location data available yet.", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onUpdateClick,
                enabled = !isRequesting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isRequesting) "Requesting..." else "Get Real-time Location")
            }
        }
    }
}

@Composable
fun LocationHistoryItem(
    location: DeviceLocation,
    onDelete: () -> Unit,
    onOpenInMaps: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${location.latitude}, ${location.longitude}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formatTime(location.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ProviderBadge(location.provider)
            }
        }
        
        Row {
            IconButton(onClick = onOpenInMaps) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open in Maps",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp)
}

@Composable
fun ProviderBadge(provider: DeviceLocation.LocationProvider) {
    val color = when (provider) {
        DeviceLocation.LocationProvider.GPS -> MaterialTheme.colorScheme.tertiary
        DeviceLocation.LocationProvider.NETWORK -> MaterialTheme.colorScheme.secondary
        DeviceLocation.LocationProvider.FUSED -> MaterialTheme.colorScheme.primary
        DeviceLocation.LocationProvider.CELL_ID -> MaterialTheme.colorScheme.secondary
        DeviceLocation.LocationProvider.IP -> MaterialTheme.colorScheme.secondary
        DeviceLocation.LocationProvider.UNKNOWN -> MaterialTheme.colorScheme.outline
    }
    
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = provider.name,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyHistoryState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(64.dp).alpha(0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No location history", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.alpha(0.5f))
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
