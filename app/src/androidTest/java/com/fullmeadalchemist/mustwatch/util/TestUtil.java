package com.fullmeadalchemist.mustwatch.util;

import com.fullmeadalchemist.mustwatch.vo.Batch;
import com.fullmeadalchemist.mustwatch.vo.Group;
import com.fullmeadalchemist.mustwatch.vo.User;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.getRandVolume;
import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.getStatus;
import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randDouble;
import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randFloat;
import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.randInt;
import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.roundOneDecimalPlace;
import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.roundThreeDecimalPlaces;
import static com.fullmeadalchemist.mustwatch.demo.DemoGenerators.roundTwoDecimalPlaces;


public class TestUtil {

    public static User createUser() {
        String name = "test_user" + INSTANCE.randInt(1, 100);
        String email = name + "@example.com";
        return new User(name, email);
    }

    public static List<User> createUsers(int n) {
        Set<Integer> usedIds = new HashSet<>();
        List<User> usersToAdd = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int userN = INSTANCE.randInt(1, 10*n);
            while (usedIds.contains(userN)){
                userN = INSTANCE.randInt(1, 10*n);
            }
            usedIds.add(userN);
            String name = "test_user" + userN;
            String email = name + "@example.com";
            usersToAdd.add(new User(name, email));
        }
        return usersToAdd;
    }


    public static Batch createBatch(long userId) {
        Batch b = new Batch();
        b.setName("Dummy Batch #" + INSTANCE.randInt(1, 1000));
        b.setCreateDate(GregorianCalendar.getInstance());
        b.setStatus(INSTANCE.getStatus());
        b.setTargetSgStarting(INSTANCE.roundThreeDecimalPlaces(INSTANCE.randDouble(1.10d, 1.30d)));
        b.setTargetSgFinal(INSTANCE.roundThreeDecimalPlaces(INSTANCE.randDouble(0.95d, 1.05d)));
        b.setStartingPh(INSTANCE.roundTwoDecimalPlaces(INSTANCE.randFloat(3.0f, 5.5f)));
        b.setStartingTemp(INSTANCE.roundOneDecimalPlace(INSTANCE.randFloat(65f, 75f)));
        b.setOutputVolume(INSTANCE.getRandVolume());
        b.setTargetABV(INSTANCE.roundTwoDecimalPlaces(INSTANCE.randFloat(0.08f, 0.17f)));
        b.setUserId(userId);
        b.setNotes(String.format("Dummy Batch \"%s\"", b.getName()));
        return b;
    }

    public static Group createGroup(String name) {
        return new Group(name);
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