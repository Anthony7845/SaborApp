package com.senati.saborapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapp.databinding.ItemPlatoBinding

class PlatoAdapter(
    private val onClick: (Plato) -> Unit,
    private val onLongClick: (Plato) -> Unit
) : ListAdapter<Plato, PlatoAdapter.PlatoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlatoViewHolder {

        val binding = ItemPlatoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlatoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlatoViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class PlatoViewHolder(
        private val binding: ItemPlatoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plato: Plato) {

            binding.tvNombre.text = plato.nombre

            binding.tvCategoria.text =
                if (plato.categoria.isBlank()) {
                    "Sin categoría"
                } else {
                    plato.categoria
                }

            binding.tvPrecio.text =
                String.format("S/ %.2f", plato.precio)

            if (plato.disponible == 1) {

                binding.tvDisponible.text = "Disponible"

                binding.tvDisponible.setTextColor(
                    Color.parseColor("#1F3D2B")
                )

                binding.tvDisponible.setBackgroundResource(
                    com.senati.saborapp.R.drawable.bg_disponible
                )

            } else {

                binding.tvDisponible.text = "No disponible"

                binding.tvDisponible.setTextColor(
                    Color.parseColor("#B3261E")
                )

                binding.tvDisponible.setBackgroundResource(
                    com.senati.saborapp.R.drawable.bg_no_disponible
                )
            }

            binding.root.setOnClickListener {
                onClick(plato)
            }

            binding.root.setOnLongClickListener {
                onLongClick(plato)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Plato>() {

        override fun areItemsTheSame(
            oldItem: Plato,
            newItem: Plato
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Plato,
            newItem: Plato
        ): Boolean {
            return oldItem == newItem
        }
    }
}