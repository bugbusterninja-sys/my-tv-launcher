@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.cleanyourtv.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun LauncherScreen(apps: List<AppItem>, onLaunch: (AppItem) -> Unit) {
    val pinned = remember(apps) {
        AppRepository.pinnedOrder.mapNotNull { pkg -> apps.firstOrNull { it.packageName == pkg } }
    }
    val rest = remember(apps, pinned) { apps.filterNot { it in pinned } }

    var focused by remember { mutableStateOf<AppItem?>(null) }
    val firstFocus = remember { FocusRequester() }
    val firstPackage = (pinned.firstOrNull() ?: rest.firstOrNull())?.packageName

    // A Home screen should never "go back" to anything.
    BackHandler(enabled = true) { }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(timeOfDayColors()))
    ) {
        // Big, soft backdrop of whichever app is focused (Apple TV style).
        Crossfade(targetState = focused, label = "backdrop") { item ->
            if (item != null) {
                Image(
                    bitmap = item.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.18f,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // Dark scrim so text stays readable.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x99000000), Color(0x00000000), Color(0xCC000000))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 32.dp)
        ) {
            Header()
            Spacer(modifier = Modifier.weight(1f))

            if (pinned.isNotEmpty()) {
                SectionTitle("Favourites")
                AppRow(
                    items = pinned,
                    tileWidth = 280.dp,
                    firstPackage = firstPackage,
                    firstFocus = firstFocus,
                    onFocused = { focused = it },
                    onLaunch = onLaunch,
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            SectionTitle("All apps")
            AppRow(
                items = rest,
                tileWidth = 200.dp,
                firstPackage = firstPackage,
                firstFocus = firstFocus,
                onFocused = { focused = it },
                onLaunch = onLaunch,
            )
        }
    }

    LaunchedEffect(apps.isNotEmpty()) {
        if (apps.isNotEmpty()) {
            runCatching { firstFocus.requestFocus() }
        }
    }
}

@Composable
private fun Header() {
    val now by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(15_000)
        }
    }
    val hour = Calendar.getInstance().apply { timeInMillis = now }.get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 5 -> "Good night"
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        hour < 21 -> "Good evening"
        else -> "Good night"
    }
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))
    val date = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date(now))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = greeting, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = date, fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f))
        }
        Text(text = time, fontSize = 44.sp, fontWeight = FontWeight.Light, color = Color.White)
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White.copy(alpha = 0.85f),
        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun AppRow(
    items: List<AppItem>,
    tileWidth: Dp,
    firstPackage: String?,
    firstFocus: FocusRequester,
    onFocused: (AppItem) -> Unit,
    onLaunch: (AppItem) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        // Padding gives the scaled-up focused tile room so it isn't clipped.
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        items(items, key = { it.packageName }) { app ->
            AppTile(
                app = app,
                width = tileWidth,
                modifier = if (app.packageName == firstPackage) Modifier.focusRequester(firstFocus) else Modifier,
                onFocus = { onFocused(app) },
                onClick = { onLaunch(app) },
            )
        }
    }
}

@Composable
private fun AppTile(
    app: AppItem,
    width: Dp,
    modifier: Modifier,
    onFocus: () -> Unit,
    onClick: () -> Unit,
) {
    val height = width * 9 / 16
    val shape = RoundedCornerShape(18.dp)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            modifier = modifier
                .size(width, height)
                .onFocusChanged { if (it.hasFocus) onFocus() },
            shape = ClickableSurfaceDefaults.shape(shape),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color(0xFF1B2133),
                focusedContainerColor = Color(0xFF1B2133),
            ),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1.12f),
            border = ClickableSurfaceDefaults.border(
                focusedBorder = Border(border = BorderStroke(3.dp, Color.White), shape = shape),
            ),
        ) {
            Image(
                bitmap = app.image,
                contentDescription = app.label,
                contentScale = if (app.isBanner) ContentScale.Crop else ContentScale.Fit,
                modifier = if (app.isBanner) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier.fillMaxSize().padding(16.dp)
                },
            )
        }
        Text(
            text = app.label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(width)
                .padding(top = 12.dp),
        )
    }
}

private fun timeOfDayColors(): List<Color> {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..10 -> listOf(Color(0xFF1E3A5F), Color(0xFF0B1020))
        in 11..16 -> listOf(Color(0xFF1B3B4B), Color(0xFF0A1218))
        in 17..20 -> listOf(Color(0xFF3B2A4F), Color(0xFF0E0A18))
        else -> listOf(Color(0xFF10162B), Color(0xFF05070D))
    }
}
