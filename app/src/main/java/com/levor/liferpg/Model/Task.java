package com.levor.liferpg.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task {
    private String title;
    private List<Skill> relatedSkills = new ArrayList<>();

    public Task(String title, Skill ... skills) {
        this.title = title;
        this.relatedSkills = Arrays.asList(skills);
    }

    public String getTitle() {
        return title;
    }

    public List<Skill> getRelatedSkills() {
        return relatedSkills;
    }

    public void perform(){
        for (Skill s: relatedSkills) {
            s.increaseSublevel();
        }
    }

    public void performNTimes(int N){
        for (int i = 0; i < N; i++) {
            perform();
        }
    }
}
