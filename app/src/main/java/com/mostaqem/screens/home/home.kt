package com.mostaqem.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.screens.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
                .verticalScroll(scrollState)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            )
            {
                AsyncImage(
                    model = "https://img.freepik.com/premium-vector/vector-male-muslim-character-with-flat-design-style-that-is-simple-minimalist_995281-2613.jpg",
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier

                        .clip(CircleShape)

                        .size(50.dp)
                        .align(Alignment.CenterVertically)

                )

                SearchBar(query = "",
                    onQueryChange = {},
                    onSearch = {},
                    modifier = Modifier.fillMaxWidth(),
                    active = false,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_mic_none_24),
                            contentDescription = "microphone"
                        )
                    },
                    onActiveChange = {}) {

                }


            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "قرئ مؤخرا", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)

            ) {
                items(10) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(45.dp)
                                .height(70.dp)
                                .width(40.dp)

                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column {
                            Text(
                                text = "سورة البقرة",
                                fontWeight = FontWeight.W600,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "علي جابر")

                        }
                    }

                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "شيوخ مختارة", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)

            ) {
                items(5) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(45.dp)
                                .height(25.dp)

                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "عبدالباسط عبدالصمد",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(100.dp)


                        )
                    }

                }
            }

        }
        Box(
            modifier = Modifier

                .background(MaterialTheme.colorScheme.primaryContainer)
                .height(100.dp)

                .fillMaxWidth()


        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = "https://static.vecteezy.com/system/resources/previews/000/364/513/original/vector-al-quran-illustration.jpg",
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(65.dp)


                            .clip(RoundedCornerShape(20.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "سورة البقرة", fontWeight = FontWeight.W800, fontSize = 19.sp)
                        Text(text = "عبدالباسط عبدالصمد", fontSize = 14.sp)

                    }
                }

                Row {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)

                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "play",
                            tint = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                    IconButton(
                        onClick = { /*TODO*/ },


                        ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_skip_previous_24),
                            contentDescription = "play",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                }

            }
        }
    }


}

