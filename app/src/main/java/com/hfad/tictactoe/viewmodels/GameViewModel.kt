package com.hfad.tictactoe.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private var _field: MutableLiveData<Array<String>> = MutableLiveData()
    val field: LiveData<Array<String>>
        get() = _field

    private var _winPlayer: MutableLiveData<String?> = MutableLiveData()
    val winPlayer: LiveData<String?>
        get() = _winPlayer

    private var _isEndGame = MutableLiveData(false)
    val isEndGame: LiveData<Boolean>
        get() = _isEndGame

    var activePlayer = "X"

    init {
        _field.value = Array(9) {""}
    }

    // Игровой ход
    fun move(i: Int) {
        Log.d("GameViewModel move", i.toString())

        if (_isEndGame.value == true) {
            return
        }

        if (_field.value?.get(i) != "") {
            return
        }

        val newFiled = _field.value!!.copyOf()
        newFiled[i] = activePlayer
        _field.value = newFiled

        checkWinner(newFiled)

        activePlayer = if (activePlayer === "X") "O" else "X"
    }

    // Новая игра
    fun newGame() {
        _field.value = Array(9) {""}
        _isEndGame.value = false
    }

    // Определение победителя
    private fun checkWinner(field: Array<String>) {
        // Победные линии
        val winLines = arrayOf(
            arrayOf(0, 1, 2),
            arrayOf(3, 4, 5),
            arrayOf(6, 7, 8),
            arrayOf(0, 3, 6),
            arrayOf(1, 4, 7),
            arrayOf(2, 5, 8),
            arrayOf(0, 4, 8),
            arrayOf(2, 4, 6),
        )

        for (line in winLines) {
            val a = _field.value?.get(line[0])
            val b = _field.value?.get(line[1])
            val c = _field.value?.get(line[2])

            if (a != "" && a == b && a == c) {
                Log.d("checkWinner", line.joinToString())
                _winPlayer.value = a
                _isEndGame.value = true
            }
        }
    }

    // Поиск пустых клеток
    fun emptyIndices() {

    }

    // Поиск лучшего хода
    fun findBestMove() {

    }
}