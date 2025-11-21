package com.haoshuang_34517812.nutritrack.data.network.genai

// --- OpenAI / DeepSeek Compatible Models ---

data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    val stream: Boolean = false,
    val thinking: Thinking? = null
)

data class Thinking(
    val type: String = "enabled"
)

data class OpenAIMessage(
    val role: String,
    val content: Any // Can be String or List<ContentPart>
)

data class ContentPart(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

data class ImageUrl(
    val url: String
)

data class OpenAIResponse(
    val choices: List<OpenAIChoice>?
)

data class OpenAIChoice(
    val message: OpenAIMessageContent
)

data class OpenAIMessageContent(
    val role: String,
    val content: String
)

// --- Legacy Gemini Models (kept for reference or fallback) ---

data class GeminiRequest(
    val contents: List<Content>,
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

data class InlineData(
    val mimeType: String,
    val data: String
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
