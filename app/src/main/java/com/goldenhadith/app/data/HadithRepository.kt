package com.goldenhadith.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HadithRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("golden_hadith_prefs", Context.MODE_PRIVATE)
    private val bookmarksKey = "bookmarked_ids"

    fun loadHadiths(): List<Hadith> {
        return try {
            val json = context.assets.open("hadiths.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Hadith>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getBookmarkedIds(): Set<Int> {
        val raw = prefs.getStringSet(bookmarksKey, emptySet()) ?: emptySet()
        return raw.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun toggleBookmark(hadithId: Int): Set<Int> {
        val current = getBookmarkedIds().toMutableSet()
        if (current.contains(hadithId)) {
            current.remove(hadithId)
        } else {
            current.add(hadithId)
        }
        prefs.edit()
            .putStringSet(bookmarksKey, current.map { it.toString() }.toSet())
            .apply()
        return current
    }
}
