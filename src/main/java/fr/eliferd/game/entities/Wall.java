package fr.eliferd.game.entities;

import org.joml.Vector4f;

public class Wall extends BaseEntity{
    public Wall() {
        super();
        this._minimapColor = new Vector4f(0.545f, 0.271f, 0.075f, 1);
    }
    @Override
    public void update(float dt) {

    }
}
