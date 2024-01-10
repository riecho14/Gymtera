package com.tugasakhir.gymtera.data

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.tugasakhir.gymtera.databinding.HistoryListItemBinding

class HistoryAdapter : BaseQuickAdapter<UserData, HistoryAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val binding: HistoryListItemBinding = HistoryListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VH, position: Int, item: UserData?) {
        holder.binding.apply {
            date.text = item?.date
            time.text = item?.time
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }
}