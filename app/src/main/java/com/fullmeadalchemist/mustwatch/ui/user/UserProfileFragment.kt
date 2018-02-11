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

package com.fullmeadalchemist.mustwatch.ui.user

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.databinding.UserDetailFragmentBinding
import com.fullmeadalchemist.mustwatch.ui.common.NavigationController
import com.fullmeadalchemist.mustwatch.vo.User
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

class UserProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var navigationController: NavigationController
    lateinit var dataBinding: UserDetailFragmentBinding
    private var viewModel: UserProfileViewModel? = null

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.user_detail_fragment, container, false)
                as UserDetailFragmentBinding
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserProfileViewModel::class.java)

        viewModel?.currentUserId?.observe(this, Observer<Long?> { userId ->
            if (userId != null) {
                viewModel!!.getUser(userId).observe(this, Observer<User> { user ->
                    Timber.d("Got user from db: %s", user)
                    if (user != null) {
                        if (user.isAnon) {
                            user.name = "Anonymous"
                        }
                        dataBinding.user = user
                    }
                })
            } else {
                Timber.e("Got a null user ID from the database.")
            }
        })

    }

    companion object {
        private val UID_KEY = "uid"
    }
}
