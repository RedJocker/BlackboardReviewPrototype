package org.hyperskill.blackboard.internals.backend.database

import org.hyperskill.blackboard.internals.backend.model.User

object MockUserDatabase {
    val users = mapOf(
        "George" to User("George", "TEACHER", "1234"),
        "Lucas" to User("Lucas", "STUDENT", "32A1"),
        "Martin" to User("Martin", "STUDENT", "2222"),
    )
}