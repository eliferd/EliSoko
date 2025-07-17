package fr.eliferd.game.entities;

import org.joml.Vector4f;

public class Ground extends BaseEntity {
    public Ground() {
        super();
        this._minimapColor = new Vector4f(1, 0.973f, 0.863f, 1f);
    }
    @Override
    public void update(float dt) {

    }
}
