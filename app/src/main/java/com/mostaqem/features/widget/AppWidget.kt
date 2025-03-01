package com.mostaqem.features.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.mostaqem.core.media.service.MediaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppWidget : GlanceAppWidget() {
    companion object {
        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
        private val BIG_SQUARE = DpSize(250.dp, 250.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            SMALL_SQUARE,
            HORIZONTAL_RECTANGLE,
            BIG_SQUARE
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val sessionToken = SessionToken(context, ComponentName(context, MediaService::class.java))
        val mediaController = withContext(Dispatchers.IO) {
            MediaController.Builder(context, sessionToken).buildAsync().get()
        }

        provideContent {
            // create your AppWidget here
            MyContent(mediaController)
        }
    }

    @Composable
    private fun MyContent(mediaController: MediaController) {
        Column(
            modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.primaryContainer),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(mediaController.currentMediaItem?.mediaMetadata.toString())

        }
    }
}

@Preview
@Composable
private fun PreviewC() {
    Column(
        modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.primaryContainer),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("abc")

    }
}