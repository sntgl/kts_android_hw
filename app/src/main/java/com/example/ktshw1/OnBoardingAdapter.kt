package com.example.ktshw1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_onboarding_feature.view.*

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
        return data.size//TODO
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding_feature, parent, false)
        )

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        with (holder.itemView) {
            item_onboarding_image.setImageResource(data[position].drawable)
            item_onboarding_text.text = data[position].text
        }
    }


    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
    data class PagerModel(
        @DrawableRes
        val drawable: Int,
        val text: String
    )
}