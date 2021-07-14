package com.chihyao.snake

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class SnakeViewModel : ViewModel() {

    val body = MutableLiveData<List<Position>>()
    val apple = MutableLiveData<Position>()
    val score = MutableLiveData<Int>()
    val gameState = MutableLiveData<GameState>()

    private val snakeBody = mutableListOf<Position>()
    private var direction = Direction.LEFT
    private lateinit var applePos: Position
    private var point: Int = 0

    fun start (){
        gameState.postValue(GameState.ONGOING)
    }

    fun refresh() {
        fixedRateTimer("timer", true, 500, 500) {
            if (gameState.value == GameState.ONGOING) {
                val pos = snakeBody.first().copy().apply {
                    when (direction) {
                        Direction.LEFT -> x--
                        Direction.RIGHT -> x++
                        Direction.TOP -> y--
                        Direction.DOWN -> y++
                    }
                    if (snakeBody.contains(this) || x < 0 || x >= 20 || y < 0 || y >= 20) {
                        cancel()
                        gameState.postValue(GameState.GAME_OVER)
                    }
                }
                snakeBody.add(0, pos)
                if (pos != applePos) {
                    snakeBody.removeLast()
                } else {
                    point += 100
                    score.postValue(point)
                    generateApple()
                }
                body.postValue(snakeBody)
            } else {
                cancel()
            }
        }
    }

    fun generateApple() {
        val spots = mutableListOf<Position>().apply {
            for (i in 0..19) {
                for (j in 0..19) {
                    add(Position(i, j))
                }
            }
        }
        spots.removeAll(snakeBody)
        spots.shuffle()
        applePos = spots[0]

/*
        do {
            applePos = Position(Random.nextInt(20), Random.nextInt(20))
        }while (snakeBody.contains(applePos))
*/

        apple.postValue(applePos)
    }

    fun reset (){
        snakeBody.removeAll(snakeBody)
        point = 0
        score.postValue(point)
        snakeBody.apply {
            add(Position(10,10))
            add(Position(11,10))
            add(Position(12,10))
            add(Position(13,10))
        }.also {
            body.value = it
        }
        direction = Direction.LEFT
        generateApple()
    }

    fun pause () {
        gameState.postValue(GameState.PAUSE)
    }

    fun move (dir: Direction){
        when (direction) {
            Direction.TOP -> if (dir != Direction.DOWN) direction = dir
            Direction.DOWN -> if (dir != Direction.TOP) direction = dir
            Direction.LEFT -> if (dir != Direction.RIGHT) direction = dir
            Direction.RIGHT -> if (dir != Direction.LEFT) direction = dir
        }
//        direction = dir
    }
}

data class Position(var x: Int, var y: Int)

enum class Direction {
    TOP, DOWN, LEFT, RIGHT
}

enum class GameState{
    ONGOING, GAME_OVER, PAUSE
}