package com.cleanyourtv.launcher

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class AppItem(
    val label: String,
    val packageName: String,
    val image: ImageBitmap,
    val isBanner: Boolean,
)

object AppRepository {

    /** Apps shown in the big "Favourites" row, in this order (only if installed). */
    val pinnedOrder = listOf(
        "com.netflix.ninja",
        "com.google.android.youtube.tv",
        "com.amazon.amazonvideo.livingroom",
        "in.startv.hotstar",
    )

    fun loadApps(context: Context): List<AppItem> {
        val pm = context.packageManager

        val leanback = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val normal = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        // Leanback entries first, so apps that have both keep their TV banner.
        val all = pm.queryIntentActivities(leanback, 0) + pm.queryIntentActivities(normal, 0)

        return all
            .filter { it.activityInfo.packageName != context.packageName }
            .distinctBy { it.activityInfo.packageName }
            .mapNotNull { info ->
                runCatching {
                    val banner = info.activityInfo.loadBanner(pm)
                        ?: info.activityInfo.applicationInfo.loadBanner(pm)
                    val drawable = banner ?: info.loadIcon(pm)
                    val isBanner = banner != null
                    val bitmap = if (isBanner) drawable.toBitmap(320, 180) else drawable.toBitmap(192, 192)
                    AppItem(
                        label = info.loadLabel(pm).toString(),
                        packageName = info.activityInfo.packageName,
                        image = bitmap.asImageBitmap(),
                        isBanner = isBanner,
                    )
                }.getOrNull()
            }
            .sortedBy { it.label.lowercase() }
    }

    fun launch(context: Context, packageName: String) {
        val pm = context.packageManager
        val intent = pm.getLeanbackLaunchIntentForPackage(packageName)
            ?: pm.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}
