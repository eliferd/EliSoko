package fr.eliferd.game.guis;
import fr.eliferd.engine.controls.Button;
import fr.eliferd.game.Game;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static fr.eliferd.engine.utils.RenderUtils.*;
import static fr.eliferd.engine.utils.RenderUtils.COLOR_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.POS_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class PauseMenuGui extends AbstractGui{

    private Button _resumeBtn;
    private Button _exitBtn;

    @Override
    public void init() {
        Game.instance().setPaused(true);
        this._bgVaoId = glGenVertexArrays();
        this._bgVboId = glGenBuffers();

        this._resumeBtn = new Button();
        final int width = 410;
        this._resumeBtn.setSize(new Vector2i(width, 50));
        this._resumeBtn.setPos(new Vector2i((Game.instance().getWindow().getWidth() / 2)-(width / 2), 400));
        this._resumeBtn.setLabel("RESUME");
        this._resumeBtn.onClick(() -> Game.instance().setPaused(false));

        this._exitBtn = new Button();
        this._exitBtn.setSize(new Vector2i(width, 50));
        this._exitBtn.setPos(new Vector2i((Game.instance().getWindow().getWidth() / 2)-(width / 2), 300));
        this._exitBtn.setLabel("MAIN MENU");
        this._exitBtn.onClick(() -> {
            Game.instance().getWindow().navigateGui(new MainMenuGui(Game.instance().getWindow().getShader()), true);
            Game.instance().setPaused(false);
        });
    }

    @Override
    public void update(float dt) {
        this.drawHalfOpaqueBackground();

        Game.instance().getFontRenderer().drawText("Pause", (Game.instance().getWindow().getWidth() / 2)-120, 500, 8f);

        this._resumeBtn.update();
        this._exitBtn.update();
    }

    private void drawHalfOpaqueBackground() {
        glBindVertexArray(this._bgVaoId);
        glBindBuffer(GL_ARRAY_BUFFER, this._bgVboId);

        final float[] colors = new float[]{0, 0, 0, 0.5f};

        final float[] vertices = new float[] {
                0, 0,                                                       colors[0], colors[1], colors[2], colors[3],
                0, this._currentWindowHeight[0],                            colors[0], colors[1], colors[2], colors[3],
                this._currentWindowWidth[0], this._currentWindowHeight[0],  colors[0], colors[1], colors[2], colors[3],

                this._currentWindowWidth[0], this._currentWindowHeight[0],  colors[0], colors[1], colors[2], colors[3],
                this._currentWindowWidth[0], 0,                             colors[0], colors[1], colors[2], colors[3],
                0, 0,                                                       colors[0], colors[1], colors[2], colors[3],
        };

        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length);
        fb.put(vertices).flip();

        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VBO_PC_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VBO_PC_SIZE * Float.BYTES, (POS_SIZE * Float.BYTES));
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        Game.instance().getWindow().getShader().uploadUniform1i("uHasTexture", 0);
        Game.instance().getWindow().getShader().uploadUniform1i("uUseLights", 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }
}
