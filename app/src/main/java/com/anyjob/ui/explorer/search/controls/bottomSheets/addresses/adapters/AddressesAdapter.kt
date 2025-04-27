package com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anyjob.R
import com.anyjob.ui.explorer.search.controls.bottomSheets.addresses.models.UserAddress
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.search.ToponymObjectMetadata

object AddressDifferenceCallback : DiffUtil.ItemCallback<GeoObject>() {
    override fun areItemsTheSame(oldItem: GeoObject, newItem: GeoObject): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: GeoObject, newItem: GeoObject): Boolean {
        val oldItemPosition = oldItem.geometry.firstOrNull()?.point
        val newItemPosition = newItem.geometry.firstOrNull()?.point
        return oldItemPosition?.latitude == newItemPosition?.latitude && oldItemPosition?.longitude == newItemPosition?.longitude
    }
}

class AddressesAdapter(
    private val geoObjects: List<GeoObject>,
    private val onClick: ((UserAddress) -> Unit)? = null
) : ListAdapter<GeoObject, AddressesAdapter.ViewHolder>(AddressDifferenceCallback) {
    class ViewHolder(
        private val view: View,
        private val onClick: ((UserAddress) -> Unit)? = null
    ) : RecyclerView.ViewHolder(view) {
        private val _addressTitle: TextView = view.findViewById(R.id.address_primary_text)
        private val _addressSubtitle: TextView = view.findViewById(R.id.address_secondary_text)
        private var _currentGeoObject: GeoObject? = null

        fun bind(geoObject: GeoObject) {
            _currentGeoObject = geoObject

            _addressTitle.text = geoObject.name
            _addressSubtitle.text = geoObject.descriptionText

            if (onClick != null) {
                view.setOnClickListener {
                    _currentGeoObject?.also {
                        val userAddress = UserAddress(
                            geoObject = it,
                            formattedAddress = _addressTitle.text.toString()
                        )

                        onClick.invoke(userAddress)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)

        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (geoObjects.any()) {
            holder.bind(geoObjects[position])
        }
    }

    override fun getItemCount(): Int {
        return geoObjects.size
    }
}