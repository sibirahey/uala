package com.sibi.uala.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sibi.uala.R
import com.sibi.uala.databinding.MealItemBinding

import com.sibi.uala.model.MealUiModel

class RecipesAdapter(
    private val onItemClick: (MealUiModel) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = listOf<MealUiModel>()

    fun setData(list: List<MealUiModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = MealItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MealItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is MealItemViewHolder -> {
                holder.bind(item)
                holder.itemView.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    class MealItemViewHolder(private val viewBinding: MealItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(meal: MealUiModel) {
            viewBinding.apply {
                mealNameTextView.text = meal.name
                categoryTextView.text = meal.category
                Glide.with(this.mealImageView.context).load(meal.imageUrl)
                    .placeholder(R.drawable.ic_place_holder)
                    .into(this.mealImageView)
            }
        }
    }
}