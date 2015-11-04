package com.levor.liferpg.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Task {
    private String title;
    private List<Skill> relatedSkills = new ArrayList<>();
    private UUID id;

    public Task(String title, UUID id, Skill ... skills) {
        this.title = title;
        this.relatedSkills = Arrays.asList(skills);
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public List<Skill> getRelatedSkills() {
        return relatedSkills;
    }

    public void addRelatedSkills(Skill ... skills) {
        this.relatedSkills = Arrays.asList(skills);
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

    public void setRelatedSkills(List<Skill> relatedSkills) {
        this.relatedSkills = relatedSkills;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public UUID getId() {
        return id;
    }
}
