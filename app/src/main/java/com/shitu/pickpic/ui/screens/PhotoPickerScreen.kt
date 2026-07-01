package com.shitu.pickpic.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.shitu.pickpic.R
import com.shitu.pickpic.model.PhotoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerScreen(
    onStartAnalysis: (List<PhotoItem>) -> Unit,
    onBack: () -> Unit
) {
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }
    val gridState = rememberLazyGridState()
    val itemPositions = remember { mutableStateMapOf<Int, androidx.compose.ui.geometry.Rect>() }

    // Mock photos for demo with safety check
    val photos = remember {
        try {
            (1..100).map { id ->
                PhotoItem(id.toLong(), Uri.parse("https://picsum.photos/seed/${id}/400/400"))
            }
        } catch (e: Exception) {
            emptyList<PhotoItem>()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("选取照片") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            if (selectedIds.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = {
                            val selectedPhotos = photos.filter { it.id in selectedIds }
                            onStartAnalysis(selectedPhotos)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("已选 ${selectedIds.size} 张照片，开始 AI 智能精选")
                    }
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState,
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { },
                        onDrag = { change, dragAmount ->
                            val currentPos = change.position
                            itemPositions.forEach { (index, rect) ->
                                if (rect.contains(currentPos)) {
                                    photos.getOrNull(index)?.id?.let { id ->
                                        selectedIds = selectedIds + id
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            itemsIndexed(photos) { index, photo ->
                val isSelected = photo.id in selectedIds
                PhotoGridItem(
                    photo = photo,
                    isSelected = isSelected,
                    onToggle = {
                        selectedIds = if (isSelected) selectedIds - photo.id else selectedIds + photo.id
                    },
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            val rect = layoutCoordinates.positionInRoot().let { pos ->
                                androidx.compose.ui.geometry.Rect(
                                    pos.x, pos.y, 
                                    pos.x + layoutCoordinates.size.width, 
                                    pos.y + layoutCoordinates.size.height
                                )
                            }
                            itemPositions[index] = rect
                        }
                )
            }
        }
    }
}

@Composable
fun PhotoGridItem(
    photo: PhotoItem,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable { onToggle() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = photo.uri.takeIf { it.toString().isNotEmpty() } ?: Uri.EMPTY
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            )
        }
    }
}
