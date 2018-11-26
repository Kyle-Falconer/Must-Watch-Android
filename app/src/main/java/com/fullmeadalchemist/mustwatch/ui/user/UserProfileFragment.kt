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
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fullmeadalchemist.mustwatch.R
import com.fullmeadalchemist.mustwatch.databinding.UserDetailFragmentBinding
import com.fullmeadalchemist.mustwatch.ui.common.MainViewModel
import com.fullmeadalchemist.mustwatch.vo.User
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class UserProfileFragment : Fragment() {

    lateinit var dataBinding: UserDetailFragmentBinding


    val viewModel: MainViewModel by sharedViewModel()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.user_detail_fragment, container, false)
                as UserDetailFragmentBinding
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getCurrentUser().observe(this, Observer<User> { user ->
            if (user != null) {
                dataBinding.user = user

                dataBinding.uuidTextView.text = user.uid.toString()
            } else {
                Timber.e("Got a null user from the database.")
            }
        })

    }

    companion object {
        private val UID_KEY = "uid"
    }
}
