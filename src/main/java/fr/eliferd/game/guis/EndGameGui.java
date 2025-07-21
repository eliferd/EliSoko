package fr.eliferd.game.guis;

import fr.eliferd.engine.controls.Button;
import fr.eliferd.engine.effects.BlinkEffect;
import fr.eliferd.game.Game;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class EndGameGui extends AbstractGui {

    private BlinkEffect _blinkEffect;
    private Button _mainMenuBtn;

    private int _textDisplayTick = 0;
    private final int _textDisplayTickMax = 350;

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
        if (this._textDisplayTick <= this._textDisplayTickMax) {
            if (this._textDisplayTick > 100) {
                Game.instance().getFontRenderer().drawText("GAME COMPLETE", posX - (posX * 0.47f), 500, 8f);
            }
            if (this._textDisplayTick > 150) {
                Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 1));
                Game.instance().getFontRenderer().drawText("Your final score : ", posX - (posX * 0.37f), 450, 3f);
                Game.instance().getFontRenderer().drawText(String.valueOf(Game.instance().getTotalScore()), (posX - (posX * 0.27f)) + 250, 450, 3f);
            }
            if (this._textDisplayTick > 200) {
                Game.instance().getFontRenderer().drawText("Thank you so much for playing !", posX - (posX * 0.37f), 400, 3f);
            }
            if (this._textDisplayTick > 250) {
                Game.instance().getFontRenderer().drawText("Level selector unlocked !", posX - (posX * 0.37f), 350, 3f);
            }
            if (this._textDisplayTick > 300) {
                Game.instance().getFontRenderer().drawText("GitHub : eliferd/EliSoko", posX - (posX * 0.37f), 400, 3f);
            }

            if (this._textDisplayTick == this._textDisplayTickMax) {
                this._mainMenuBtn.update();
            }
        }

        if (this._textDisplayTick < this._textDisplayTickMax) {
            this._textDisplayTick++;
        }

        if (this._blinkEffect.hasStarted()) {
            this._blinkEffect.update(dt);
        }
    }

    private void initButtons() {
        Vector2i btnSize = new Vector2i(400, 50);
        int windowWidth = Game.instance().getWindow().getWidth();
        int posX = (windowWidth - btnSize.x) / 2;

        this._mainMenuBtn = new Button();
        this._mainMenuBtn.setPos(new Vector2i(posX, 160));
        this._mainMenuBtn.setSize(btnSize);
        this._mainMenuBtn.setLabel("MAIN MENU");
        this._mainMenuBtn.onClick(() -> {
            Game.instance().resetTotalScore();
            Game.instance().getWindow().navigateGui(new MainMenuGui(Game.instance().getWindow().getShader()));
        });
    }
}
