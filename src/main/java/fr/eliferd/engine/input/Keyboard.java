package fr.eliferd.engine.input;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Map.entry;
import static org.lwjgl.glfw.GLFW.*;

public class Keyboard {

    private static final Map<Integer, String> _keyNameMap = Map.ofEntries(
            entry(GLFW_KEY_UP, "UP"),
            entry(GLFW_KEY_DOWN, "DOWN"),
            entry(GLFW_KEY_LEFT, "LEFT"),
            entry(GLFW_KEY_RIGHT, "RIGHT"),
            entry(GLFW_KEY_ESCAPE, "ESCAPE")
    );

    private static boolean[] _keyPressed = new boolean[350];

    private static final Map<Action, List<Integer>> _actionKeyMap = Map.ofEntries(
            entry(Action.MOVE_UP,    List.of(new Integer[] {GLFW_KEY_W, GLFW_KEY_UP})),
            entry(Action.MOVE_DOWN,  List.of(new Integer[] {GLFW_KEY_S, GLFW_KEY_DOWN})),
            entry(Action.MOVE_LEFT,  List.of(new Integer[] {GLFW_KEY_A, GLFW_KEY_LEFT})),
            entry(Action.MOVE_RIGHT, List.of(new Integer[] {GLFW_KEY_D, GLFW_KEY_RIGHT})),
            entry(Action.RESET,      List.of(new Integer[] {GLFW_KEY_R})),
            entry(Action.PAUSE,      List.of(new Integer[] {GLFW_KEY_ESCAPE}))
    );

    public static void onKeyEvent(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            Keyboard._keyPressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            Keyboard._keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keycode) {
        return keycode < Keyboard._keyPressed.length && Keyboard._keyPressed[keycode];
    }

    /**
     * Predicate which key has been pressed and return its game action's enum
     * @return The performed action or null if no key of the dictionary was pressed
     */
    public static Action getAction() {
        try {
            return Keyboard._actionKeyMap.entrySet().stream().filter(
                    (entry) -> entry.getValue().stream().filter(Keyboard::isKeyPressed).count() > 0
            ).findFirst().get().getKey();
        } catch (NoSuchElementException e) {
            return null;
        }

    }

    public static String getKeyNameFromAction(Action action) {
        return Keyboard._actionKeyMap.get(action).stream().map(
                (keycode) -> Optional.ofNullable(glfwGetKeyName(keycode, glfwGetKeyScancode(keycode))).orElse(Keyboard._keyNameMap.get(keycode))
        ).toList().toString().replaceAll("[\\[\\]]", "").toUpperCase();
    }
}
