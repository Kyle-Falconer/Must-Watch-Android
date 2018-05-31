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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.vo.Recipe
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber

class RecipeListFragment : Fragment() {
    protected var mRecyclerView: RecyclerView? = null
    lateinit var mAdapter: RecipeListViewAdapter

    private var viewModel: RecipeViewModel? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.recipe_list, container, false)
        rootView.tag = TAG

        mAdapter = RecipeListViewAdapter(null, object : RecipeListViewAdapter.RecipeClickCallback {
            override fun onClick(recipe: Recipe) {
                Timber.e("Navigation to Recipe #${recipe.id} not yet implemented")
//                navigationController.navigateToRecipeDetail(recipe.id)
            }
        })

        viewModel = ViewModelProviders.of(activity!!).get(RecipeViewModel::class.java)
        viewModel!!.currentUserId.observe(this, Observer<Long> { userId ->
            viewModel!!.getRecipes(userId).observe(this, Observer<List<Recipe>> { recipes ->
                // update UI
                mAdapter.dataSet = recipes
                mAdapter.notifyDataSetChanged()
            })
        })

        mRecyclerView = rootView.findViewById(R.id.recipe_list)
        mRecyclerView?.let {
            val llm = LinearLayoutManager(context)
            llm.orientation = LinearLayoutManager.VERTICAL

            it.setHasFixedSize(true)
            it.layoutManager = llm
            it.adapter = mAdapter
        }

        return rootView
    }

    companion object {

        private val TAG = RecipeListFragment::class.java.simpleName
    }
}