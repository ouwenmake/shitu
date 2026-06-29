package com.shitu.pickpic.model

import android.net.Uri

enum class PhotoCategory {
    BEST, ALTERNATIVE, DUPLICATE, TRASH, UNKNOWN
}

data class PhotoItem(
    val id: Long,
    val uri: Uri,
    val score: Int = 0,
    val category: PhotoCategory = PhotoCategory.UNKNOWN,
    val aiLabels: List<String> = emptyList(),
    val aiReason: String = ""
)

data class PhotoGroup(
    val groupId: Int,
    val photos: List<PhotoItem>
)
