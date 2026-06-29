package com.shitu.pickpic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shitu.pickpic.model.PhotoCategory
import com.shitu.pickpic.model.PhotoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val _selectedPhotos = MutableStateFlow<List<PhotoItem>>(emptyList())
    val selectedPhotos: StateFlow<List<PhotoItem>> = _selectedPhotos

    private val _analyzedResults = MutableStateFlow<Map<PhotoCategory, List<PhotoItem>>>(emptyMap())
    val analyzedResults: StateFlow<Map<PhotoCategory, List<PhotoItem>>> = _analyzedResults

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing

    private val _analysisProgress = MutableStateFlow(0f)
    val analysisProgress: StateFlow<Float> = _analysisProgress

    fun analyzePhotos(photos: List<PhotoItem>) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysisProgress.value = 0f
            
            val total = photos.size
            val analyzed = mutableListOf<PhotoItem>()

            photos.forEachIndexed { index, photo ->
                val result = com.shitu.pickpic.ai.LocalAIEngine.analyze(photo) { /* individual progress */ }
                analyzed.add(result)
                _analysisProgress.value = (index + 1).toFloat() / total
            }

            val grouped = analyzed.groupBy { it.category }
            _analyzedResults.value = grouped
            _isAnalyzing.value = false
        }
    }

    fun cleanupTrash() {
        // Logic to delete trash photos
        val currentResults = _analyzedResults.value.toMutableMap()
        currentResults[PhotoCategory.TRASH] = emptyList()
        _analyzedResults.value = currentResults
    }
}
