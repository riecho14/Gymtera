package com.tugasakhir.gymtera.data

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter4.BaseQuickAdapter
import com.tugasakhir.gymtera.databinding.EquipmentListItemBinding

class ExerciseAdapter : BaseQuickAdapter<ExerciseData, ExerciseAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val binding: EquipmentListItemBinding = EquipmentListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VH, position: Int, item: ExerciseData?) {
        item?.let {
            holder.binding.equipmentTitle.text = it.exerName
            holder.binding.equipmentDesc.text = it.exerDesc
            Glide.with(holder.itemView.context).load(it.exerImage).into(holder.binding.equipmentImg)
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }
}