package org.hyperskill.blackboard.ui.teacher.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.databinding.ListItemGradeBinding

class TeacherGradesStudentAdapter(
    grades : List<Int>, val onGradesChanged: (grades: List<Int>) -> Unit) : RecyclerView.Adapter<TeacherGradesStudentAdapter.GradesViewHolder>() {

    private var grades : List<Int> = grades
        set(value) {
            field = value
            editedGrades = value.toMutableList()
            notifyDataSetChanged()
        }

    private var editedGrades : MutableList<Int> = grades.toMutableList()

    inner class GradesViewHolder(val item: ListItemGradeBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(gradeValue: Int, gradeIndex: Int) {
            item.gradeHeaderTV.text = "T:${gradeIndex + 1}"
            item.gradeValueET.setText("$gradeValue")
            item.gradeValueET.setOnEditorActionListener { v, actionId, event ->
                val inputIntValue = item.gradeValueET.text.toString().toIntOrNull() ?: -1
                val normalizedInputValue = if(inputIntValue > 100) {
                    item.gradeValueET.setText("100")
                    100

                } else inputIntValue

                editedGrades[adapterPosition] = normalizedInputValue
                onGradesChanged(editedGrades)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradesViewHolder {
        val itemGradeBinding =
            ListItemGradeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return GradesViewHolder(itemGradeBinding)
    }

    override fun getItemCount(): Int {
        return grades.size
    }

    override fun onBindViewHolder(holder: GradesViewHolder, position: Int) {
        holder.bind(grades[position], position)
    }
}