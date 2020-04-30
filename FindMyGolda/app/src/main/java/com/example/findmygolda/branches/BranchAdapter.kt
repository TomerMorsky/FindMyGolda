package com.example.findmygolda.branches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.databinding.ListBranchItemBinding

class BranchAdapter(val clickListener: BranchClickListener) : ListAdapter<BranchEntity, BranchAdapter.ViewHolder>(BranchDiffCallback()) {

    var data =  listOf<BranchEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }

    class ViewHolder private constructor(val binding: ListBranchItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(
            item: BranchEntity,
            clickListener: BranchClickListener
        ) {
            binding.branch = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val binding = ListBranchItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }

    }


    class BranchClickListener(val clickListener: (branchId: Long) -> Unit) {
        fun onClick(branch: BranchEntity) = clickListener(branch.id.toLong())
    }
}

class BranchDiffCallback : DiffUtil.ItemCallback<BranchEntity>() {

    override fun areItemsTheSame(oldItem: BranchEntity, newItem: BranchEntity): Boolean {
        return oldItem.id == newItem.id
    }


    override fun areContentsTheSame(oldItem: BranchEntity, newItem: BranchEntity): Boolean {
        return oldItem == newItem
    }


}
