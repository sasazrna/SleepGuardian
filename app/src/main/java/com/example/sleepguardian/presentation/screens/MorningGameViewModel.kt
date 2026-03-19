package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class MorningGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()

    private var startTime: Long = 0

    fun startGame() {
        _uiState.update { it.copy(gameState = GameState.WAITING, reactionTime = null) }
        // In a real app, use a timer or delay to trigger the GO state
    }

    fun onSignalReady() {
        _uiState.update { it.copy(gameState = GameState.GO) }
        startTime = System.currentTimeMillis()
    }

    fun onTap() {
        if (_uiState.value.gameState == GameState.GO) {
            val endTime = System.currentTimeMillis()
            val reaction = endTime - startTime
            _uiState.update { it.copy(gameState = GameState.FINISHED, reactionTime = reaction) }
        } else if (_uiState.value.gameState == GameState.WAITING) {
            _uiState.update { it.copy(gameState = GameState.START) } // False start
        }
    }

    data class GameUiState(
        val gameState: GameState = GameState.START,
        val reactionTime: Long? = null
    )

    enum class GameState {
        START, WAITING, GO, FINISHED
    }
}
