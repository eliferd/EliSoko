package fr.eliferd.engine.renderer;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

public class Camera {
    private Vector2f position;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    public Camera(float width, float height) {
        this.position = new Vector2f(0, 0);
        this.projectionMatrix = new Matrix4f().ortho(0f, width, 0f, height, -1f, 1f);
        this.viewMatrix = new Matrix4f();
        updateView();
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        updateView();
    }

    private void updateView() {
        this.viewMatrix.identity().translate(-this.position.x, -this.position.y, 0);
    }

    public Matrix4f getViewProjection() {
        return new Matrix4f(this.projectionMatrix).mul(this.viewMatrix); // projection * view
    }
}
