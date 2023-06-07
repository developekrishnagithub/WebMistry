package com.example.webmistry2

import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.webmistry2.databinding.ItemRowBinding

class WebViewHoder(val itemRowBinding: ItemRowBinding,val  clickItem: ClickItem):ViewHolder(itemRowBinding.root) {

    fun setData(web: Web){
        itemRowBinding.webName.text=web.webName
        itemRowBinding.webIconImage.setImageResource(web.webIcon)
        itemRowBinding.webIconImage.setOnClickListener {
            clickItem.clickItem(web)
        }
    }
}