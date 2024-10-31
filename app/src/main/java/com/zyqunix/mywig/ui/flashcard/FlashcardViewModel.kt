package com.zyqunix.mywig.ui.flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import android.widget.Toast

class FlashcardViewModel(context: Context) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("FlashcardPrefs", Context.MODE_PRIVATE)

    private val _currentQuestion = MutableLiveData<String>()
    val currentQuestion: LiveData<String> get() = _currentQuestion

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    private val _highScore = MutableLiveData<Int>()
    val highScore: LiveData<Int> get() = _highScore

    private var flashcards: MutableList<Pair<String, String>> = mutableListOf()
    private var currentIndex = 0

    init {
        _score.value = 0
        _highScore.value = sharedPreferences.getInt("HIGH_SCORE", 0) // Load saved high score
        showNextQuestion()
    }

    fun showNextQuestion() {
        if (currentIndex < flashcards.size) {
            _currentQuestion.value = flashcards[currentIndex].first
        } else {
            _currentQuestion.value = "No more questions!" // Handle end of flashcards
        }
    }

    fun checkAnswer(userAnswer: String, context: Context) {
        if (currentIndex < flashcards.size) {
            val correctAnswer = flashcards[currentIndex].second
            if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                _score.value = (_score.value ?: 0) + 1
                Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
            } else {
                _score.value = (_score.value ?: 0) - 1
                Toast.makeText(context, "Incorrect! Try Again!", Toast.LENGTH_SHORT).show()
            }
            currentIndex++ // Move to next question after checking
            showNextQuestion()
        }
    }

    fun resetQuiz() {
        updateHighScore() // Update high score when quiz is reset
        _score.value = 0
        currentIndex = 0
        showNextQuestion()
    }

    fun addFlashcard(question: String, answer: String) {
        flashcards.add(Pair(question, answer))
    }

    private fun updateHighScore() {
        val currentScore = _score.value ?: 0
        if (currentScore > (_highScore.value ?: 0)) {
            _highScore.value = currentScore
            // Save the new high score in SharedPreferences
            sharedPreferences.edit().putInt("HIGH_SCORE", currentScore).apply()
        }
    }
}
