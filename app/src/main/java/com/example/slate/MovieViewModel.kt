package com.example.slate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MovieViewModel : ViewModel() {
    private val _selectedMovieTitle = MutableLiveData<String>()
    val selectedMovieTitle: LiveData<String> get() = _selectedMovieTitle

    private val _selectedMovieRank = MutableLiveData<Int>()
    val selectedMovieRank: LiveData<Int> get() = _selectedMovieRank

    fun setMovieInfo(title: String, rank: Int) {
        _selectedMovieTitle.value = title
        _selectedMovieRank.value = rank
    }
}