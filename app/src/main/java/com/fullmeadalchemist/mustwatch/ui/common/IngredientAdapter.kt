package com.fullmeadalchemist.mustwatch.ui.common

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.vo.BatchIngredient
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*

class IngredientListViewAdapter(private val ingredientClickCallback: IngredientClickCallback?,
                                var dataSet: List<BatchIngredient> = arrayListOf())
    : RecyclerView.Adapter<IngredientListViewAdapter.IngredientViewHolder>() {
    private val defaultLocale = Locale.getDefault()


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): IngredientViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.ingredient_item, viewGroup, false)
        return IngredientViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: IngredientViewHolder, position: Int) {
        Timber.d("Element $position set.")

        val item = dataSet[position]

        val resId = viewHolder.itemView.resources.getIdentifier(item.ingredientId,
                "string", viewHolder.itemView.context.packageName)
        viewHolder.ingredientLabelTextView.text = viewHolder.itemView.context.getText(resId)

        item.quantityVol?.let { vol ->
            val outVol = vol.value.toFloat()
            if (outVol < 0.01) {
                viewHolder.ingredientAmountTextView.text = "-"
            } else {
                val volumeAmount = vol.value as Double
                val f = DecimalFormat("#.##")
                viewHolder.ingredientAmountTextView.text = String.format(defaultLocale, "%s", f.format(volumeAmount))
                viewHolder.ingredientUnitTextView.text = vol.unit.toString()
            }
        }
        item.quantityMass?.let { mass ->
            val outMassl = mass.value.toFloat()
            if (outMassl < 0.01) {
                viewHolder.ingredientAmountTextView.text = "-"
            } else {
                val volumeAmount = mass.value as Double
                val f = DecimalFormat("#.##")
                viewHolder.ingredientAmountTextView.text = String.format(defaultLocale, "%s", f.format(volumeAmount))
                viewHolder.ingredientUnitTextView.text = mass.unit.toString()
            }
        }

        viewHolder.itemView.setOnClickListener { v ->
            if (ingredientClickCallback != null) {
                Timber.d("Element for batch ingredient #%s was clicked.", item.id)
                ingredientClickCallback.onClick(item)
            } else {
                Timber.e("No click listener set or BatchIngredient is null!?")
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    interface IngredientClickCallback {
        fun onClick(repo: BatchIngredient)
    }

    class IngredientViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val ingredientLabelTextView: TextView = v.findViewById(R.id.ingredient_label)
        val ingredientAmountTextView: TextView = v.findViewById(R.id.ingredient_amount)
        val ingredientUnitTextView: TextView = v.findViewById(R.id.ingredient_unit)
    }
}
