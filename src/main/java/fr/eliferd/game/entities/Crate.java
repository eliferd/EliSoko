package fr.eliferd.game.entities;

import org.joml.Vector4f;

public class Crate extends BaseEntity {

    private Goal _reachedGoal = null;

    public Crate() {
        super();
        this._minimapColor = new Vector4f(0.804f, 0.522f, 0.247f, 1f);
    }

    public void setInGoal(Goal goal) {
        this._reachedGoal = goal;
    }

    public boolean hasReachedGoal() {
        return this._reachedGoal != null;
    }

    public Goal getReachedGoal() {
        return this._reachedGoal;
    }

    @Override
    public void update(float dt) {
    }
}
