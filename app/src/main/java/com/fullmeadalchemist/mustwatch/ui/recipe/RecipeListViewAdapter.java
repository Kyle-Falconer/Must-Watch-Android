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

package com.fullmeadalchemist.mustwatch.ui.recipe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fullmeadalchemist.mustwatch.R;
import com.fullmeadalchemist.mustwatch.vo.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class RecipeListViewAdapter extends RecyclerView.Adapter<RecipeListViewAdapter.ViewHolder> {

    private final RecipeClickCallback recipeClickCallback;
    List<Recipe> dataSet;
    private Locale defaultLocale = Locale.getDefault();

    public RecipeListViewAdapter(List<Recipe> dataSet, RecipeClickCallback recipeClickCallback) {
        if (dataSet == null)
            dataSet = new ArrayList<>();
        this.dataSet = dataSet;
        this.recipeClickCallback = recipeClickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Timber.d("Recipe element %s set.", position);

        final Recipe r = dataSet.get(position);

        viewHolder.getRecipeLabelTextView().setText(String.format(defaultLocale, "%s", r.name));
        viewHolder.getRecipeNumberTextView().setText(String.format(defaultLocale, "Recipe %d", r.id));
        viewHolder.getRecipeTargetAbvTextView().setText(String.format(defaultLocale, "Recipe %d", r.id));

        viewHolder.itemView.setOnClickListener(v -> {
            if (recipeClickCallback != null) {
                Timber.d("Element for recipe #%s was clicked.", r.id);
                recipeClickCallback.onClick(r);
            } else {
                Timber.e("No click listener set or Recipe is null!?");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    public interface RecipeClickCallback {
        void onClick(Recipe repo);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView recipeLabelTextView;
        private final TextView recipeNumberTextView;
        private final TextView recipeTargetAbvTextView;

        public ViewHolder(View v) {
            super(v);
            recipeLabelTextView = v.findViewById(R.id.name);
            recipeNumberTextView = v.findViewById(R.id.recipe_id);
            recipeTargetAbvTextView = v.findViewById(R.id.recipeCardTargetAbvTextView);
        }

        public TextView getRecipeLabelTextView() {
            return recipeLabelTextView;
        }

        public TextView getRecipeNumberTextView() {
            return recipeNumberTextView;
        }

        public TextView getRecipeTargetAbvTextView() {
            return recipeTargetAbvTextView;
        }
    }
}
