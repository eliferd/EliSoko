package fr.eliferd.engine;

import fr.eliferd.engine.input.Keyboard;
import fr.eliferd.engine.input.Mouse;
import fr.eliferd.engine.renderer.Camera;
import fr.eliferd.engine.renderer.Shader;
import fr.eliferd.engine.utils.Logger;
import fr.eliferd.engine.utils.LoggerLevel;
import fr.eliferd.engine.utils.OpenGLDebugLayer;
import fr.eliferd.game.Game;
import fr.eliferd.game.guis.AbstractGui;
import fr.eliferd.game.guis.LevelGui;
import fr.eliferd.game.guis.MainMenuGui;
import fr.eliferd.game.guis.VictoryGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.Map;

import static java.util.Map.entry;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final String _title = "Elisoko - A fanmade Sokoban";
    private final int _width = 1280;
    private final int _height = 720;
    private final Map<Integer, Integer> _windowSettingsDictionnary = Map.ofEntries(
            entry(GLFW_VISIBLE, GLFW_FALSE), // Hiding the window while completing its setup
            entry(GLFW_RESIZABLE, GLFW_FALSE), // Do not allow custom resizing
            entry(GLFW_CONTEXT_VERSION_MAJOR, 4), // This project requires OpenGL 4.4
            entry(GLFW_CONTEXT_VERSION_MINOR, 4), // With modern Core profile mode
            entry(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE),
            entry(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE) // Enabling debuging context.
    );
    private long _window;
    private AbstractGui _currentGui = null;
    private Shader _shader = null;
    private Camera _camera = null;

    public int getWidth() {
        return this._width;
    }

    public int getHeight() {
        return this._height;
    }

    private void init() {
        Game.instance().setWindow(this);

        Logger.print("Setting up the window...", LoggerLevel.INFO);

        // Log any error in the console
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW lib
        if (!glfwInit()) {
            throw new IllegalStateException("GLFW failed to init.");
        }

        // Dynamically applying window hints
        this._windowSettingsDictionnary.forEach(GLFW::glfwWindowHint);

        this._window = glfwCreateWindow(this._width, this._height, this._title, NULL, NULL);

        if (this._window == NULL) {
            glfwTerminate();
            throw new RuntimeException("GLFW failed to create a window.");
        }

        Logger.print("Window setup complete.", LoggerLevel.INFO);

        this.registerInputCallbacks();

        this.centerWindow();

        glfwMakeContextCurrent(this._window);

        // Enabling V-Sync
        // TODO Add a setting to the game in order to disable it.
        glfwSwapInterval(1);

        glfwShowWindow(this._window);

        // Setting the crucial LWJGL line
        GL.createCapabilities();

        // Managing debug
        OpenGLDebugLayer.register();

        // Managing alpha bit
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Version printing
        Logger.print("GLFW : " + glfwGetVersionString(), LoggerLevel.INFO);
        Logger.print("OpenGL : " + glGetString(GL_VERSION), LoggerLevel.INFO);

        // Init shader class and compile shaders
        this._shader = new Shader();
        this._shader.compile();

        // Creating the camera
        this._camera = new Camera(this._width, this._height);

        // Display the main menu
        this.navigateGui(new MainMenuGui(this._shader));
    }

    private void loop() {
        glClearColor(0f, 0f, 0f, 1.0f);
        glViewport(0, 0, this._width, this._height);

        float beginUpdateTime = (float) glfwGetTime();
        float endUpdateTime;
        float dt = -1f;

        while (!glfwWindowShouldClose(this._window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            this._shader.use();
            this._shader.uploadUniform4fv("uViewProj", this._camera.getViewProjection());

            if (dt >= 0) {
                this._currentGui.update(dt);
            }

            glfwPollEvents();
            glfwSwapBuffers(this._window);

            endUpdateTime = (float) glfwGetTime();
            dt = endUpdateTime - beginUpdateTime;
            beginUpdateTime = (float) glfwGetTime();

            int error = glGetError();
            if (error != GL_NO_ERROR) {
                Logger.print("OpenGL : " + error, LoggerLevel.ERROR);
            }

            this._shader.detach();
        }
    }

    public void terminate() {
        glfwFreeCallbacks(this._window);
        glfwDestroyWindow(this._window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * Gui navigator
     *
     * @param gui
     */
    public AbstractGui navigateGui(AbstractGui gui) {
        this._currentGui = gui;
        this._currentGui.init();
        return this._currentGui;
    }

    public Shader getShader() {
        return this._shader;
    }

    /**
     * Centering the window's position
     */
    private void centerWindow() {
        // Retrieving the current window size
        int[] currentWindowWidth = {0};
        int[] currentWindowHeight = {0};
        glfwGetWindowSize(this._window, currentWindowWidth, currentWindowHeight);

        // And the monitor's current size
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        int windowPosX = (vidmode.width() - currentWindowWidth[0]) / 2;
        int windowPosY = (vidmode.height() - currentWindowHeight[0]) / 2;
        glfwSetWindowPos(this._window, windowPosX, windowPosY);
    }

    /**
     * Adding key and mouse callbacks
     */
    private void registerInputCallbacks() {
        glfwSetKeyCallback(this._window, Keyboard::onKeyEvent);
        glfwSetMouseButtonCallback(this._window, Mouse::onClickEvent);
        glfwSetCursorPosCallback(this._window, Mouse::onMouseMoveEvent);
    }

    /**
     * Main entry point to show the window.
     */
    public void show() {
        this.init();
        this.loop();
        this.terminate();
    }

    public long currentGlfwWindowContext() {
        return this._window;
    }
}
