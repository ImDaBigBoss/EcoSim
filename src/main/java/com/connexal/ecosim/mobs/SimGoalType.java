package com.connexal.ecosim.mobs;

import com.connexal.ecosim.mobs.goals.FleeGoal;
import com.connexal.ecosim.mobs.goals.MultiplyGoal;
import com.connexal.ecosim.mobs.goals.SustainGoal;
import com.connexal.ecosim.utils.MobSex;

//The definition order is the priority order, top executes first
public enum SimGoalType {
    FLEE(FleeGoal.class, true),
    SUSTAIN(SustainGoal.class),
    MULTIPLY(MultiplyGoal.class, MobSex.MALE);

    private final Class<? extends SimGoal> goal;
    private final boolean isFlee;
    private final MobSex targetSex;

    SimGoalType(Class<? extends SimGoal> goal, boolean isFlee, MobSex targetSex) {
        this.goal = goal;
        this.isFlee = isFlee;
        this.targetSex = targetSex;
    }

    SimGoalType(Class<? extends SimGoal> goal, boolean isFlee) {
        this(goal, isFlee, null);
    }

    SimGoalType(Class<? extends SimGoal> goal, MobSex targetSex) {
        this(goal, false, targetSex);
    }

    SimGoalType(Class<? extends SimGoal> goal) {
        this(goal, false, null);
    }

    public Class<? extends SimGoal> getGoal() {
        return this.goal;
    }

    public boolean isFlee() {
        return this.isFlee;
    }

    public MobSex getTargetSex() {
        return this.targetSex;
    }
}
