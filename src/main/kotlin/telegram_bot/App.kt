package telegram_bot

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.sun.deploy.net.HttpRequest
import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.utils.toJson
import io.ktor.client.request.*
import kotlinx.coroutines.*
import telegram_bot.model.SongModel
import java.net.URI

const val botId: String = "5396849544:AAFjSoBzBZybmTQiUX-r416F2I8pkmOlpN0"

/**
 * This method by default expects one argument in [args] field: telegram bot token
 */
suspend fun main(args: Array<String>) {
    // that is your bot
    val bot = telegramBot(botId)

    // that is kotlin coroutine scope which will be used in requests and parallel works under the hood
    val scope = CoroutineScope(Dispatchers.Default)

    // here should be main logic of your bot
    bot.buildBehaviourWithLongPolling(scope) {
        // in this lambda you will be able to call methods without "bot." prefix
        val me = getMe()

        // this method will create point to react on each /start command
        onCommand("song", requireOnlyCommandInMessage = true) {
            reply(
                it, "Just a sec..."
            )
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://localhost:8080/song")
                .build()
            val response = client.newCall(request).execute()

            val song = Gson().fromJson(response.body().string(), SongModel::class.java)

            reply(
                it, "Listen to ${song.name} by ${song.artist}\n" +
                        "${song.href}"
            )
        }

        // That will be called on the end of bot initiation. After that prinln will be started long polling and bot will
        // react on your commands
        println(me)
    }.join()
}
