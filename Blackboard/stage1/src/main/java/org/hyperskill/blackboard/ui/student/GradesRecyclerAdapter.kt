package org.hyperskill.blackboard.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.databinding.ListItemGradeBinding

class GradesRecyclerAdapter(grades : List<String>) : RecyclerView.Adapter<GradesRecyclerAdapter.GradesViewHolder>() {

    var grades : List<String> = grades
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class GradesViewHolder(val item: ListItemGradeBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(gradeValue: String, gradeIndex: Int) {
            item.gradeHeaderTV.text = "T:$gradeIndex"
            item.gradeValueTV.text = gradeValue
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
        holder.bind(grades[position], position + 1)
    }
}