package com.example.mistriji

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mistriji.databinding.AdapterBinding

class QRAdapter:RecyclerView.Adapter<QRAdapter.QRViewHolder>() {
    inner class QRViewHolder(private val binding: AdapterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(qrInfo: QRInfo){
            binding.apply {
                name.text = qrInfo.name
                code.text=qrInfo.code
                reward.text=qrInfo.redeem
            }
        }
    }

    private val differCallbacks= object : DiffUtil.ItemCallback<QRInfo>(){
        override fun areItemsTheSame(oldItem: QRInfo, newItem: QRInfo): Boolean {
            return oldItem.name==newItem.name
        }

        override fun areContentsTheSame(oldItem: QRInfo, newItem: QRInfo): Boolean {
            return oldItem==newItem
        }
    }

    val differ= AsyncListDiffer(this,differCallbacks)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRViewHolder {
        return QRViewHolder(
            AdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }


    override fun onBindViewHolder(holder: QRViewHolder, position: Int) {
        val qrcode=differ.currentList[position]
        holder.bind(qrcode)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}