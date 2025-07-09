package fr.eliferd.engine.utils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class FPSCounter {
    private double _lastTime = glfwGetTime();
    private int _frames = 0;
    private int _fps = 0;

    public void update() {
        double currentTime = glfwGetTime();
        this._frames++;

        if (currentTime - this._lastTime >= 1.0) {
            this._fps = this._frames;
            this._frames = 0;
            this._lastTime = currentTime;
        }
    }

    public int getFPS() {
        return _fps;
    }
}