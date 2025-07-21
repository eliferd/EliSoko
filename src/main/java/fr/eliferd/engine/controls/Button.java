package fr.eliferd.engine.controls;

import fr.eliferd.engine.callbacks.IClickCallback;
import fr.eliferd.engine.callbacks.IHoverCallback;
import fr.eliferd.engine.input.Mouse;
import fr.eliferd.game.Game;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static fr.eliferd.engine.utils.RenderUtils.*;
import static fr.eliferd.engine.utils.RenderUtils.COLOR_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.POS_SIZE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Button {

    private String _label;
    private Vector2i _pos;
    private Vector2i _size;
    private Vector4f _color;
    private Vector4f _hoverColor;
    private IClickCallback _clickHandler = null;
    private IHoverCallback _hoverHandler = null;
    private Vector2i _textSize;
    private boolean _isDisabled = false;

    private int _vaoID = 0;
    private int _vboID = 0;

    public Button() {
        this._vaoID = glGenVertexArrays();
        this._vboID = glGenBuffers();

        this.setDefaultButtonProperties();
    }

    public void setLabel(String _label) {
        this._label = _label;
    }

    public void setDisabled(boolean isDisabled) {
        this._isDisabled = isDisabled;
    }

    public void setPos(Vector2i _pos) {
        this._pos = _pos;
    }

    public void setSize(Vector2i _size) {
        this._size = _size;
    }

    public void setColor(Vector4f _color) {
        this._color = _color;
    }

    public void setHoverColor(Vector4f _hoverColor) {
        this._hoverColor = _hoverColor;
    }

    public void onClick(IClickCallback callback) {
        this._clickHandler = callback;
    }

    public void onHover(IHoverCallback callback) {
        this._hoverHandler = callback;
    }

    public boolean isMouseOver() {
        int mouseX = (int)Mouse.getPosX();
        int mouseY = Game.instance().getWindow().getHeight() - (int)Mouse.getPosY();
        return (mouseX >= this._pos.x && mouseX <= (this._pos.x + this._size.x)) &&
                (mouseY >= this._pos.y && mouseY <= (this._pos.y + this._size.y));
    }

    public boolean isClicked() {
        return this.isMouseOver() && Mouse.isButtonPressed(GLFW_MOUSE_BUTTON_1);
    }

    public void update() {
        glBindVertexArray(this._vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, this._vboID);

        final float[] vertices = getVertices();

        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length);
        fb.put(vertices).flip();

        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VBO_PC_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VBO_PC_SIZE * Float.BYTES, (POS_SIZE * Float.BYTES));
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        Game.instance().getWindow().getShader().uploadUniform1i("uHasTexture", 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 1));

        if (this._textSize == null) {
            this._textSize = Game.instance().getFontRenderer().getTextSize(this._label, 4f);
        }

        Game.instance().getFontRenderer().drawText(this._label, this._pos.x + (this._size.x - this._textSize.x) / 2f, this._pos.y, 4f);

        if (!Game.instance().isTransitionInProgress()) {
            if (this.isMouseOver() && this._hoverHandler != null && !this._isDisabled && !this.isClicked()) {
                this._hoverHandler.onHover();
            }

            if (this.isClicked() && this._clickHandler != null && !this._isDisabled) {
                this._clickHandler.actionPerformed();
                Mouse.resetMouseButtonsState(); // Mark all buttons as "idle" after the click to avoid weird conflicts
            }
        }
    }

    private float[] getVertices() {
        Vector4f drawnColor = this.isMouseOver() && !this._isDisabled ? this._hoverColor : this._color;

        final float[] vertices = new float[] {
                // pos + size                                            colors
                this._pos.x, this._pos.y,                                drawnColor.x, drawnColor.y, drawnColor.z, drawnColor.w, // bottom left
                this._pos.x, this._pos.y + this._size.y,                 drawnColor.x, drawnColor.y, drawnColor.z, drawnColor.w, // top left
                this._pos.x + this._size.x, this._pos.y + this._size.y,  drawnColor.x, drawnColor.y, drawnColor.z, drawnColor.w, // top right

                this._pos.x + this._size.x, this._pos.y + this._size.y,  drawnColor.x, drawnColor.y, drawnColor.z, drawnColor.w, // top right
                this._pos.x + this._size.x, this._pos.y,                 drawnColor.x, drawnColor.y, drawnColor.z, drawnColor.w, // bottom right
                this._pos.x, this._pos.y,                                drawnColor.x, drawnColor.y, drawnColor.z, drawnColor.w, // bottom left
        };
        return vertices;
    }

    private void setDefaultButtonProperties() {
        this.setColor(new Vector4f(0.600f, 0.451f, 0.298f, 1));
        this.setHoverColor(new Vector4f(0.702f, 0.553f, 0.400f, 1));
    }
}
