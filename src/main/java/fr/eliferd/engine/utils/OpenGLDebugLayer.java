package fr.eliferd.engine.utils;

import org.lwjgl.opengl.GLDebugMessageCallback;

import static org.lwjgl.opengl.GL43.*;

public class OpenGLDebugLayer {
    public static void register() {
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback(GLDebugMessageCallback.create(OpenGLDebugLayer::printDebugLogs), 0);
        glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, (int[]) null, true);
    }

    private static void printDebugLogs(int source, int type, int id, int severity, int length, long message, long userParam) {
        String msg = GLDebugMessageCallback.getMessage(length, message);
        StringBuilder sb = new StringBuilder();

        sb.append("=== OpenGL Debug Message ===");
        sb.append("\nSource  : " + getSource(source));
        sb.append("\nType    : " + getType(type));
        sb.append("\nSeverity: " + getSeverity(severity));
        sb.append("\nID      : " + id);
        sb.append("\nMessage : " + msg);
        sb.append("\n============================\n");

        System.err.println(sb.toString());
    }

    private static String getSource(int source) {
        return switch (source) {
            case GL_DEBUG_SOURCE_API -> "API";
            case GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "Window System";
            case GL_DEBUG_SOURCE_SHADER_COMPILER -> "Shader Compiler";
            case GL_DEBUG_SOURCE_THIRD_PARTY -> "Third Party";
            case GL_DEBUG_SOURCE_APPLICATION -> "Application";
            case GL_DEBUG_SOURCE_OTHER -> "Other";
            default -> "Unknown";
        };
    }

    private static String getType(int type) {
        return switch (type) {
            case GL_DEBUG_TYPE_ERROR -> "Error";
            case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "Deprecated Behavior";
            case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "Undefined Behavior";
            case GL_DEBUG_TYPE_PORTABILITY -> "Portability";
            case GL_DEBUG_TYPE_PERFORMANCE -> "Performance";
            case GL_DEBUG_TYPE_MARKER -> "Marker";
            case GL_DEBUG_TYPE_PUSH_GROUP -> "Push Group";
            case GL_DEBUG_TYPE_POP_GROUP -> "Pop Group";
            case GL_DEBUG_TYPE_OTHER -> "Other";
            default -> "Unknown";
        };
    }

    private static String getSeverity(int severity) {
        return switch (severity) {
            case GL_DEBUG_SEVERITY_HIGH -> "High";
            case GL_DEBUG_SEVERITY_MEDIUM -> "Medium";
            case GL_DEBUG_SEVERITY_LOW -> "Low";
            case GL_DEBUG_SEVERITY_NOTIFICATION -> "Notification";
            default -> "Unknown";
        };
    }
}
