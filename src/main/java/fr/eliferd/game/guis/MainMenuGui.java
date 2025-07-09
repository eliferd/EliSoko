package fr.eliferd.game.guis;

import fr.eliferd.engine.controls.Button;
import fr.eliferd.engine.effects.BlinkEffect;
import fr.eliferd.engine.renderer.Render;
import fr.eliferd.engine.renderer.Shader;
import fr.eliferd.game.Game;
import fr.eliferd.game.entities.*;
import org.joml.Vector2i;
import org.joml.Vector4f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class MainMenuGui extends AbstractGui{
    private Render _renderer;
    private Shader _shader;
    private List<BaseEntity> _levelEntityList = new ArrayList<>();
    private BlinkEffect _blinkEffect;

    private String[] menuSplashText = new String[] {
            "************************************",
            "*                                  *",
            "* CCCC C    CCC  CCC  CC  C C  CC  *",
            "* C    C     C  C    C  C C C C  C *",
            "* CCCC C     C   CC  C  C CC  C  C *",
            "* C    C     C     C C  C C C C  C *",
            "* CCCC CCCC CCC CCC   CC  C C  CC  *",
            "*                                  *",
            "************************************"
    };

    private Button _playBtn;
    private Button _selectLvlBtn;
    private Button _exitBtn;

    public MainMenuGui(Shader shader) {
        super();
        this._shader = shader;
        this._blinkEffect = new BlinkEffect(
                1,
                new Vector2i(0, 0),
                new Vector2i(_currentWindowWidth[0], _currentWindowHeight[0]),
                new Vector4f(0.545f, 0.369f, 0.180f, 1.0f));
    }
    @Override
    public void init() {
        this.loadSplashLogo();
        this._blinkEffect.setVaoID(this._renderer.getVaoId());
        this._blinkEffect.start();
        this._bgVaoId = glGenVertexArrays();
        this._bgVboId = glGenBuffers();

        this.initMenuButtons();
    }

    @Override
    public void update(float dt) {
        this.drawBackgroundLayer("assets/textures/ground.png", 20f);
        this._renderer.update(dt);

        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 1f));
        Game.instance().getFontRenderer().drawText("Welcome to EliSoko", 500, 400, 3.0f);

        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 0.3f));
        Game.instance().getFontRenderer().drawText("Another fanmade of the original SOKOBAN by Hiroyuki Imabayashi", 290, 370, 2.0f);

        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 0.3f));
        Game.instance().getFontRenderer().drawText("by Eliferd", 20, 20, 1.5f);


        if (this._blinkEffect.hasStarted()) {
            this._blinkEffect.update(dt);
        }

        this._playBtn.update();
        this._selectLvlBtn.update();
        this._exitBtn.update();
    }

    private void placeEntity(char tileChar, int posX, int posY) {
        BaseEntity _entity = null;
        EntityTypeEnum _type = null;
        String texturePath = "assets/textures/";
        int zIndex = 0;
        switch (tileChar) {
            case '*':
                _type = EntityTypeEnum.WALL;
                texturePath += "wall4.png";
                _entity = new Wall();
                break;
            case ' ':
                _type = EntityTypeEnum.GROUND;
                texturePath += "ground.png";
                _entity = new Ground();
                break;
            case 'G':
                this.placeEntity(' ', posX, posY);
                _type = EntityTypeEnum.GOAL;
                texturePath += "goal2.png";
                zIndex = 1;
                _entity = new Goal();
                break;
            case 'C':
                this.placeEntity(' ', posX, posY);
                _type = EntityTypeEnum.CRATE;
                texturePath += "crate2.png";
                _entity = new Crate();
                zIndex = 2;
                break;
        }
        _entity.init(_type, posX, posY, texturePath, zIndex);
        this._levelEntityList.add(_entity);
    }

    private void initMenuButtons() {
        this._playBtn = new Button();
        this._playBtn.setSize(new Vector2i(250, 40));
        this._playBtn.setLabel("PLAY");
        this._playBtn.setPos(new Vector2i(520, 240));
        this._playBtn.onClick(() -> {
            Game.instance().getWindow().navigateGui(new LevelGui(1));
        });

        this._selectLvlBtn = new Button();
        this._selectLvlBtn.setSize(new Vector2i(250, 40));
        this._selectLvlBtn.setLabel("LEVELS");
        this._selectLvlBtn.setPos(new Vector2i(520, 180));

        this._exitBtn = new Button();
        this._exitBtn.setSize(new Vector2i(250, 40));
        this._exitBtn.setLabel("EXIT");
        this._exitBtn.setPos(new Vector2i(520, 120));
        this._exitBtn.onClick(() ->  glfwSetWindowShouldClose(Game.instance().getWindow().currentGlfwWindowContext(), true));
    }

    private void loadSplashLogo() {
        Collections.reverse(Arrays.asList(this.menuSplashText));

        final int textureSize = 20;

        int logoPosX = textureSize * 14;
        int logoPosY = textureSize * 24;

        for (int y = 0; y < this.menuSplashText.length; y++) {
            for(int x = 0; x < this.menuSplashText[y].length(); x++) {
                int posX = (textureSize * x) + logoPosX;
                int posY = (textureSize * y) + logoPosY;
                char currentTile = this.menuSplashText[y].charAt(x);
                this.placeEntity(currentTile, posX, posY);
            }
        }

        this._renderer = new Render();
        this._renderer.init(this._levelEntityList, this._shader);
        this._renderer.setTextureSize(textureSize);
    }
}
