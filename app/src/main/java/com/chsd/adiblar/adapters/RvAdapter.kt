package com.chsd.adiblar.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chsd.adiblar.R
import com.chsd.adiblar.databinding.InnerItemBinding
import com.chsd.adiblar.models.Writer

class RvAdapter(
    var list: ArrayList<Writer>,
    var fav: ArrayList<Writer>,
    var fragment: Fragment,
    var onpress: onPress
) :
    RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(var itemview: InnerItemBinding) : RecyclerView.ViewHolder(itemview.root) {
        @SuppressLint("ResourceAsColor")
        fun Bind(writer: Writer, position: Int) {
            itemview.name.text = writer.name
            itemview.year.text = "(${writer.birth_date} - ${writer.death_date})"
            Glide
                .with(fragment)
                .load(writer.image)
                .into(itemview.wrImage)
            for (i in fav) {
                if (writer.name == i.name) {
                    itemview.favorites2.visibility = View.VISIBLE
                }
            }
            itemview.favorites.setOnClickListener {
                if (itemview.favorites2.isInvisible) {
                    itemview.favorites2.visibility = View.VISIBLE
                } else {
                    itemview.favorites2.visibility = View.INVISIBLE
                }
                onpress.onclick(writer)
            }
            itemview.container.setOnClickListener {
                onpress.myIntent(writer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(InnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.Bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
    interface onPress {
        fun onclick(writer: Writer)
        fun myIntent(writer: Writer)
    }
}