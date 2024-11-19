package com.hfad.tictactoe.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class BestMove(
    var index: Int? = null,
    var score: Int? = null
)

class GameViewModel : ViewModel() {
    private var symbolHuman = "X"   // Игрок
    private var symbolAndroid = "O" // Android

    private var _countHuman: MutableLiveData<Int> = MutableLiveData(0)
    val countHuman: LiveData<Int>
        get() = _countHuman

    private var _countAndroid: MutableLiveData<Int> = MutableLiveData(0)
    val countAndroid: LiveData<Int>
        get() = _countAndroid

    private var _field: MutableLiveData<Array<String>> = MutableLiveData()
    val field: LiveData<Array<String>>
        get() = _field

    private var _winPlayer: MutableLiveData<String?> = MutableLiveData()
    val winPlayer: LiveData<String?>
        get() = _winPlayer

    private var _isEndGame = MutableLiveData(false)
    val isEndGame: LiveData<Boolean>
        get() = _isEndGame

    private var activePlayer = symbolHuman

    init {
        _field.value = Array(9) {""}
//        _field.value = arrayOf(
//            "O", "X", "O",
//            "X", "", "",
//            "O", "", ""
//        )
//        _field.value = arrayOf(
//            "O", "", "",
//            "", "", "",
//            "", "", ""
//        )

        startGame()
    }

    private fun startGame() {
        activePlayer = if (Random.nextBoolean()) symbolHuman else symbolAndroid

        if (activePlayer == symbolAndroid) {
            moveGame((0..8).random())
        }
    }

    // Игровой ход
    fun moveGame(move: Int) {
        Log.d("GameViewModel move", move.toString())

        if (_isEndGame.value == true) {
            return
        }

        if (_field.value?.get(move) != "") {
            return
        }

        val newFiled = _field.value!!.copyOf()
        newFiled[move] = activePlayer
        _field.value = newFiled

        val winner = checkWinner(newFiled)

        if (winner != null) {
            _winPlayer.value = activePlayer
            _isEndGame.value = true

            if (activePlayer == symbolHuman) {
                _countHuman.value = _countHuman.value?.plus(1)
            }

            if (activePlayer == symbolAndroid) {
                _countAndroid.value = _countAndroid.value?.plus(1)
            }
        }

        if (activePlayer === symbolHuman) {
            activePlayer = symbolAndroid

            val bestMove = if ((0..9).random() < 8) {
                Log.d("moveGame", "findBestMove")
                findBestMove(newFiled, activePlayer).index
            } else {
                Log.d("moveGame", "findRandomMove")
                findRandomMove(newFiled)
            }

            if (bestMove != null) {
                moveGame(bestMove)
            }

            Log.d("moveGame", "${bestMove}, ${bestMove}")
        } else {
            activePlayer = symbolHuman
        }
    }

    // Новая игра
    fun newGame() {
        _field.value = Array(9) {""}
        activePlayer = symbolHuman
        _isEndGame.value = false

        startGame()
    }

    // Определение победителя
    private fun checkWinner(field: Array<String>): String? {
        var winPlayer: String? = null

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
            val a = field[line[0]]
            val b = field[line[1]]
            val c = field[line[2]]

            if (a != "" && a == b && a == c) {
                winPlayer = a
            }
        }

        return winPlayer
    }

    // Поиск лучшего хода
    private fun findBestMove(field: Array<String>, playerSymbol: String): BestMove {
        val availSpots = emptyIndices(field);
        val winner = checkWinner(field);

        if (winner == symbolAndroid) {
            return BestMove(score = -1)
        }

        if (winner == symbolHuman) {
            return BestMove(score = 1)
        }

        if (availSpots.size == 0) {
            return BestMove(score = 0)
        }

        val moves: ArrayList<BestMove> = ArrayList()

        for (i in 0..<availSpots.size) {
            val move = BestMove()
            move.index = availSpots[i]

            field[availSpots[i]] = playerSymbol

            if (playerSymbol == symbolHuman) {
                move.score = findBestMove(field, symbolAndroid).score
            } else {
                move.score = findBestMove(field, symbolHuman).score
            }

            field[availSpots[i]] = ""
            moves.add(move);
        }

        var bestMove = 0;
        // если это ход ИИ, пройти циклом по ходам и выбрать ход с наибольшим количеством очков
        if (playerSymbol == symbolHuman) {
            var bestScore = -10000

            for (i in 0..<moves.size) {
                if (moves[i].score!! > bestScore) {
                    bestScore = moves[i].score!!
                    bestMove = i
                }
            }
        // иначе пройти циклом по ходам и выбрать ход с наименьшим количеством очков
        } else {
            var bestScore = 10000;
            for (i in 0..<moves.size) {
                if (moves[i].score!! < bestScore) {
                    bestScore = moves[i].score!!
                    bestMove = i
                }
            }
        }

        return moves[bestMove]
    }

    // Поиск случайного хода
    private fun findRandomMove(field: Array<String>): Int? {
        Log.d("findRandomMove", field.joinToString())
        Log.d("findRandomMove", emptyIndices(field).size.toString())

        emptyIndices(field)

        if (emptyIndices(field).size == 0) {
            return null
        }

        return emptyIndices(field).random();
    }

    // Поиск пустых клеток
    private fun emptyIndices(field: Array<String>): ArrayList<Int> {
        val emptyCells: ArrayList<Int> = arrayListOf();

        field.forEachIndexed { index, symbol ->
            if (symbol === "") {
                emptyCells.add(index)
            }
        }

        return emptyCells
    }
}


