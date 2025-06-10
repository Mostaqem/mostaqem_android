package com.mostaqem.features.widget

import android.content.ComponentName
import android.content.Context
import android.provider.MediaStore.Audio.Media
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.IconImageProvider
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.mostaqem.R
import com.mostaqem.core.media.service.MediaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppWidget : GlanceAppWidget() {


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val sessionToken = SessionToken(context, ComponentName(context, MediaService::class.java))
        val mediaController = withContext(Dispatchers.IO) {
            MediaController.Builder(context, sessionToken).buildAsync().get()
        }

        provideContent {
            MyContent(mediaController)
        }
    }

    @Composable
    private fun MyContent(mediaController: MediaController) {
        val metadata = mediaController.mediaMetadata
        Box(
            modifier = GlanceModifier.height(150.dp).width(280.dp)
                .background(GlanceTheme.colors.primaryContainer),
            contentAlignment = androidx.glance.layout.Alignment.TopEnd
        ) {
            Column() {
                Row(modifier = GlanceModifier.padding(20.dp)) {
                    Column(
                        verticalAlignment = androidx.glance.layout.Alignment.Vertical.Bottom,
                        modifier = GlanceModifier.fillMaxHeight().padding(bottom = 40.dp),
                        horizontalAlignment = androidx.glance.layout.Alignment.Horizontal.End
                    ) {
                        Text(metadata.title.toString(), style = TextStyle(fontSize = 18.sp))
                        Text(metadata.artist.toString())

                    }
                    Spacer(modifier = GlanceModifier.width(20.dp))
                    Box(
                        modifier = GlanceModifier.background(GlanceTheme.colors.onPrimaryContainer)
                            .size(80.dp)

                    ) {}
                    Image(
                        provider = ImageProvider(R.drawable.outline_play_arrow_24),
                        contentDescription = "play",
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
                        modifier = GlanceModifier.clickable(onClick = actionRunCallback<PlayActionButton>())

                    )
                }


            }

        }
    }
}


object PlayActionButton : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val sessionToken = SessionToken(context, ComponentName(context, MediaService::class.java))
        val mediaController = withContext(Dispatchers.IO) {
            MediaController.Builder(context, sessionToken).buildAsync().get()
        }
        val mediaFuture = MediaController.Builder(context, sessionToken).buildAsync()

        mediaFuture.addListener(
            {
                if (mediaController.playbackState != Player.STATE_READY) {
                    mediaController.prepare()
                }
                if (mediaController.isPlaying) {
                    mediaController.pause()
                } else {
                    mediaController.play()
                }
                mediaController.release()
            },
            ContextCompat.getMainExecutor(context)
        )


    }

}