package com.example.juan.buscachinos.presentation.map

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.juan.buscachinos.R
import com.example.juan.buscachinos.domain.model.Chino

class ChinoListAdapter(
    private val onClick: (Chino) -> Unit
) : ListAdapter<Chino, ChinoListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chino, parent, false) as TextView
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val textView: TextView,
        private val onClick: (Chino) -> Unit
    ) : RecyclerView.ViewHolder(textView) {
        fun bind(chino: Chino) {
            textView.text = chino.name
            textView.setOnClickListener { onClick(chino) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Chino>() {
            override fun areItemsTheSame(oldItem: Chino, newItem: Chino) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Chino, newItem: Chino) = oldItem == newItem
        }
    }
}
