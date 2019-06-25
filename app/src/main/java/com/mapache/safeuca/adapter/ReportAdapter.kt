package com.mapache.safeuca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import kotlinx.android.synthetic.main.card_view.view.*

class ReportAdapter (var items : List<Report>, val clickListener : (Report) -> Unit) : RecyclerView.Adapter<ReportAdapter.viewholder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view,parent,false)
        return viewholder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(items[position],clickListener)
    }

    fun dataChange(listaMatches : List<Report>){
        items = listaMatches
        notifyDataSetChanged()
    }

    class viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item : Report, clickListener: (Report) -> Unit) = with(itemView){
            nombre_de_reportes_en_lista.text = item.name
            status_del_reporte_en_lista.text = item.status
            this.setOnClickListener{clickListener(item)}
        }
    }
}