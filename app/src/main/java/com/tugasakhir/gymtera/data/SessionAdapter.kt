package com.tugasakhir.gymtera.data

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.tugasakhir.gymtera.databinding.TrainingSessionListBinding

class SessionAdapter(private val difficultyLevel: String) :
    BaseQuickAdapter<ExerciseData, SessionAdapter.VH>() {
    private val checkedItems = mutableSetOf<Int>()
    var onItemCheckedListener: OnItemCheckedListener? = null

    class VH(
        parent: ViewGroup,
        val binding: TrainingSessionListBinding = TrainingSessionListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: VH, position: Int, item: ExerciseData?) {
        item?.let {
            holder.binding.exerciseName.text = it.exerName
            holder.binding.sets.text = getSetsText(difficultyLevel)
            holder.binding.reps.text = getRepsText(difficultyLevel)
            Glide.with(holder.itemView.context).load(it.exerImage).into(holder.binding.imageView2)

            holder.binding.checkBox.isChecked = checkedItems.contains(position)
            holder.binding.checkBox.setOnClickListener {
                if (checkedItems.contains(position)) {
                    checkedItems.remove(position)
                } else {
                    checkedItems.add(position)
                }
                onItemCheckedListener?.onItemChecked(checkedItems.size == itemCount)
            }
            holder.binding.checkBox.isChecked = checkedItems.contains(position)
        }
    }

    interface OnItemCheckedListener {
        fun onItemChecked(allItemsChecked: Boolean)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    private fun getSetsText(difficultyLevel: String): String {
        return when (difficultyLevel) {
            "beginner" -> "2"
            "intermediate" -> "3"
            "advanced" -> "4"
            else -> "N/A"
        }
    }

    private fun getRepsText(difficultyLevel: String): String {
        return when (difficultyLevel) {
            "beginner" -> "10"
            "intermediate" -> "12"
            "advanced" -> "15"
            else -> "N/A"
        }
    }
}