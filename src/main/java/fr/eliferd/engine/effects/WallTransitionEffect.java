package fr.eliferd.engine.effects;

import fr.eliferd.engine.ResourceManager;
import fr.eliferd.engine.callbacks.ITransitionEventCallback;
import fr.eliferd.engine.renderer.Texture;
import fr.eliferd.game.Game;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static fr.eliferd.engine.utils.RenderUtils.COLOR_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.POS_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.TEXCOORDS_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.VBO_PCT_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class WallTransitionEffect extends AbstractEffect {
    private static final int WINDOW_WIDTH = Game.instance().getWindow().getWidth();
    private static final int WINDOW_HEIGHT = Game.instance().getWindow().getHeight();

    private float xOffset = 0;
    private ITransitionEventCallback _callback;
    private boolean _isCallbackTriggered = false;

    public WallTransitionEffect(float duration) {
        super(duration);
    }

    public void setTransitionCallback(ITransitionEventCallback callback) {
        this._callback = callback;
    }

    public void start() {
        super.start();
        Game.instance().setTransitionInProgress(true);
        this.xOffset = 0;
        this._isCallbackTriggered = false;
        if (this.effectVBO == 0) {
            this.effectVBO = glGenBuffers();
        }
    }

    public void update(float dt) {
        super.update(dt);
        this.xOffset += 100f * this.effectProgress;

        if (Math.round(this.effectProgress) == Math.round(this.effectDuration / 2) && !this._isCallbackTriggered && this._callback != null) {
            this._isCallbackTriggered = true;
            this._callback.onScreenFullfilled();
        }

        if (this.effectProgress >= this.effectDuration && Game.instance().isTransitionInProgress()) {
            Game.instance().setTransitionInProgress(false);
        }

        this.drawTexture();
    }

    private void drawTexture() {
        glBindVertexArray(this.vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, this.effectVBO);

        final float[] colors = new float[]{1, 1, 1, 1f};

        int textureMultiplier = 15;

        final float[] vertices = new float[] {
                (-WINDOW_WIDTH) + this.xOffset, 0,                                colors[0], colors[1], colors[2], colors[3],        0.0f, 0.0f, // bottom left
                (-WINDOW_WIDTH) + this.xOffset, WINDOW_HEIGHT,                    colors[0], colors[1], colors[2], colors[3],        0.0f, 1.0f * textureMultiplier, // top left
                0 + this.xOffset, WINDOW_HEIGHT,         colors[0], colors[1], colors[2], colors[3],        (1.0f * textureMultiplier)*1.53f, 1.0f * textureMultiplier, // top right

                0 + this.xOffset, WINDOW_HEIGHT,         colors[0], colors[1], colors[2], colors[3],        (1.0f * textureMultiplier)*1.53f, 1.0f * textureMultiplier, // top right
                0 + this.xOffset, 0,                     colors[0], colors[1], colors[2], colors[3],        (1.0f * textureMultiplier)*1.53f, 0.0f, // bottom right
                (-WINDOW_WIDTH) + this.xOffset, 0,                                colors[0], colors[1], colors[2], colors[3],        0.0f, 0.0f, // bottom left
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

        Texture tex = ResourceManager.getTexture("assets/textures/wall.png");
        Game.instance().getWindow().getShader().uploadUniform1i("uTex", 0);
        Game.instance().getWindow().getShader().uploadUniform1i("uHasTexture", 1);
        Game.instance().getWindow().getShader().uploadUniform1i("uUseLights", 0);
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
