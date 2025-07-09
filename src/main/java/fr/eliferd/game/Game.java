package fr.eliferd.game;

import fr.eliferd.engine.Window;
import fr.eliferd.engine.renderer.font.FontRenderer;
import fr.eliferd.game.guis.VictoryGui;
import fr.eliferd.game.levels.Level;

public class Game {
    private static Game _INSTANCE = null;
    private static int MAX_SCORE = 10000;
    private Window _window;
    private Level _lvl;
    private boolean _hasWon = false;
    private boolean _isPaused = false;
    private FontRenderer _fontRenderer;
    private int currentScore = 0;
    public void setWindow(Window window) {
        this._window = window;
    }

    public void setCurrentLevel(Level lvl) {
        this._lvl = lvl;
    }

    public void setPaused(boolean isPaused) {
        this._isPaused = isPaused;
    }

    public Level getCurrentLevel() {
        return this._lvl;
    }

    public FontRenderer getFontRenderer() {
        if (this._fontRenderer == null) {
            this._fontRenderer = new FontRenderer();
        }
        return this._fontRenderer;
    }

    public boolean isInGame() {
        return this._lvl != null;
    }

    public boolean isPaused() {
        return this._isPaused;
    }

    public void handleProgress() {
        this._hasWon = this.isInGame() && this._lvl.getReachedGoalCount() >= this._lvl.getMaximumGoalCount();
        if (_hasWon) {
            this._window.navigateGui(new VictoryGui(this.getCurrentLevel()));
            this.setCurrentLevel(null);
        }
    }

    public Window getWindow() {
        return this._window;
    }

    public static Game instance() {
        if(_INSTANCE == null) {
            _INSTANCE = new Game();
        }
        return _INSTANCE;
    }
}
