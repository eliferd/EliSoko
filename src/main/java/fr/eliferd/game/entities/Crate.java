package fr.eliferd.game.entities;

public class Crate extends BaseEntity {

    private Goal _reachedGoal = null;

    public Crate() {
        super();
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
