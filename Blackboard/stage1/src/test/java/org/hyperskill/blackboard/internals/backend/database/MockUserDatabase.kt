package org.hyperskill.blackboard.internals.backend.database

import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.model.User
import org.hyperskill.blackboard.internals.backend.model.User.Role.ROLE_TEACHER

object MockUserDatabase {
    const val GEORGE = "George"
    const val LUCAS = "Lucas"
    const val MARTIN = "Martin"


    val users = mapOf(
        GEORGE to User(GEORGE, ROLE_TEACHER, "1234"),
        LUCAS to Student(LUCAS, "32A1", Grades(listOf(60, -1, 50, 70, 99, 80, -1), -1)),
        MARTIN to Student(MARTIN, "2222", Grades(listOf(60, -1, 50, 70, 99, 80, -1), -1)),
    )
}