package com.fullmeadalchemist.mustwatch.util;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.User;

import java.util.ArrayList;

/**
 * Created by Kyle on 7/22/2017.
 */

public class TestUtil {

    public static User createUser(String name, String email) {
        return new User(name, email);
    }

    public static Batch createBatch() {
        return new Batch();
    }

//    public static List<Repo> createRepos(int count, String owner, String name,
//                                         String description) {
//        List<Repo> repos = new ArrayList<>();
//        for(int i = 0; i < count; i ++) {
//            repos.add(createRepo(owner + i, name + i, description + i));
//        }
//        return repos;
//    }
//
//    public static Repo createRepo(String owner, String name, String description) {
//        return createRepo(Repo.UNKNOWN_ID, owner, name, description);
//    }
//
//    public static Repo createRepo(int id, String owner, String name, String description) {
//        return new Repo(id, name, owner + "/" + name,
//                description, new Repo.Owner(owner, null), 3);
//    }
//
//    public static Contributor createContributor(Repo repo, String login, int contributions) {
//        Contributor contributor = new Contributor(login, contributions, null);
//        contributor.setRepoName(repo.name);
//        contributor.setRepoOwner(repo.owner.login);
//        return contributor;
//    }
}