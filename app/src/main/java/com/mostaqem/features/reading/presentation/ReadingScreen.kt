package com.mostaqem.features.reading.presentation

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.navigation.models.ShareDestination
import com.mostaqem.core.ui.theme.uthmaniFont
import com.mostaqem.dataStore
import com.mostaqem.features.reading.data.models.Verse
import com.mostaqem.features.offline.domain.toArabicNumbers
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.share.ShareViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    chapterNumber: Int,
    chapterName: String,
    readingViewModel: ReadingViewModel = hiltViewModel(),
    sharingViewModel: ShareViewModel,
    navController: NavController,
) {
    val versesPerPage = 10

    val verses = remember(chapterNumber) {
        readingViewModel.getVerses(chapterNumber)
    }
    val showBottomSheet = remember { mutableStateOf(false) }

    val totalPages = (verses.size + versesPerPage - 1) / versesPerPage
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { totalPages })
    var selectedVerses by remember { mutableStateOf<Set<Verse>>(emptySet()) }
    var enableSelection by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code


    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == totalPages - 1) {
            readingViewModel.preloadVerses(chapterNumber + 1)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.Start
    ) {

        Crossfade(
            targetState = selectedVerses.isNotEmpty() || enableSelection,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow

            )
        ) { isSelectionMode ->
            if (isSelectionMode) {
                val count: String =
                    if (languageCode == "ar") selectedVerses.size.toArabicNumbers() else selectedVerses.size.toString()
                TopAppBar(
                    title = { Text("$count ${stringResource(R.string.selectedd)} ") },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (enableSelection) enableSelection = false
                            selectedVerses = emptySet()

                        }) {
                            Icon(Icons.Default.Close, "close")
                        }
                    },
                    actions = {
                        if (selectedVerses.isNotEmpty()) {
                            IconButton(onClick = {
                                sharingViewModel.updateVerses(selectedVerses)
                                navController.navigate(ShareDestination(chapterName = chapterName))
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "share")
                            }
                        }

                    }
                )
            } else {
                CenterAlignedTopAppBar(
                    title = { Text(chapterName, fontFamily = uthmaniFont) },

                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showBottomSheet.value = true
                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "more")
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (showBottomSheet.value) {
            ModalBottomSheet(onDismissRequest = {
                showBottomSheet.value = false

            }) {

                ListItem(
                    headlineContent = {
                        Text(stringResource(R.string.share_verse))
                    },
                    leadingContent = {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    modifier = Modifier.clickable {
                        enableSelection = true
                        showBottomSheet.value = false
                    }
                )

            }
        }
        var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
        HorizontalPager(
            state = pagerState,
            pageSpacing = 10.dp,
            reverseLayout = languageCode == "en",
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val pageVerses = verses.chunked(versesPerPage)[page]

            Box(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (page == 0) {
                        Image(
                            painter = painterResource(if (chapterNumber != 9) R.drawable.basmallah else R.drawable.a3ooz),
                            contentDescription = "basmallah",
                            modifier = Modifier.width(250.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        )
                    }

                    val annotatedString = buildAnnotatedString {
                        pageVerses.forEach { verse ->
                            val start = length
                            val isSelected = selectedVerses.contains(verse)
                            withStyle(
                                style = SpanStyle(
                                    background = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified
                                )
                            ) {
                                append(verse.text)
                            }

                            withStyle(
                                style = SpanStyle(
                                    background = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            ) {
                                append(" ${displayVerseNumber(verse.verse)} ")
                            }

                            val end = length
                            addStringAnnotation(
                                tag = verse.verse.toArabicNumbers(),
                                annotation = verse.text,
                                start = start,
                                end = end
                            )
                        }
                    }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

                        Text(
                            text = annotatedString,
                            textAlign = TextAlign.Justify,
                            lineHeight = 45.sp,
                            fontFamily = uthmaniFont,
                            onTextLayout = { layoutResult = it },
                            fontSize = 30.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { offset ->
                                            layoutResult?.let { layout ->
                                                val position = layout.getOffsetForPosition(offset)
                                                val clickedAnnotation = annotatedString
                                                    .getStringAnnotations(position, position)
                                                    .firstOrNull()

                                                clickedAnnotation?.let { annotation ->
                                                    // Find the corresponding verse from pageVerses
                                                    val clickedVerse = pageVerses.find {
                                                        it.verse.toArabicNumbers() == annotation.tag
                                                    }

                                                    clickedVerse?.let { verse ->
                                                        when {
                                                            // If verse is already selected, deselect it
                                                            selectedVerses.contains(verse) -> {
                                                                selectedVerses -= verse
                                                            }
                                                            // If we haven't reached max selection limit
                                                            selectedVerses.size < 7 -> {
                                                                if (selectedVerses.isEmpty()) {
                                                                    // First selection
                                                                    selectedVerses += verse
                                                                } else {
                                                                    // Find the range
                                                                    val minSelectedVerse =
                                                                        selectedVerses.minOf { it.verse }
                                                                    val maxSelectedVerse =
                                                                        selectedVerses.maxOf { it.verse }

                                                                    when {
                                                                        // Clicking before the current selection
                                                                        verse.verse < minSelectedVerse -> {
                                                                            val versesToAdd =
                                                                                pageVerses
                                                                                    .filter {
                                                                                        it.verse in verse.verse..minSelectedVerse &&
                                                                                                !selectedVerses.contains(
                                                                                                    it
                                                                                                )
                                                                                    }
                                                                                    .take(7 - selectedVerses.size)
                                                                            selectedVerses += versesToAdd
                                                                        }
                                                                        // Clicking after the current selection
                                                                        verse.verse > maxSelectedVerse -> {
                                                                            val versesToAdd =
                                                                                pageVerses
                                                                                    .filter {
                                                                                        it.verse in maxSelectedVerse..verse.verse &&
                                                                                                !selectedVerses.contains(
                                                                                                    it
                                                                                                )
                                                                                    }
                                                                                    .take(7 - selectedVerses.size)
                                                                            selectedVerses += versesToAdd
                                                                        }

                                                                        else -> {
                                                                            selectedVerses += verse
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }, onLongPress = { offset ->
                                            layoutResult?.let { layout ->
                                                val position = layout.getOffsetForPosition(offset)
                                                annotatedString
                                                    .getStringAnnotations(position, position)
                                                    .firstOrNull()
                                                    ?.let { annotation ->
                                                        selectedVerses += Verse(
                                                            text = annotation.item,
                                                            verse = annotation.tag.toInt(),
                                                            chapter = chapterNumber
                                                        )

                                                    }
                                            }
                                        }
                                    )
                                }
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (pagerState.currentPage == 0) Arrangement.End  else Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),


                            ) {
                            AnimatedVisibility(
                                visible = pagerState.currentPage != 0,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)


                            ) {
                                IconButton(

                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                pagerState.currentPage - 1,
                                                animationSpec = tween(300)
                                            )
                                        }

                                    }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "back",

                                        )
                                }

                            }
                            AnimatedVisibility(
                                visible = pagerState.currentPage != pagerState.pageCount - 1,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)

                            ) {
                                IconButton(

                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                pagerState.currentPage + 1,
                                                animationSpec = tween(300)
                                            )
                                        }

                                    }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "forward",

                                        )
                                }


                            }
                        }


                    }
                }
            }
        }
    }
}


fun displayVerseNumber(verse: Int): String {
    val currentAPI = Build.VERSION.SDK_INT
    if (currentAPI < 33) {
        return verse.toArabicNumbers().reversed()
    }
    return verse.toArabicNumbers()

}