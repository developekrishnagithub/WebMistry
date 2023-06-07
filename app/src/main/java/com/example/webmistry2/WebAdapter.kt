package com.example.webmistry2

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.webmistry2.databinding.ItemRowBinding

class WebAdapter constructor(val listOfWebUrl:ArrayList<Web>,val mainActivity: MainActivity):Adapter<WebViewHoder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebViewHoder {
        return WebViewHoder(ItemRowBinding.inflate(LayoutInflater.from(parent.context),parent,false),mainActivity as ClickItem)
    }

    override fun getItemCount(): Int {
      return listOfWebUrl.size
    }

    override fun onBindViewHolder(holder: WebViewHoder, position: Int) {
       holder.setData(listOfWebUrl[position])
    }
}