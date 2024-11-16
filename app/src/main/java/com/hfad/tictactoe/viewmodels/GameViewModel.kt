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

    val user = Player(symbol = "X")
    val comp = Player(symbol = "O")

    init {
        _field.value = Array(9) {""}
//        _field.value = arrayOf("O", "X", "O", "X", "", "", "O", "", "")
//        _field.value = arrayOf("O", "", "", "", "", "", "", "", "")
    }

    fun controllerGame() {

    }

    fun togglePlayer(nextPlayer: String) {
        activePlayer = if (activePlayer === "X") "O" else "X"
    }

    // Игровой ход
    fun moveGame(i: Int) {
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

        val winner = checkWinner(newFiled)

        if (winner != null) {
            _winPlayer.value = activePlayer
            _isEndGame.value = true
        }

        if (activePlayer === "X") {
            activePlayer = "O"

            val bestMove = findBestMove(newFiled, activePlayer)

            if (bestMove.index != null) {
                moveGame(bestMove.index!!)
            }

            Log.d("findBestMove", "${bestMove.index}, ${bestMove.score}")
        } else {
            activePlayer = "X"
        }
    }

    // Новая игра
    fun newGame() {
        _field.value = Array(9) {""}
        _isEndGame.value = false
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
    fun findBestMove(field: Array<String>, playerSymbol: String): BestMove {
        var availSpots = emptyIndices(field);
        var winner = checkWinner(field);

//        Log.d("findBestMove", "findBestMove start ${field.joinToString()}; $playerSymbol; $winner")

        if (winner == "O") {
            return BestMove(score = -1)
        }

        if (winner == "X") {
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

            if (playerSymbol == "X") {
                move.score = findBestMove(field, "O").score
            } else {
                move.score = findBestMove(field, "X").score
            }

            field[availSpots[i]] = ""
            moves.add(move);
        }

        var bestMove = 0;
        // если это ход ИИ, пройти циклом по ходам и выбрать ход с наибольшим количеством очков
        if (playerSymbol == "X") {
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

    // Поиск пустых клеток
    fun emptyIndices(field: Array<String>): ArrayList<Int> {
        val emptyCells: ArrayList<Int> = arrayListOf();

        field.forEachIndexed { index, symbol ->
            if (symbol === "") {
                emptyCells.add(index)
            }
        }

        return emptyCells
    }
}

class BestMove(
    var index: Int? = null,
    var score: Int? = null
)

class Player(
    var symbol: String
) {
    fun getMove() {

    }
}
