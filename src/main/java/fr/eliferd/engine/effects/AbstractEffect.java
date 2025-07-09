package fr.eliferd.engine.effects;

import fr.eliferd.engine.renderer.Shader;
import fr.eliferd.game.Game;

public abstract class AbstractEffect {
    protected int vaoID;
    protected int effectVBO;
    protected float effectDuration;
    protected float effectProgress;

    public AbstractEffect(float duration) {
        this.effectDuration = duration;
        this.effectProgress = this.effectDuration;
    }

    protected Shader getShader() {
        return Game.instance().getWindow().getShader();
    }

    public void setVaoID(int vao) {
        vaoID = vao;
    }

    public void start() {
        this.effectProgress = 0;
    }

    public void update(float dt) {
        if (this.hasStarted()) {
            this.effectProgress += dt;
        }
    }

    public boolean hasStarted() {
        return this.effectProgress < this.effectDuration;
    }
}
