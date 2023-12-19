package com.example.favspot

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

internal class SpotListRecyclerAdapter(val context: Context,var lists: List<SpotList>) : RecyclerView.Adapter<SpotListRecyclerAdapter.ViewHolder>() {

    var layoutInflater = LayoutInflater.from(context)
    lateinit var db : FirebaseFirestore
    lateinit var auth : FirebaseAuth
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var itemNameView = itemView.findViewById<TextView>(R.id.itemTextView)
        var itemImageView : ImageView = itemView.findViewById(R.id.itemImageView)
        //var itemImageView = itemView.findViewById<ImageView>(R.id.itemImageView)
        var itemPosistion = 0





        init {
            itemView.setOnClickListener {
                val intent = Intent(context,CreateAndEditSpotList::class.java)
                intent.putExtra(ITEM_POSISTION_KEY,itemPosistion)
                context.startActivity(intent)
            }

        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotListRecyclerAdapter.ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_item,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SpotListRecyclerAdapter.ViewHolder, position: Int) {
        var itemList = lists[position]
        holder.itemNameView.text = itemList.itemName
        holder.itemImageView.setImageResource(itemList.itemImage)
        holder.itemPosistion = position
    }

    override fun getItemCount(): Int {
       return lists.size
    }


}
