package com.fullmeadalchemist.mustwatch.ui.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.fullmeadalchemist.mustwatch.repository.UserRepository;
import com.fullmeadalchemist.mustwatch.vo.User;

import javax.inject.Inject;

public class UserProfileViewModel extends ViewModel {
    private final LiveData<User> user;
    private UserRepository userRepo;


    @Inject
    public UserProfileViewModel(UserRepository userRepo) {
        this.userRepo = userRepo;
        this.user = userRepo.getCurrentUser();
    }

    public LiveData<User> getUser() {
        return this.user;
    }
}
