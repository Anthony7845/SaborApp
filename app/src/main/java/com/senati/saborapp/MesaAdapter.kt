package com.senati.saborapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapp.databinding.ItemMesaBinding

class MesaAdapter(
    private val onClick: (Mesa) -> Unit,
    private val onLongClick: (Mesa) -> Unit
) : ListAdapter<Mesa, MesaAdapter.MesaViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MesaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MesaViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class MesaViewHolder(
        private val binding: ItemMesaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mesa: Mesa) {
            val numTexto = mesa.numero.trim()
            val numMostrar = if (numTexto.lowercase().startsWith("mesa")) {
                numTexto
            } else {
                "Mesa $numTexto"
            }

            binding.tvNumero.text = if (numTexto.isEmpty()) "Mesa ${mesa.id}" else numMostrar
            binding.tvCapacidad.text = "👤 ${mesa.capacidad}"

            val estadoLower = mesa.estado.lowercase().trim()
            val esLibre = estadoLower in listOf("disponible", "libre", "1", "vacía", "vacia", "")

            if (esLibre) {
                binding.cardMesa.setCardBackgroundColor(Color.parseColor("#1F3D2B"))
                binding.tvNumero.setTextColor(Color.WHITE)
                binding.tvCapacidad.setTextColor(Color.WHITE)
            } else {
                binding.cardMesa.setCardBackgroundColor(Color.parseColor("#F2A93B"))
                binding.tvNumero.setTextColor(Color.parseColor("#1F3D2B"))
                binding.tvCapacidad.setTextColor(Color.parseColor("#1F3D2B"))
            }

            binding.root.setOnClickListener {
                onClick(mesa)
            }

            binding.root.setOnLongClickListener {
                onLongClick(mesa)
                true
            }

            binding.root.post {
                val width = binding.root.width
                if (width > 0 && binding.root.layoutParams.height != width) {
                    binding.root.layoutParams.height = width
                    binding.root.requestLayout()
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Mesa>() {
        override fun areItemsTheSame(
            oldItem: Mesa,
            newItem: Mesa
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Mesa,
            newItem: Mesa
        ): Boolean {
            return oldItem == newItem
        }
    }
}