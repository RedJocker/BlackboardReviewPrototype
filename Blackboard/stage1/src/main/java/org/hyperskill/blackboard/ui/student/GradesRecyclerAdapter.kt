package org.hyperskill.blackboard.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.databinding.ListItemGradeBinding

class GradesRecyclerAdapter(grades : List<Int>, val onPredictionGradesChanged: (List<Int>) -> Unit) : RecyclerView.Adapter<GradesRecyclerAdapter.GradesViewHolder>() {

    var grades : List<Int> = grades
        set(value) {
            field = value
            predictionGrades = value.toMutableList()
            notifyDataSetChanged()
        }

    private var predictionGrades : MutableList<Int> = grades.toMutableList()
        set(value) {
            field = value
            for(i in value.indices) {
                value[i] = if(value[i] < 0) 0 else value[i]
            }
            onPredictionGradesChanged(predictionGrades)
        }

    inner class GradesViewHolder(val item: ListItemGradeBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(gradeValue: Int, gradeIndex: Int) {
            item.gradeHeaderTV.text = "T:${gradeIndex + 1}"
            if(gradeValue < 0) {
                item.gradeValueET.isEnabled = true
                item.gradeValueET.setOnEditorActionListener { _, actionId, _ ->
                    if(actionId == EditorInfo.IME_ACTION_DONE) {
                        val inputIntValue = item.gradeValueET.text.toString().toIntOrNull() ?: 0
                        val normalizedInputValue = if(inputIntValue > 100) {
                            item.gradeValueET.setText("100")
                            100
                        } else inputIntValue

                        predictionGrades[gradeIndex] = normalizedInputValue
                        onPredictionGradesChanged(predictionGrades)
                        true
                    } else {
                        false
                    }
                }
            } else {
                item.gradeValueET.setText("$gradeValue")
                item.gradeValueET.isEnabled = false
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