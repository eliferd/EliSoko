package fr.eliferd.engine.effects;

import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static fr.eliferd.engine.utils.RenderUtils.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class BlinkEffect extends AbstractEffect {
    private Vector4f _colors;
    private Vector2i _position;
    private Vector2i _size;

    public BlinkEffect(float duration, Vector2i position, Vector2i size, Vector4f colors) {
        super(duration);
        this._colors = colors;
        this._position = position;
        this._size = size;
    }

    public void start() {
        if (this.effectVBO == 0) {
            this.effectVBO = glGenBuffers();
        }

        super.start();
    }

    public void update(float dt) {
        if (this.hasStarted()) {
            float alpha = 1.0f - Math.clamp(this.effectProgress / this.effectDuration, 0.0f, 1.0f);
            this._colors.w = alpha;
            this.drawVertices();
            this.effectProgress += dt;
        }
        super.update(dt);
    }

    private void drawVertices() {
        glBindVertexArray(this.vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, this.effectVBO);
        final float[] vertices = new float[]{
                this._position.x, this._position.y,                                 this._colors.x, this._colors.y, this._colors.z, this._colors.w, // bottom left
                this._position.x, this._position.y + this._size.y,                  this._colors.x, this._colors.y, this._colors.z, this._colors.w, // top left
                this._position.x + this._size.x, this._position.y + this._size.y,   this._colors.x, this._colors.y, this._colors.z, this._colors.w, // top right

                this._position.x + this._size.x, this._position.y + this._size.y,   this._colors.x, this._colors.y, this._colors.z, this._colors.w, // top right
                this._position.x + this._size.x, this._position.y,                  this._colors.x, this._colors.y, this._colors.z, this._colors.w, // bottom right
                this._position.x, this._position.y,                                 this._colors.x, this._colors.y, this._colors.z, this._colors.w, // bottom left
        };
        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length);
        fb.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, fb, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VBO_PC_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VBO_PC_SIZE * Float.BYTES, (POS_SIZE * Float.BYTES));
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        this.getShader().uploadUniform1i("uHasTexture", 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
}
