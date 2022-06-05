package com.example.smokesms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessagesAdapter(private val list_: List<MessagesData>, private val listener: onItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==1){
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.msg_receive, parent, false)
            return ReceiveViewHolder(itemView)
        }
        else{
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.msg_sent, parent, false)
            return SentViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        val currentItem = list_[position]
        if(holder.javaClass == ReceiveViewHolder::class.java){
            val viewHolder = holder as ReceiveViewHolder
            holder.msgReceiveBody.text = currentItem.msgBody
        }
        else{
            val viewHolder = holder as SentViewHolder
            holder.msgSentBody.text = currentItem.msgBody
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(list_[position].isSent == "1"){
            return 1
        }
        else{
            return 2
        }
    }

    override fun getItemCount() = list_.size
   inner class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener{
        val msgReceiveBody: TextView = itemView.findViewById(R.id.msg_receive_text)
        init{
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            listener.onItemClick(position)
        }
    }
    inner class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener{
        val msgSentBody: TextView = itemView.findViewById(R.id.msg_sent_text)
        init{
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            listener.onItemClick(position)
        }
    }
     interface onItemClickListener{
        fun onItemClick(position: Int)
    }
}