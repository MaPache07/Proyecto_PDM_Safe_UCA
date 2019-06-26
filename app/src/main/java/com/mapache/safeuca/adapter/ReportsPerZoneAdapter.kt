package com.mapache.safeuca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import kotlinx.android.synthetic.main.card_view_report_simple.view.*

class ReportsPerZoneAdapter (var items : List<Report>) : RecyclerView.Adapter<ReportsPerZoneAdapter.viewholder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_report_simple,parent,false)
        return viewholder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(items[position])
    }

    fun dataChange(listaMatches : List<Report>){
        items = listaMatches
        notifyDataSetChanged()
    }

    class viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item : Report) = with(itemView){
            nombre_de_reportes_en_lista_simple.text = item.name
            status_del_reporte_en_lista_simple.text = item.status
            piso_del_reporte_en_lista_simple.text = item.level.toString()
        }
    }
}