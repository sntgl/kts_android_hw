package com.example.ktshw1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.example.ktshw1.databinding.ItemOnboardingFeatureBinding

class OnBoardingAdapter :
    RecyclerView.Adapter<OnBoardingAdapter.PagerVH>() {

    //TODO Пока захардкодил, тк не знаю, что сюда разместить в данный момент
    private val data = listOf(
        PagerModel(
            drawable = R.drawable.hand,
            text = "Hi!"
        ),
        PagerModel(
            drawable = R.drawable.lb_ic_play,
            text = "It's BReddit."
        ),
        PagerModel(
            drawable = R.drawable.ic_baseline_arrow_downward_24,
            text = "Just press 'start'."
        )
    )

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(
            ItemOnboardingFeatureBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        holder.bind(data[position])
    }

    class PagerVH(private val binding: ItemOnboardingFeatureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: PagerModel) {
            with(binding) {
                itemOnboardingImage.setImageResource(model.drawable)
                itemOnboardingText.text = model.text
            }
        }
    }

    data class PagerModel(
        @DrawableRes
        val drawable: Int,
        val text: String
    )
}