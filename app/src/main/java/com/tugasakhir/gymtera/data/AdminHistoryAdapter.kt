package com.tugasakhir.gymtera.data

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.tugasakhir.gymtera.databinding.AdminHistoryListBinding

class AdminHistoryAdapter : BaseQuickAdapter<UserData, AdminHistoryAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val binding: AdminHistoryListBinding = AdminHistoryListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VH, position: Int, item: UserData?) {
        holder.binding.apply {
            name.text = item?.fullname
            date.text = item?.date
            time.text = item?.time
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }
}