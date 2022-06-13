package com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.adapters

import android.location.Address
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anyjob.R

object AddressDifferenceCallback : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.longitude == newItem.longitude && oldItem.latitude == newItem.latitude
    }
}

class AddressesAdapter(private val addresses: List<Address>, private val onClick: (address: Address) -> Unit) : ListAdapter<Address, AddressesAdapter.ViewHolder>(AddressDifferenceCallback) {
    class ViewHolder(view: View, onClick: (address: Address) -> Unit) : RecyclerView.ViewHolder(view) {
        private val addressTitle: TextView = view.findViewById(R.id.address_title)
        private var currentAddress: Address? = null

        init {
            view.setOnClickListener {
                currentAddress?.also(onClick)
            }
        }

        fun bind(address: Address) {
            currentAddress = address
            addressTitle.text = address.getAddressLine(0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)

        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (addresses.any()) {
            holder.bind(addresses[position])
        }
    }

    override fun getItemCount(): Int {
        return addresses.size
    }
}