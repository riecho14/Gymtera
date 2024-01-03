package com.tugasakhir.gymtera.data

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.tugasakhir.gymtera.databinding.TrainingListItemBinding

class TrainingAdapter : BaseQuickAdapter<TrainingData, TrainingAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val binding: TrainingListItemBinding = TrainingListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VH, position: Int, item: TrainingData?) {
        holder.binding.apply {
            trainingTitle.text = item?.name
            Glide.with(holder.itemView.context).load(item?.imageUrl).into(trainingImg)
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }
}