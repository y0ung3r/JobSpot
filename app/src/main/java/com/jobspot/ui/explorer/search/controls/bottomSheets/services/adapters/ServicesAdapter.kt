package com.jobspot.ui.explorer.search.controls.bottomSheets.services.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jobspot.R
import com.jobspot.domain.services.models.Service

object ServicesDifferenceCallback : DiffUtil.ItemCallback<Service>() {
    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem.id == newItem.id
    }
}

class ServicesAdapter(
    private val services: List<Service>,
    private val onClick: ((Service) -> Unit)? = null
) : ListAdapter<Service, ServicesAdapter.ViewHolder>(ServicesDifferenceCallback) {
    class ViewHolder(
        private val view: View,
        private val onClick: ((Service) -> Unit)? = null
    ) : RecyclerView.ViewHolder(view) {
        private val _serviceCategory: TextView = view.findViewById(R.id.service_category)
        private val _serviceTitle: TextView = view.findViewById(R.id.service_title)

        fun bind(service: Service) {
            _serviceCategory.text = service.category
            _serviceTitle.text = service.title

            if (onClick != null) {
                view.setOnClickListener {
                    onClick.invoke(service)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.service_item, parent, false)

        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (services.any()) {
            holder.bind(services[position])
        }
    }

    override fun getItemCount(): Int {
        return services.size
    }
}