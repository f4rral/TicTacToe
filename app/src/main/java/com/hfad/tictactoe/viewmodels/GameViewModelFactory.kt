package com.hfad.tictactoe.viewmodels

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(
    private var pictureHuman: Drawable? = null,
    private var pictureAndroid: Drawable? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(pictureHuman, pictureAndroid) as T
    }
}