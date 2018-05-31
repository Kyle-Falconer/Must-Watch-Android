/*
 * Copyright (c) 2017 Full Mead Alchemist, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fullmeadalchemist.mustwatch.ui.recipe

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.vo.Recipe
import timber.log.Timber
import java.util.*

class RecipeListViewAdapter(var dataSet: List<Recipe>? = arrayListOf(), private val recipeClickCallback: RecipeClickCallback?) : RecyclerView.Adapter<RecipeListViewAdapter.ViewHolder>() {

    private val defaultLocale = Locale.getDefault()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.recipe_card_view, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Timber.d("Recipe element %s set.", position)

        val r = dataSet!![position]

        viewHolder.recipeLabelTextView.text = String.format(defaultLocale, "%s", r.name)
        viewHolder.recipeNumberTextView.text = String.format(defaultLocale, "Recipe %d", r.id)
        viewHolder.recipeTargetAbvTextView.text = String.format(defaultLocale, "Recipe %d", r.id)

        viewHolder.itemView.setOnClickListener { v ->
            if (recipeClickCallback != null) {
                Timber.d("Element for recipe #%s was clicked.", r.id)
                recipeClickCallback.onClick(r)
            } else {
                Timber.e("No click listener set or Recipe is null!?")
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataSet == null) 0 else dataSet!!.size
    }

    interface RecipeClickCallback {
        fun onClick(recipe: Recipe)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val recipeLabelTextView: TextView
        val recipeNumberTextView: TextView
        val recipeTargetAbvTextView: TextView

        init {
            recipeLabelTextView = v.findViewById(R.id.name)
            recipeNumberTextView = v.findViewById(R.id.recipe_id)
            recipeTargetAbvTextView = v.findViewById(R.id.recipeCardTargetAbvTextView)
        }
    }
}
