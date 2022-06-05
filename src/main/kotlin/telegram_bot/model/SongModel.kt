package telegram_bot.model

data class SongModel(
    val name: String,
    val href: String?,
    val artist: List<String>,
    val duration: Long
)