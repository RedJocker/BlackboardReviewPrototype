package org.hyperskill.blackboard.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.hyperskill.blackboard.BlackBoardClient
import org.hyperskill.blackboard.BlackboardApplication

class LoginViewModel(blackBoardClient: BlackBoardClient) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val blackboardClient =
                    (this[APPLICATION_KEY] as BlackboardApplication).blackboardClient
                LoginViewModel(blackboardClient)
            }
        }
    }
}