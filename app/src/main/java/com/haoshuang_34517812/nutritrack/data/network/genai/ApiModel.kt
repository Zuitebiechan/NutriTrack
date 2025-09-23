package com.haoshuang_34517812.nutritrack.data.network.genai

data class GeminiRequest(
    val contents: List<Content>,
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GeminiResponse(
    val candidates: List<Candidate>?,
    val promptFeedback: PromptFeedback?
)

data class Candidate(
    val content: Content,
    val finishReason: String?
)

data class PromptFeedback(
    val safetyRatings: List<SafetyRating>?
)

data class SafetyRating(
    val category: String?,
    val probability: String?
)
