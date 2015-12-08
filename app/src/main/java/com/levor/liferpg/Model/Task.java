package com.levor.liferpg.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Task {
    private String title;
    private List<Skill> relatedSkills = new ArrayList<>();
    private UUID id;

    public Task(String title, UUID id, Skill ... skills) {
        this(title, id, Arrays.asList(skills));
    }

    public Task(String title, UUID id, List<Skill> skills) {
        this.title = title;
        this.relatedSkills = skills;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public List<Skill> getRelatedSkills() {
        return relatedSkills;
    }

    public String getRelatedSkillsString() {
        StringBuilder sb = new StringBuilder();
        for (Skill sk : relatedSkills) {
            sb.append(sk.getId())
                    .append("::");
        }
        return sb.toString();
    }

    public void setRelatedSkills(Skill... skills) {
        this.relatedSkills = Arrays.asList(skills);
    }

    public void addRelatedSkill(Skill sk){
        this.relatedSkills.add(sk);
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
