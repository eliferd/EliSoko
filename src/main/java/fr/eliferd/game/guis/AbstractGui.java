package fr.eliferd.game.guis;

import fr.eliferd.engine.ResourceManager;
import fr.eliferd.engine.renderer.Texture;
import fr.eliferd.game.Game;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static fr.eliferd.engine.utils.RenderUtils.COLOR_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.POS_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.TEXCOORDS_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.VBO_PCT_SIZE;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public abstract class AbstractGui {
    protected int _bgVaoId = 0;
    protected int _bgVboId = 0;
    protected int[] _currentWindowWidth = {0};
    protected int[] _currentWindowHeight = {0};
    public abstract void init();
    public abstract void update(float dt);

    public AbstractGui() {
        glfwGetWindowSize(glfwGetCurrentContext(), this._currentWindowWidth, this._currentWindowHeight);
    }

    protected final void drawBackgroundLayer(String texturePath, float lightRadius) {
        glBindVertexArray(this._bgVaoId);
        glBindBuffer(GL_ARRAY_BUFFER, _bgVboId);

        final float[] colors = new float[]{1, 1, 1, 0.5f};

        int textureMultiplier = 15;

        final float[] vertices = new float[] {
                0, 0,                                                       colors[0], colors[1], colors[2], colors[3],        0.0f, 0.0f, // bottom left
                0, this._currentWindowHeight[0],                            colors[0], colors[1], colors[2], colors[3],        0.0f, 1.0f * textureMultiplier, // top left
                this._currentWindowWidth[0], this._currentWindowHeight[0],  colors[0], colors[1], colors[2], colors[3],        (1.0f * textureMultiplier)*1.53f, 1.0f * textureMultiplier, // top right

                this._currentWindowWidth[0], this._currentWindowHeight[0],  colors[0], colors[1], colors[2], colors[3],        (1.0f * textureMultiplier)*1.53f, 1.0f * textureMultiplier, // top right
                this._currentWindowWidth[0], 0,                             colors[0], colors[1], colors[2], colors[3],        (1.0f * textureMultiplier)*1.53f, 0.0f, // bottom right
                0, 0,                                                       colors[0], colors[1], colors[2], colors[3],        0.0f, 0.0f, // bottom left
        };

        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length);
        fb.put(vertices).flip();

        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VBO_PCT_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VBO_PCT_SIZE * Float.BYTES, (POS_SIZE * Float.BYTES));
        glVertexAttribPointer(2, TEXCOORDS_SIZE, GL_FLOAT, false, VBO_PCT_SIZE * Float.BYTES, ((POS_SIZE + COLOR_SIZE) * Float.BYTES));
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        Texture tex = ResourceManager.getTexture(texturePath);
        Game.instance().getWindow().getShader().uploadUniform1i("uTex", 0);
        Game.instance().getWindow().getShader().uploadUniform1i("uHasTexture", 1);
        Game.instance().getWindow().getShader().uploadUniform1i("uUseLights", 1);
        Game.instance().getWindow().getShader().uploadUniform2f("uLightPos", new Vector2f(10.5f, 10.5f)); // coordonnée normalisée
        Game.instance().getWindow().getShader().uploadUniform3f("uLightColor", new Vector3f(1, 1, 1)); // jaune clair
        Game.instance().getWindow().getShader().uploadUniform1f("uLightRadius", lightRadius);
        Game.instance().getWindow().getShader().uploadUniform1f("uAmbient", 0.3f);
        tex.bind();

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
    }
}
