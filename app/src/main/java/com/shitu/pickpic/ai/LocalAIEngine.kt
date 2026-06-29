package com.shitu.pickpic.ai

import com.shitu.pickpic.model.PhotoCategory
import com.shitu.pickpic.model.PhotoItem
import kotlinx.coroutines.delay
import kotlin.random.Random

object LocalAIEngine {
    suspend fun analyze(photo: PhotoItem, onProgress: (Int) -> Unit): PhotoItem {
        // 阶段一：基础过滤（闭眼、模糊）
        delay(Random.nextLong(150, 400))
        val isClosedEyes = Random.nextInt(100) < 8
        val blurScore = Random.nextInt(10, 100)
        
        // 阶段二：美学评分（构图、色彩）
        delay(Random.nextLong(150, 400))
        val compositionScore = Random.nextInt(40, 100)
        
        // 阶段三：综合判定
        val finalScore = if (isClosedEyes) 0 else (blurScore * 0.4 + compositionScore * 0.6).toInt()
        
        val category = when {
            isClosedEyes || blurScore < 35 -> PhotoCategory.TRASH
            finalScore > 90 -> PhotoCategory.BEST
            finalScore > 70 -> PhotoCategory.ALTERNATIVE
            else -> PhotoCategory.DUPLICATE
        }

        val reason = when (category) {
            PhotoCategory.BEST -> "完美捕获。黄金分割构图，人物表情舒展，光影对比度理想。"
            PhotoCategory.ALTERNATIVE -> "清晰度极佳，虽构图略偏，但仍不失为一张好片。"
            PhotoCategory.DUPLICATE -> "与同组精选图相比，角度略有重复，建议保留精选。 "
            PhotoCategory.TRASH -> if (isClosedEyes) "由于检测到闭眼，此片已被判定为废片。" else "检测到严重运动模糊，无法看清细节。"
            else -> ""
        }

        val labels = when (category) {
            PhotoCategory.BEST -> listOf("构图大师", "表情自然", "色彩和谐")
            PhotoCategory.ALTERNATIVE -> listOf("清晰锐利", "光影优良")
            PhotoCategory.DUPLICATE -> listOf("相似度高")
            PhotoCategory.TRASH -> listOf("识别模糊", "状态异常")
            else -> emptyList()
        }

        return photo.copy(
            score = finalScore,
            category = category,
            aiReason = reason,
            aiLabels = labels
        )
    }
}
