package fr.eliferd.game.entities;

public abstract class BaseEntity {
    private int _positionX;
    private int _positionY;
    private String _texturePath;
    private static int _entityId = 0;
    protected final int currentEntityId;
    private EntityTypeEnum _type;
    private int _zIndex;

    public BaseEntity() {
        this.currentEntityId = ++_entityId;
    }

    public void init(EntityTypeEnum type, int posX, int posY, String texturePath, int zIndex) {
        this._type = type;
        this._positionX = posX;
        this._positionY = posY;
        this._texturePath = texturePath;
        this._zIndex = zIndex;
    }

    public abstract void update(float dt);

    public void setPosition(int posX, int posY) {
        this._positionX = posX;
        this._positionY = posY;
    }

    public void setTexture(String texturePath) {
        this._texturePath = texturePath;
    }

    public String getTexturePath() {
        return this._texturePath;
    }

    public int getPosX() {
        return this._positionX;
    }

    public int getPosY() {
        return this._positionY;
    }

    public int getEntityId() {
        return this.currentEntityId;
    }

    public EntityTypeEnum getEntityType() {
        return this._type;
    }

    public int getZIndex() {
        return this._zIndex;
    }
}
