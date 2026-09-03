package com.banglacodes.aitranslatorkeyboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder

object TranslationHelper {
    private val client = OkHttpClient()

    suspend fun translate(text: String, sourceLang: String, targetLang: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val encodedText = URLEncoder.encode(text, "UTF-8")
                val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$sourceLang&tl=$targetLang&dt=t&q=$encodedText"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val jsonStr = response.body?.string() ?: return@withContext text
                
                val jsonArray = JSONArray(jsonStr)
                val sentencesArray = jsonArray.getJSONArray(0)
                val sb = StringBuilder()
                for (i in 0 until sentencesArray.length()) {
                    val sentence = sentencesArray.getJSONArray(i)
                    sb.append(sentence.getString(0))
                }
                sb.toString()
            } catch (e: Exception) {
                text
            }
        }
    }
}
