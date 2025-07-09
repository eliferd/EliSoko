package fr.eliferd.engine.renderer;

import fr.eliferd.engine.utils.Logger;
import fr.eliferd.engine.utils.LoggerLevel;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static  org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderProgramId;
    private int vShaderId;
    private int fShaderId;

    public Shader() {
        this.vShaderId = glCreateShader(GL_VERTEX_SHADER);
        this.fShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        this.shaderProgramId = glCreateProgram();
    }

    private void compileShader(int shaderId, String shaderPath) {
        try {
            String src = new String(Files.readAllBytes(Paths.get(shaderPath)));

            glShaderSource(shaderId, src);
            glCompileShader(shaderId);

            // Checking for any compilation errors
            int compileStatus = glGetShaderi(shaderId, GL_COMPILE_STATUS);
            if (compileStatus == GL_FALSE) {
                Logger.print("Shader compilation failure :", LoggerLevel.ERROR);
                Logger.print(glGetShaderInfoLog(shaderId), LoggerLevel.ERROR);
                glDeleteShader(shaderId);
            }

            // Finally, attaching the shader to the program
            glAttachShader(this.shaderProgramId, shaderId);
        } catch (IOException e) {
            Logger.print("Shader file reading error : " + e.getMessage(), LoggerLevel.ERROR);
            e.printStackTrace();
        }
    }

    private void compileProgram() {
        glLinkProgram(this.shaderProgramId);

        // Checking for shader program linking errors
        int linkStatus = glGetProgrami(this.shaderProgramId, GL_LINK_STATUS);
        if (linkStatus == GL_FALSE) {
            Logger.print("Shader program failed to link : ", LoggerLevel.ERROR);
            Logger.print(glGetProgramInfoLog(this.shaderProgramId), LoggerLevel.ERROR);

            glDeleteProgram(this.shaderProgramId);
        }

        glDetachShader(this.shaderProgramId, vShaderId);
        glDetachShader(this.shaderProgramId, fShaderId);
    }

    public void compile() {
        this.compileShader(vShaderId, "assets/shaders/vertex.glsl");
        this.compileShader(fShaderId, "assets/shaders/fragment.glsl");
        this.compileProgram();
        glValidateProgram(this.shaderProgramId);
    }

    public void use() {
        glUseProgram(this.shaderProgramId);
    }

    public void detach() {
        glUseProgram(0);
    }

    public void uploadUniform4fv(String uniformName, Matrix4f matrix) {
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);
        glUniformMatrix4fv(glGetUniformLocation(this.shaderProgramId, uniformName), false, matrixBuffer);
    }

    public void uploadUniform2f(String uniformName, Vector2f vec) {
        glUniform2f(glGetUniformLocation(this.shaderProgramId, uniformName), vec.x, vec.y);
    }
    public void uploadUniform3f(String uniformName, Vector3f vec) {
        glUniform3f(glGetUniformLocation(this.shaderProgramId, uniformName), vec.x, vec.y, vec.z);
    }

    public void uploadUniform1i(String uniformName, int i) {
        glUniform1i(glGetUniformLocation(this.shaderProgramId, uniformName), i);
    }

    public void uploadUniform1f(String uniformName, float f) {
        glUniform1f(glGetUniformLocation(this.shaderProgramId, uniformName), f);
    }
}