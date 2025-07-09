package fr.eliferd.engine.input;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
    private static boolean[] _mouseKeyPressed = new boolean[8];
    private static volatile double posX;
    private static volatile double posY;
    private static volatile double lastPosX;
    private static volatile double lastPosY;

    public static void onClickEvent(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            Mouse._mouseKeyPressed[button] = true;
        } else if (action == GLFW_RELEASE) {
            Mouse._mouseKeyPressed[button] = false;
        }
    }

    public static void onMouseMoveEvent(long window, double xPos, double yPos) {
        Mouse.lastPosX = posX;
        Mouse.lastPosY = posY;
        Mouse.posX = xPos;
        Mouse.posY = yPos;
    }

    public static boolean isButtonPressed(int buttonCode) {
        return buttonCode < Mouse._mouseKeyPressed.length && Mouse._mouseKeyPressed[buttonCode];
    }

    public static void setInputMode(long window, MouseInputMode mim) {
        int glfwInputModeValue = -1;
        switch (mim) {
            case NORMAL:
                glfwInputModeValue = GLFW_CURSOR_NORMAL;
                break;
            case HIDDEN:
                glfwInputModeValue = GLFW_CURSOR_HIDDEN;
                break;
            case DISABLED:
                glfwInputModeValue = GLFW_CURSOR_DISABLED;
                break;
            case CAPTURED:
                glfwInputModeValue = GLFW_CURSOR_CAPTURED;
                break;
        }

        glfwSetInputMode(window, GLFW_CURSOR, glfwInputModeValue);
    }

    public static float getPosX() {
        return (float) Mouse.posX;
    }

    public static float getPosY() {
        return (float) Mouse.posY;
    }

    public static float getDeltaX() {
        return (float) (lastPosX - posX);
    }

    public static float getDeltaY() {
        return (float) (lastPosY - posY);
    }
}
