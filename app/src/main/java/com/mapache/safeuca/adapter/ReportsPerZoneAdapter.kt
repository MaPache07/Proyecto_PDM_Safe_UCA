package com.mapache.safeuca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Report
import kotlinx.android.synthetic.main.card_view_report_simple.view.*

class ReportsPerZoneAdapter (var items : List<Report>, val pending : String, val done : String) : RecyclerView.Adapter<ReportsPerZoneAdapter.viewholder>(){
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

    inner class viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item : Report) = with(itemView){
            nombre_de_reportes_en_lista_simple.text = item.name
            if(item.status == "0")
                status_del_reporte_en_lista_simple.text = pending
            else status_del_reporte_en_lista_simple.text = done
            if(item.level == -1) floor_tv.visibility = View.GONE
            else piso_del_reporte_en_lista_simple.text = item.level.toString()
        }
    }
}