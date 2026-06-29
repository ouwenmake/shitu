package com.shitu.pickpic.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.shitu.pickpic.R
import com.shitu.pickpic.model.PhotoCategory
import com.shitu.pickpic.model.PhotoItem
import com.shitu.pickpic.ui.theme.AmberGold
import com.shitu.pickpic.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val results by viewModel.analyzedResults.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val progress by viewModel.analysisProgress.collectAsState()
    var selectedTab by remember { mutableStateOf(PhotoCategory.BEST) }
    var selectedPhotoForDetail by remember { mutableStateOf<PhotoItem?>(null) }

    if (isAnalyzing) {
        AnalysisAnimationScreen(progress)
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = {
                if (selectedTab == PhotoCategory.TRASH && (results[PhotoCategory.TRASH]?.isNotEmpty() == true)) {
                    Button(
                        onClick = { viewModel.cleanupTrash() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(id = R.string.one_click_cleanup))
                    }
                } else if (selectedTab == PhotoCategory.BEST) {
                    Button(
                        onClick = { /* Export Logic */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(id = R.string.export_best))
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                ScrollableTabRow(
                    selectedTabIndex = PhotoCategory.values().indexOf(selectedTab),
                    containerColor = Color.Transparent,
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    PhotoCategory.values().filter { it != PhotoCategory.UNKNOWN }.forEach { category ->
                        Tab(
                            selected = selectedTab == category,
                            onClick = { selectedTab = category },
                            text = {
                                Text(
                                    text = when (category) {
                                        PhotoCategory.BEST -> stringResource(id = R.string.best_photos)
                                        PhotoCategory.ALTERNATIVE -> stringResource(id = R.string.alternative_photos)
                                        PhotoCategory.DUPLICATE -> stringResource(id = R.string.duplicate_photos)
                                        PhotoCategory.TRASH -> stringResource(id = R.string.trash_photos)
                                        else -> ""
                                    } + " (${results[category]?.size ?: 0})"
                                )
                            }
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(results[selectedTab] ?: emptyList()) { photo ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clickable { selectedPhotoForDetail = photo }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(photo.uri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (selectedTab == PhotoCategory.BEST) {
                                Surface(
                                    color = AmberGold,
                                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        "Best",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            selectedPhotoForDetail?.let { photo ->
                AestheticReportDialog(photo = photo, onDismiss = { selectedPhotoForDetail = null })
            }
        }
    }
}

@Composable
fun AnalysisAnimationScreen(progress: Float) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            stringResource(id = R.string.analyzing),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        val statusText = when {
            progress < 0.3f -> "正在提取特征点..."
            progress < 0.6f -> "构图美学深度分析中..."
            else -> "正在排除冗余重复照片..."
        }
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AestheticReportDialog(photo: PhotoItem, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.report_title), style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.score_label) + ": ${photo.score}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGold
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                photo.aiLabels.forEach { label ->
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AI 分析结果：",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = photo.aiReason,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FlowRow(
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    // Simplified FlowRow implementation
    androidx.compose.ui.layout.Layout(content = content) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        var rowWidth = 0
        var rowHeight = 0
        var totalHeight = 0
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()

        placeables.forEach { placeable ->
            if (rowWidth + placeable.width + mainAxisSpacing.roundToPx() > constraints.maxWidth) {
                rows.add(currentRow)
                totalHeight += rowHeight + crossAxisSpacing.roundToPx()
                rowWidth = 0
                rowHeight = 0
                currentRow = mutableListOf()
            }
            currentRow.add(placeable)
            rowWidth += placeable.width + mainAxisSpacing.roundToPx()
            rowHeight = maxOf(rowHeight, placeable.height)
        }
        rows.add(currentRow)
        totalHeight += rowHeight

        layout(constraints.maxWidth, totalHeight) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                var maxHeight = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + mainAxisSpacing.roundToPx()
                    maxHeight = maxOf(maxHeight, placeable.height)
                }
                y += maxHeight + crossAxisSpacing.roundToPx()
            }
        }
    }
}
