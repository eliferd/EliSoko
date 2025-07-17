package fr.eliferd.game.guis;

import fr.eliferd.engine.controls.Button;
import fr.eliferd.engine.renderer.Render;
import fr.eliferd.game.Game;
import fr.eliferd.game.levels.Level;
import fr.eliferd.game.levels.LevelLoader;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelSelectorGui extends AbstractGui {

    private List<Button> _levelBtnList = new ArrayList<>();
    private Button _mainMenuBtn = null;
    private Level _hoveredLevel = null;
    private Render _minimapRenderer = null;

    public LevelSelectorGui() {
        super();
    }

    @Override
    public void init() {
        this._bgVaoId = glGenVertexArrays();
        this._bgVboId = glGenBuffers();
        this._minimapRenderer = new Render();
        this._minimapRenderer.init(List.of(), Game.instance().getWindow().getShader(), true);
        this._minimapRenderer.setTextureSize(32);

        for (int i = 0; i < LevelLoader.levelCount(); i++) {
            int levelId = i+1;
            Button btn = new Button();
            btn.setLabel(String.valueOf(levelId));
            btn.setSize(new Vector2i(80, 80));
            btn.setPos(new Vector2i(150 + (i * 100), 450));
            btn.onHover(() -> {
                this._hoveredLevel = new Level(levelId, LevelLoader.loadLevel(levelId), LevelLoader.getLevelMetadata(levelId).getScore(), 32, 14, 1);
                this._minimapRenderer.setEntityList(this._hoveredLevel.getEntityList());
            });
            btn.onClick(() -> Game.instance().getWindow().navigateGui(new LevelGui(levelId)));
            this._levelBtnList.add(btn);
        }

        this._mainMenuBtn = new Button();
        String mainMenuBtnLabel = "MAIN MENU";
        this._mainMenuBtn.setLabel(mainMenuBtnLabel);
        this._mainMenuBtn.setSize(new Vector2i(250, 50));
        this._mainMenuBtn.setPos(new Vector2i(this.getCenterPosX() - 125, 200));
        this._mainMenuBtn.onClick(() -> Game.instance().getWindow().navigateGui(new MainMenuGui(Game.instance().getWindow().getShader())));
    }

    @Override
    public void update(float dt) {
        this.drawBackgroundLayer("assets/textures/crate2.png", 20);
        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 1));
        Game.instance().getFontRenderer().drawText("SELECT LEVEL", this.getCenterPosX() - (this.getCenterPosX() * 0.24f), 600, 5f);
        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 0.5f));
        Game.instance().getFontRenderer().drawText("Tip : Hover the buttons to preview the level", this.getCenterPosX() - (this.getCenterPosX() * 0.54f), 550, 3f);
        Game.instance().getFontRenderer().setFontColors(new Vector4f(1, 1, 1, 1));

        this._levelBtnList.forEach(Button::update);

        if (this._hoveredLevel != null) {
            if (this._levelBtnList.stream().noneMatch(Button::isMouseOver)) {
                this._hoveredLevel = null;
            } else {
                this._minimapRenderer.update(dt);
            }
        }

        // displays the main menu button only if we don't hover a level.
        if (this._hoveredLevel == null) {
            this._mainMenuBtn.update();
        }
    }

    private int getCenterPosX() {
        return this._currentWindowWidth[0] / 2;
    }
}
