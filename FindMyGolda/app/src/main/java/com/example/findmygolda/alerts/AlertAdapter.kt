package com.example.findmygolda.alerts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.findmygolda.R
import com.example.findmygolda.database.AlertEntity
import java.text.SimpleDateFormat

class AlertAdapter: RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    var data =  listOf<AlertEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        val alertTitle: TextView = itemView.findViewById(R.id.alert_title)
        val alertDiscription: TextView = itemView.findViewById(R.id.alert_discription)
        val alertTime: TextView = itemView.findViewById(R.id.alert_time)

        fun bind(
            item: AlertEntity
        ) {
            val res = itemView.context.resources
            alertTitle.text = item.title
            alertDiscription.text = item.description
            alertTime.text = convertLongToDateString(item.time)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(
                        R.layout.list_alert_item,
                        parent, false
                    )
                return ViewHolder(view)
            }
        }

        private fun convertLongToDateString(systemTime: Long): String {
            return SimpleDateFormat("HH:mm'\n'dd-MM-yy'\n'")
                .format(systemTime).toString()
        }

    }

}
