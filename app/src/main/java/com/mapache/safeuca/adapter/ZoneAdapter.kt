package com.mapache.safeuca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapache.safeuca.R
import com.mapache.safeuca.database.entities.Zone
import kotlinx.android.synthetic.main.card_view_zone.view.*

class ZoneAdapter (var items : List<Zone>, val clickListener : (Zone) -> Unit) : RecyclerView.Adapter<ZoneAdapter.viewholder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_zone,parent,false)
        return viewholder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(items[position],clickListener)
    }

    fun dataChange(listaZones : List<Zone>){
        items = listaZones
        notifyDataSetChanged()
    }

    class viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item : Zone, clickListener: (Zone) -> Unit) = with(itemView){
            nombre_de_zona_en_lista.text = item.name
            this.setOnClickListener{clickListener(item)}
        }
    }
}