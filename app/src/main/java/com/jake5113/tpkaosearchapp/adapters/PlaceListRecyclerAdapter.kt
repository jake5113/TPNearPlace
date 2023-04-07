package com.jake5113.tpkaosearchapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jake5113.tpkaosearchapp.activities.PlaceUrlActivity
import com.jake5113.tpkaosearchapp.databinding.RecyclerItemListFragmentBinding
import com.jake5113.tpkaosearchapp.model.Place

class PlaceListRecyclerAdapter(var context: Context, var documents: MutableList<Place>) :
    Adapter<PlaceListRecyclerAdapter.VH>() {
    inner class VH(var binding: RecyclerItemListFragmentBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(RecyclerItemListFragmentBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place: Place = documents[position]
        holder.binding.tvPlaceName.text = place.place_name
//        if(place.road_address_name == "") holder.binding.tvAddress.text = place.address_name
//        else holder.binding.tvAddress.text = place.road_address_name
        holder.binding.tvAddress.text = if (place.road_address_name == "") place.address_name else place.road_address_name
        holder.binding.tvDistance.text = "${place.distance}m"

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("place_url", place.place_url)
            context.startActivity(intent)
        }



    }
}