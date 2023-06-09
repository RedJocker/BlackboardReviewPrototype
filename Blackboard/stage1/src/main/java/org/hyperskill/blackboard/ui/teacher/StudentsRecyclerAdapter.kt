package org.hyperskill.blackboard.ui.teacher

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.data.model.Student

class StudentsRecyclerAdapter(students : List<Student>, val onStudentClick: (Student) -> Unit)
    : RecyclerView.Adapter<StudentsRecyclerAdapter.StudentsViewHolder>() {

    var students = students
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class StudentsViewHolder(val root: TextView) : RecyclerView.ViewHolder(root) {
        fun bind(position: Int) {
            val student = students[position]
            root.text = student.name
            root.setPadding(50)
            root.setOnClickListener {
                onStudentClick(student)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        return StudentsViewHolder(TextView(parent.context))
    }

    override fun getItemCount(): Int {
        return students.size
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        holder.bind(position)
    }
}