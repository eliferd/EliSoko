package fr.eliferd.game.guis;

import fr.eliferd.engine.controls.Button;
import fr.eliferd.engine.effects.BlinkEffect;
import fr.eliferd.game.Game;
import fr.eliferd.game.levels.Level;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VictoryGui extends AbstractGui {

    private BlinkEffect _blinkEffect;
    private Button _nextLvlBtn;
    private Button _replayBtn;
    private Button _mainMenuBtn;

    private Level _currentLevel;

    public VictoryGui(Level currentLevel) {
        this._currentLevel = currentLevel;
    }

    @Override
    public void init() {
        this._bgVaoId = glGenVertexArrays();
        this._bgVboId = glGenBuffers();

        this.initButtons();

        this._blinkEffect = new BlinkEffect(3f,
                new Vector2i(0, 0),
                new Vector2i(
                        Game.instance().getWindow().getWidth(),
                        Game.instance().getWindow().getHeight()
                ),
                new Vector4f(0, 0, 0, 1));

        this._blinkEffect.setVaoID(glGenVertexArrays());
        this._blinkEffect.start();


    }

    @Override
    public void update(float dt) {
        this.drawBackgroundLayer("assets/textures/wall2.png", 20);
        Game.instance().getFontRenderer().setFontColors(new Vector4f(1.0f, 0.9f, 0.1f, 1.0f));
        int posX = (Game.instance().getWindow().getWidth() / 2);
        Game.instance().getFontRenderer().drawText("YOU WIN", posX - (posX * 0.27f), 500, 8f);
        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 1));
        Game.instance().getFontRenderer().drawText("Your score : ", posX - (posX * 0.27f), 400, 3f);

        _nextLvlBtn.update();
        _replayBtn.update();
        _mainMenuBtn.update();

        if (this._blinkEffect.hasStarted()) {
            this._blinkEffect.update(dt);
        }
    }

    private void initButtons() {
        Vector2i btnSize = new Vector2i(400, 50);
        int windowWidth = Game.instance().getWindow().getWidth();
        int posX = (windowWidth - btnSize.x) / 2;

        this._nextLvlBtn = new Button();
        this._nextLvlBtn.setPos(new Vector2i(posX, 300));
        this._nextLvlBtn.setSize(btnSize);
        this._nextLvlBtn.setLabel("NEXT");
        this._nextLvlBtn.onClick(() -> Game.instance().getWindow().navigateGui(new LevelGui(this._currentLevel.getId() + 1)));

        this._replayBtn = new Button();
        this._replayBtn.setPos(new Vector2i(posX, 230));
        this._replayBtn.setSize(btnSize);
        this._replayBtn.setLabel("REPLAY");
        this._replayBtn.onClick(() -> {
            if (this._currentLevel != null) {
                this._currentLevel.reset();
                Game.instance().getWindow().navigateGui(new LevelGui(this._currentLevel.getId()));
            }
        });

        this._mainMenuBtn = new Button();
        this._mainMenuBtn.setPos(new Vector2i(posX, 160));
        this._mainMenuBtn.setSize(btnSize);
        this._mainMenuBtn.setLabel("MAIN MENU");
        this._mainMenuBtn.onClick(() -> Game.instance().getWindow().navigateGui(new MainMenuGui(Game.instance().getWindow().getShader())));
    }
}
