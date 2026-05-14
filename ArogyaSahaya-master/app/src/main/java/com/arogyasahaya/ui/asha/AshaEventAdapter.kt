package com.arogyasahaya.ui.asha

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arogyasahaya.data.entity.AshaEventEntity
import com.arogyasahaya.data.entity.AshaEventType
import com.arogyasahaya.databinding.ItemAshaEventBinding
import java.text.SimpleDateFormat
import java.util.*

class AshaEventAdapter : ListAdapter<AshaEventEntity, AshaEventAdapter.EventViewHolder>(DiffCallback) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, EEEE", Locale.getDefault())

    inner class EventViewHolder(private val binding: ItemAshaEventBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: AshaEventEntity) {
            binding.tvEventTitle.text    = event.title
            binding.tvEventDate.text     = dateFormat.format(Date(event.eventDate))
            binding.tvEventLocation.text = event.location
            binding.tvEventDesc.text     = event.description

            // Set icon/color based on event type
            val (emoji, colorResId) = when (event.eventType) {
                AshaEventType.HEALTH_CAMP    -> "🏥" to "#1565C0"
                AshaEventType.ASHA_VISIT     -> "👩‍⚕️" to "#2E7D32"
                AshaEventType.VACCINATION    -> "💉" to "#E65100"
                AshaEventType.WELLNESS_CHECK -> "❤️" to "#880E4F"
            }
            binding.tvEventIcon.text = emoji
            binding.cardEvent.setCardBackgroundColor(
                android.graphics.Color.parseColor("#F5F5F5")
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemAshaEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AshaEventEntity>() {
        override fun areItemsTheSame(a: AshaEventEntity, b: AshaEventEntity) = a.id == b.id
        override fun areContentsTheSame(a: AshaEventEntity, b: AshaEventEntity) = a == b
    }
}
