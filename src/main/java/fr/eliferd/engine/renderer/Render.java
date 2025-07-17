package fr.eliferd.engine.renderer;

import fr.eliferd.engine.ResourceManager;
import fr.eliferd.game.Game;
import fr.eliferd.game.entities.BaseEntity;
import fr.eliferd.game.entities.EntityTypeEnum;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.*;

import static fr.eliferd.engine.utils.RenderUtils.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Render {
    private List<BaseEntity> _entityList;
    private int vaoID = -1;
    private Map<Integer, Integer> vboEntityIdDictionnary = new HashMap<>();
    private Shader _shader = null;
    private int _textureSize = TILE_SIZE;
    private boolean _disableTextures = false;
    private boolean _useLights = false;

    public void init(List<BaseEntity> entityList, Shader shader) {
        this.init(entityList, shader, false);
    }

    public void init(List<BaseEntity> entityList, Shader shader, boolean disableTextures) {
        this._shader = shader;
        this.setEntityList(entityList);
        this._disableTextures = disableTextures;
    }

    public void setTextureSize(int size) {
        this._textureSize = size;
    }

    public void setEntityList(List<BaseEntity> entityList) {
        if (entityList.isEmpty()) {
            return;
        }

        this.vboEntityIdDictionnary.clear();
        this._entityList = entityList;
        this._entityList.sort(Comparator.comparing(BaseEntity::getZIndex));

        if (this.vaoID == -1) {
            this.vaoID = glGenVertexArrays();
        }

        this.generateVBOs();
    }

    public void useLight(boolean value) {
        this._useLights = value;
    }

    public void update(float dt) {
        glBindVertexArray(this.vaoID);

        this.drawEntities();

        glBindVertexArray(0);
    }

    public int getVaoId() {
        return this.vaoID;
    }

    private void drawEntities() {
        this._entityList.forEach((entity) -> {
            glBindBuffer(GL_ARRAY_BUFFER, this.vboEntityIdDictionnary.get(entity.getEntityId()));

            final float[] vertices = this.genereateVertices(entity);

            FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length);
            fb.put(vertices).flip();
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
            int stride = (this._disableTextures ? VBO_PC_SIZE : VBO_PCT_SIZE) * Float.BYTES;
            glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, stride, 0);
            glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, stride, (POS_SIZE * Float.BYTES));
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            if (!this._disableTextures) {
                glVertexAttribPointer(2, TEXCOORDS_SIZE, GL_FLOAT, false, stride, ((POS_SIZE + COLOR_SIZE) * Float.BYTES));
                glEnableVertexAttribArray(2);
            }

            Texture tex = null;

            if (!this._disableTextures) {
                tex = ResourceManager.getTexture(entity.getTexturePath());
            }

            this._shader.uploadUniform1i("uTex", 0);
            this._shader.uploadUniform1i("uHasTexture", this._disableTextures ? 0 : 1);

            this._shader.uploadUniform1i("uUseLights", this._useLights ? 1 : 0);

            if (this._useLights) {
                this._shader.uploadUniform2f("uLightPos", new Vector2f(entity.getPosX() / Game.instance().getWindow().getWidth() + 0.5f, entity.getPosY() / Game.instance().getWindow().getHeight()));
                this._shader.uploadUniform3f("uLightColor", new Vector3f(1, 1, 1));
                this._shader.uploadUniform1f("uLightRadius", 1.4f);
                this._shader.uploadUniform1f("uAmbient", 0.4f);
            }

            if (tex != null) {
                tex.bind();
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            if (!this._disableTextures)
                glEnableVertexAttribArray(2);

            glDrawArrays(GL_TRIANGLES, 0, 6);
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);

            if (!this._disableTextures)
                glDisableVertexAttribArray(2);
        });
    }

    private void generateVBOs() {
        this._entityList.forEach((entity) -> this.vboEntityIdDictionnary.put(entity.getEntityId(), glGenBuffers()));
    }

    private float[] genereateVertices(BaseEntity entity) {
        float[] colors = new float[] { 1, 1, 1, 1 };

        float[] vertices = new float[]{
            entity.getPosX(), entity.getPosY(),                                         colors[0], colors[1], colors[2], colors[3],        0.0f, 0.0f, // bottom left
            entity.getPosX(), entity.getPosY() + this._textureSize,                     colors[0], colors[1], colors[2], colors[3],        0.0f, 1.0f, // top left
            entity.getPosX() + this._textureSize, entity.getPosY() + this._textureSize, colors[0], colors[1], colors[2], colors[3],        1.0f, 1.0f, // top right

            entity.getPosX() + this._textureSize, entity.getPosY() + this._textureSize, colors[0], colors[1], colors[2], colors[3],        1.0f, 1.0f, // top right
            entity.getPosX() + this._textureSize, entity.getPosY(),                     colors[0], colors[1], colors[2], colors[3],        1.0f, 0.0f, // bottom right
            entity.getPosX(), entity.getPosY(),                                         colors[0], colors[1], colors[2], colors[3],        0.0f, 0.0f, // bottom left
        };

        if (this._disableTextures) {
            colors = new float[] {
                    entity.getMinimapColor().x,
                    entity.getMinimapColor().y,
                    entity.getMinimapColor().z,
                    entity.getMinimapColor().w
            };

            vertices = new float[]{
                entity.getPosX(), entity.getPosY(),                                         colors[0], colors[1], colors[2], colors[3], // bottom left
                entity.getPosX(), entity.getPosY() + this._textureSize,                     colors[0], colors[1], colors[2], colors[3], // top left
                entity.getPosX() + this._textureSize, entity.getPosY() + this._textureSize, colors[0], colors[1], colors[2], colors[3], // top right

                entity.getPosX() + this._textureSize, entity.getPosY() + this._textureSize, colors[0], colors[1], colors[2], colors[3], // top right
                entity.getPosX() + this._textureSize, entity.getPosY(),                     colors[0], colors[1], colors[2], colors[3], // bottom right
                entity.getPosX(), entity.getPosY(),                                         colors[0], colors[1], colors[2], colors[3], // bottom left
            };
        }

        return vertices;
    }
}
