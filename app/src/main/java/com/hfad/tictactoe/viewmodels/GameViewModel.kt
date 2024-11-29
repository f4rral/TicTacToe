package com.hfad.tictactoe.viewmodels

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameCell(
    var symbol: String = "",
    var drawable: Drawable? = null,
    var isWin: Boolean = false
)

data class BestMove(
    var index: Int? = null,
    var score: Int? = null
)

class GameViewModel(
    private var pictureHuman: Drawable? = null,
    private var pictureAndroid: Drawable? = null,
) : ViewModel() {
    private var symbolHuman = "X"   // Игрок
    private var symbolAndroid = "O" // Android

    private var _countHuman: MutableLiveData<Int> = MutableLiveData(0)
    val countHuman: LiveData<Int>
        get() = _countHuman

    private var _countAndroid: MutableLiveData<Int> = MutableLiveData(0)
    val countAndroid: LiveData<Int>
        get() = _countAndroid

    private var _winPlayer: MutableLiveData<String?> = MutableLiveData()
    val winPlayer: LiveData<String?>
        get() = _winPlayer

    private var _isEndGame = MutableLiveData(false)
    val isEndGame: LiveData<Boolean>
        get() = _isEndGame

    private var _fieldGame: MutableLiveData<List<GameCell>> = MutableLiveData()
    val fieldGame: MutableLiveData<List<GameCell>>
        get() = _fieldGame

    private var activePlayer = symbolHuman

    init {
        _fieldGame.value = List(9) { GameCell() }
        Log.d("init fieldGame", "${_fieldGame.value?.joinToString()}")

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
        if (_isEndGame.value == true) {
            return
        }

        if (_fieldGame.value?.get(move)?.symbol != "") {
            return
        }

        val newFieldGame = _fieldGame.value!!
        newFieldGame[move].symbol = activePlayer
        newFieldGame[move].drawable = getPictureForSymbol(activePlayer)

        _fieldGame.value = newFieldGame

        Log.d("moveGame fieldGame", "${_fieldGame.value?.joinToString()}")

        val winner = checkWinner(newFieldGame)

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
                findBestMove(newFieldGame, activePlayer).index
            } else {
                findRandomMove(newFieldGame)
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
        activePlayer = symbolHuman
        _isEndGame.value = false

        _fieldGame.value = List(9) { GameCell() }

        startGame()
    }

    private fun checkWinner(fieldGame: List<GameCell>): String? {
        var winPlayer: String? = null

        // Победные комбинации
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
            val a = fieldGame[line[0]].symbol
            val b = fieldGame[line[1]].symbol
            val c = fieldGame[line[2]].symbol

            if (a != "" && a == b && a == c) {
                winPlayer = a
            }
        }

        return winPlayer
    }

    // Поиск лучшего хода
    private fun findBestMove(field: List<GameCell>, playerSymbol: String): BestMove {
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

            field[availSpots[i]].symbol = playerSymbol

            if (playerSymbol == symbolHuman) {
                move.score = findBestMove(field, symbolAndroid).score
            } else {
                move.score = findBestMove(field, symbolHuman).score
            }

            field[availSpots[i]].symbol = ""
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
    private fun findRandomMove(field: List<GameCell>): Int? {
        val emptyIndices = emptyIndices(field);

        if (emptyIndices.size == 0) {
            return null
        }

        return emptyIndices.random();
    }

    // Поиск пустых клеток
    private fun emptyIndices(field: List<GameCell>): ArrayList<Int> {
        val emptyCells: ArrayList<Int> = arrayListOf();

        field.forEachIndexed { index, cell ->
            if (cell.symbol === "") {
                emptyCells.add(index)
            }
        }

        return emptyCells
    }

    // Получение изображения
    private fun getPictureForSymbol(symbol: String): Drawable? {
        if (symbol == symbolHuman) {
            return pictureAndroid
        }

        if (symbol == symbolAndroid) {
            return pictureHuman
        }

        return null
    }
}
