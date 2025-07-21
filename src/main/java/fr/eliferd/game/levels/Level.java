package fr.eliferd.game.levels;

import fr.eliferd.game.Game;
import fr.eliferd.game.entities.*;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Level {

    private String[] _levelMap;
    private List<BaseEntity> _entityList = new ArrayList<>();
    private int _id = -1;
    private Player _player;
    private Vector2i _playerSpawnPoint;
    private int _maxScore = 0;
    private int _currentScore = 0;
    private int _tileSize;
    private int _gridOffsetX;
    private int _gridOffsetY;

    public Level(int id, String[] map, int score) {
        this(id, map, score, 64, 4, 0);
    }

    public Level(int id, String[] map, int score, int tileSize, int gridOffsetX, int gridOffsetY) {
        this._id = id;
        this._levelMap = map;
        this._maxScore = score;
        this._currentScore = this._maxScore;
        this._tileSize = tileSize;
        this._gridOffsetX = gridOffsetX;
        this._gridOffsetY = gridOffsetY;
        this.generateGrid();
        Game.instance().setCurrentLevel(this);
    }

    public List<BaseEntity> getEntityList() {
        return this._entityList;
    }

    public Player getPlayer() {
        return this._player;
    }

    public long getMaximumGoalCount() {
        return this._entityList.stream().filter((entity) -> entity.getEntityType() == EntityTypeEnum.GOAL).count();
    }

    public long getReachedGoalCount() {
        return this._entityList.stream().filter((entity) ->
                (entity.getEntityType() == EntityTypeEnum.CRATE)
                && ((Crate)entity).hasReachedGoal()
        ).count();
    }

    public void reset() {
        this._entityList.clear();
        this.generateGrid();
        this.initPlayer();
        this._player.setEntityList(this._entityList);
    }

    public int getCurrentScore() {
        return this._currentScore;
    }

    public int getId() {
        return this._id;
    }

    public void updateScore(int score) {
        this._currentScore = score;
        if (this._currentScore > this._maxScore) {
            this._currentScore = this._maxScore;
        }
        if (this._currentScore < 0) {
            this._currentScore = 0;
        }
    }

    private void generateGrid() {
        for (int y = 0; y < this._levelMap.length; y++) {
            for(int x = 0; x < this._levelMap[y].length(); x++) {
                int posX = (this._tileSize * x) + (this._gridOffsetX * this._tileSize);
                int posY = (this._tileSize * y) + (this._gridOffsetY * this._tileSize);
                char currentTile = this._levelMap[y].charAt(x);
                this.placeEntity(currentTile, posX, posY);
            }
        }
    }

    private void initPlayer() {
        this._player = new Player();
        int playerSpawnX = this._playerSpawnPoint.x;
        int playerSpawnY = this._playerSpawnPoint.y;
        this._player.init(EntityTypeEnum.PLAYER, playerSpawnX, playerSpawnY, "assets/textures/player.png", 3);
        this._entityList.add(_player);
    }

    private void placeEntity(char tileChar, int posX, int posY) {
        BaseEntity _entity = null;
        EntityTypeEnum _type = null;
        String texturePath = "assets/textures/";
        int zIndex = 0;
        switch (tileChar) {
            case '*':
                _type = EntityTypeEnum.WALL;
                texturePath += "wall.png";
                _entity = new Wall();
                break;
            case ' ':
                _type = EntityTypeEnum.GROUND;
                texturePath += "ground2.png";
                _entity = new Ground();
                break;
            case 'G':
                this.placeEntity(' ', posX, posY);
                _type = EntityTypeEnum.GOAL;
                texturePath += "goal.png";
                zIndex = 1;
                _entity = new Goal();
                break;
            case 'C':
                this.placeEntity(' ', posX, posY);
                _type = EntityTypeEnum.CRATE;
                texturePath += "crate.png";
                _entity = new Crate();
                zIndex = 2;
                break;
            case 'P':
                this.placeEntity(' ', posX, posY);
                this._playerSpawnPoint = new Vector2i(posX, posY);
                this.initPlayer();
                this._player.setEntityList(this.getEntityList());
                return;
        }
        _entity.init(_type, posX, posY, texturePath, zIndex);
        this._entityList.add(_entity);
    }
}
