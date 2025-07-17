package fr.eliferd.game.entities;

import org.joml.Vector4f;

public class Goal extends BaseEntity{

    public Goal() {
        super();
        this._minimapColor = new Vector4f(0.5f, 0, 0, 0.5f);
    }

    @Override
    public void update(float dt) {

    }
}
