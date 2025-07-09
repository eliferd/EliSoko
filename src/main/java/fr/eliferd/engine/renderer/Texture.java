package fr.eliferd.engine.renderer;

import fr.eliferd.engine.utils.Logger;
import fr.eliferd.engine.utils.LoggerLevel;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String _filepath;
    private int _textureId;
    private int _width, _height;

    public void init(String filepath) {
        this._filepath = filepath;

        int[] width = new int[]{0};
        int[] height = new int[]{0};
        int[] channels = new int[]{0};

        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            stbi_set_flip_vertically_on_load(true);
            this._width = width[0];
            this._height = height[0];

            // Generate texture and bind it
            this._textureId = glGenTextures();
            this.bind();

            // Texture params
            // Repeat image in both ways (x and y) when stretched
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            // Loading image data to memory
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this._width, this._height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

        } else {
            Logger.print("Failed to load image '" + filepath + "'.", LoggerLevel.ERROR);
        }

        stbi_image_free(image);
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this._textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return this._width;
    }

    public int getHeight() {
        return this._height;
    }

    public int getTextureId() {
        return this._textureId;
    }
}
