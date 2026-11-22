package com.goldenhadith.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.goldenhadith.app.data.Hadith
import com.goldenhadith.app.data.HadithRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope
import java.util.Calendar

class HadithViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HadithRepository(application.applicationContext)

    private val _allHadiths = MutableStateFlow<List<Hadith>>(emptyList())
    val allHadiths: StateFlow<List<Hadith>> = _allHadiths.asStateFlow()

    private val _bookmarkedIds = MutableStateFlow<Set<Int>>(emptySet())
    val bookmarkedIds: StateFlow<Set<Int>> = _bookmarkedIds.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _fontScale = MutableStateFlow(1.0f)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    val categories: StateFlow<List<String>> = _allHadiths
        .combine(_selectedCategory) { hadiths, _ ->
            listOf("All") + hadiths.map { it.category }.distinct().sorted()
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf("All"))

    val filteredHadiths: StateFlow<List<Hadith>> = combine(
        _allHadiths,
        _selectedCategory,
        _searchQuery
    ) { hadiths, category, query ->
        hadiths.filter { hadith ->
            val matchesCategory = category == "All" || hadith.category == category
            val q = query.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                hadith.english.lowercase().contains(q) ||
                hadith.narrator.lowercase().contains(q) ||
                hadith.source.lowercase().contains(q) ||
                hadith.category.lowercase().contains(q) ||
                hadith.number.toString() == q
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val bookmarkedHadiths: StateFlow<List<Hadith>> = combine(
        _allHadiths,
        _bookmarkedIds
    ) { hadiths, ids ->
        hadiths.filter { ids.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        _allHadiths.value = repository.loadHadiths()
        _bookmarkedIds.value = repository.getBookmarkedIds()
    }

    fun getDailyHadith(): Hadith? {
        val list = _allHadiths.value
        if (list.isEmpty()) return null
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return list[dayOfYear % list.size]
    }

    fun getHadithById(id: Int): Hadith? {
        return _allHadiths.value.find { it.id == id }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleBookmark(hadithId: Int) {
        _bookmarkedIds.value = repository.toggleBookmark(hadithId)
    }

    fun increaseFontScale() {
        _fontScale.value = (_fontScale.value + 0.1f).coerceAtMost(1.5f)
    }

    fun decreaseFontScale() {
        _fontScale.value = (_fontScale.value - 0.1f).coerceAtLeast(0.8f)
    }
}
