package fr.eliferd.engine.renderer.font;

import fr.eliferd.engine.renderer.Texture;
import fr.eliferd.game.Game;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.*;

import static fr.eliferd.engine.utils.RenderUtils.COLOR_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.POS_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.TEXCOORDS_SIZE;
import static fr.eliferd.engine.utils.RenderUtils.VBO_PCT_SIZE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class FontRenderer {
    private BitmapFont font;
    private int _vaoId = -1;
    private int _vboId = -1;
    private Vector4f _colors;

    public FontRenderer() {
        this.font = new BitmapFont("assets/fonts/minogram_6x10.png", "assets/fonts/minogram_6x10.xml");
        this.setFontColors(new Vector4f(1, 1, 1, 1));
        this._vaoId = glGenVertexArrays();
        this._vboId = glGenBuffers();
    }

    public void setFontColors(Vector4f colors) {
        this._colors = colors;
    }

    public Vector2i getTextSize(String text, float scale) {
        List<Glyph> glyphList = text.chars().mapToObj((c) -> this.font.getGlyph((char)c)).filter(Objects::nonNull).toList();
        int sizeX = glyphList.stream().map((g) -> (int)(g.width * scale)).reduce(0, Integer::sum);
        int sizeY = glyphList.stream().map((g) -> (int)(g.height * scale)).max(Integer::compare).orElse(0);
        return new Vector2i(sizeX, sizeY);
    }

    public void drawText(String text, float startX, float startY, float scale) {
        float x = startX;
        for (char c : text.toCharArray()) {
            Glyph glyph = this.font.getGlyph(c);
            if (glyph == null) continue;

            float gx = x + glyph.xOffset * scale;
            float gy = startY + glyph.yOffset * scale;
            float gw = glyph.width * scale;
            float gh = glyph.height * scale;

            this.draw(
                    font.getTexture(),
                    gx, gy,
                    gw, gh,
                    glyph.x, glyph.y,
                    glyph.width, glyph.height
            );

            x += glyph.xAdvance * scale;
        }
    }

    private void draw(Texture texture, float x, float y, float width, float height, int texX, int texY, int texW, int texH) {
        float texWidth = texture.getWidth();
        float texHeight = texture.getHeight();
        float invertedY = texHeight - texY - texH;

        float u0 = texX / texWidth;
        float v0 = (invertedY + texH) / texHeight;
        float u1 = (texX + texW) / texWidth;
        float v1 = invertedY / texHeight;

        float r = this._colors.x, g = this._colors.y, b = this._colors.z, a = this._colors.w;

        float[] vertices = {
                // Pos         // Colors          // TexCoords
                x, y,           r, g, b, a,         u0, v1,
                x + width, y,   r, g, b, a,         u1, v1,
                x + width, y + height, r, g, b, a,  u1, v0,

                x + width, y + height, r, g, b, a,  u1, v0,
                x, y + height, r, g, b, a,          u0, v0,
                x, y,           r, g, b, a,         u0, v1
        };

        glBindVertexArray(this._vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, this._vboId);

        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length);
        fb.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VBO_PCT_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VBO_PCT_SIZE * Float.BYTES, (POS_SIZE * Float.BYTES));
        glVertexAttribPointer(2, TEXCOORDS_SIZE, GL_FLOAT, false, VBO_PCT_SIZE * Float.BYTES, ((POS_SIZE + COLOR_SIZE) * Float.BYTES));
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        Game.instance().getWindow().getShader().uploadUniform1i("uTex", 0);
        Game.instance().getWindow().getShader().uploadUniform1i("uHasTexture", 1);
        Game.instance().getWindow().getShader().uploadUniform1i("uUseLights", 0);
        texture.bind();

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
