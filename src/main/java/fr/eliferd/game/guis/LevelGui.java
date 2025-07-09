package fr.eliferd.game.guis;

import fr.eliferd.engine.controls.Button;
import fr.eliferd.engine.effects.BlinkEffect;
import fr.eliferd.engine.input.Action;
import fr.eliferd.engine.input.Keyboard;
import fr.eliferd.engine.renderer.Render;
import fr.eliferd.engine.renderer.Shader;
import fr.eliferd.engine.utils.FPSCounter;
import fr.eliferd.game.Game;
import fr.eliferd.game.levels.Level;
import fr.eliferd.game.levels.LevelLoader;
import org.joml.Vector2i;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelGui extends AbstractGui {
    private Render _renderer;
    private Shader _shader;
    private BlinkEffect _blinkEffect;
    private Level _level;
    private Button _resetBtn;
    private PauseMenuGui _pauseGui;
    private FPSCounter _fpsCounter;

    public LevelGui(int levelId) {
        super();
        this.setLevel(levelId, 10000);
        this._shader = Game.instance().getWindow().getShader();
        this._fpsCounter = new FPSCounter();
    }

    public void setLevel(int levelId, int score) {
        this._level = new Level(levelId, LevelLoader.loadLevel(levelId), score);
    }

    @Override
    public void init() {
        this._renderer = new Render();
        this._renderer.init(this._level.getEntityList(), this._shader);

        int width = Game.instance().getWindow().getWidth();
        int height = Game.instance().getWindow().getHeight();
        this._blinkEffect = new BlinkEffect(0.5f, new Vector2i(0, 0), new Vector2i(width, height), new Vector4f(1f, 1f, 1f, 1f));
        this._blinkEffect.setVaoID(glGenVertexArrays()); // Using the render's VAO
        this._blinkEffect.start();

        this._bgVaoId = glGenVertexArrays();
        this._bgVboId = glGenBuffers();

        this._resetBtn = new Button();
        this._resetBtn.setLabel("RESET");
        this._resetBtn.setSize(new Vector2i(130, 50));
        this._resetBtn.setPos(new Vector2i(20, 530));
        this._resetBtn.onClick(() -> {
            this._level.reset();
            this._renderer.setEntityList(this._level.getEntityList());
            this._blinkEffect.start();
        });
    }

    @Override
    public void update(float dt) {
        this.drawBackgroundLayer("assets/textures/ground2.png", 15f);
        this._level.getEntityList().forEach((entity) -> entity.update(dt));
        this._renderer.update(dt);
        this._fpsCounter.update();

        this.drawLevelInformations();

        this._resetBtn.update();

        if (this._blinkEffect.hasStarted()) {
            this._blinkEffect.update(dt);
        }

        this.handlePause(dt);
    }

    private void handlePause(float dt) {
        if (Game.instance().isPaused()) {
            this._pauseGui.update(dt);
        }

        if (Keyboard.getAction() == Action.PAUSE && !Game.instance().isPaused()) {
            this._pauseGui = new PauseMenuGui();
            this._pauseGui.init();
            this._resetBtn.setDisabled(true);
        }

        if (this._pauseGui != null && !Game.instance().isPaused()) {
            this._pauseGui = null;
            this._resetBtn.setDisabled(false);
        }
    }

    public void drawLevelInformations() {
        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 1));
        Game.instance().getFontRenderer().drawText("Reached goals : " +this._level.getReachedGoalCount() + "/" + this._level.getMaximumGoalCount(), 20, 680, 2.0f);
        Game.instance().getFontRenderer().drawText("Move count : " + this._level.getPlayer().getRecordedMovementList().size(), 20, 660, 2.0f);
        Game.instance().getFontRenderer().drawText("FPS : " + this._fpsCounter.getFPS(), 20, 620, 2.0f);

        Game.instance().getFontRenderer().drawText("Controls :", 1040, 680, 2.0f);
        Game.instance().getFontRenderer().drawText("Move up : " + Keyboard.getKeyNameFromAction(Action.MOVE_UP), 1040, 640, 2.0f);
        Game.instance().getFontRenderer().drawText("Move left : " + Keyboard.getKeyNameFromAction(Action.MOVE_LEFT), 1040, 620, 2.0f);
        Game.instance().getFontRenderer().drawText("Move down : " + Keyboard.getKeyNameFromAction(Action.MOVE_DOWN), 1040, 600, 2.0f);
        Game.instance().getFontRenderer().drawText("Move right : " + Keyboard.getKeyNameFromAction(Action.MOVE_RIGHT), 1040, 580, 2.0f);
        Game.instance().getFontRenderer().drawText("Pause : " + Keyboard.getKeyNameFromAction(Action.PAUSE), 1040, 560, 2.0f);
    }
}
