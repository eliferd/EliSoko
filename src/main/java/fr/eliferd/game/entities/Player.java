package fr.eliferd.game.entities;

import fr.eliferd.engine.input.Keyboard;
import fr.eliferd.game.Game;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fr.eliferd.engine.utils.RenderUtils.TILE_SIZE;

public class Player extends BaseEntity {

    private static final int MAX_KEY_COOLDOWN = 10;

    private int _keyCooldown;
    private List<BaseEntity> _worldEntityList = null;
    private boolean _isPushingCrateToWall = false;
    private List<Vector2i> _recordedMovementList = new ArrayList();

    public Player() {
        this._keyCooldown = MAX_KEY_COOLDOWN;
    }

    private Optional<BaseEntity> getEntityAt(int posX, int posY) {
        return this._worldEntityList.stream().filter((entity) ->
                entity.getEntityType() != EntityTypeEnum.GROUND
                && (entity.getPosX() == posX && entity.getPosY() == posY)
        ).findFirst();
    }

    private Optional<BaseEntity> getEntityAt(int posX, int posY, EntityTypeEnum type) {
        return this._worldEntityList.stream().filter((entity) ->
                entity.getEntityType() == type
                && (entity.getPosX() == posX && entity.getPosY() == posY)
        ).findFirst();
    }

    private void movePlayer(int destX, int destY, float dt) {
        destX = destX * TILE_SIZE;
        destY = destY * TILE_SIZE;

        int newPosX = this.getPosX() + destX;
        int newPosY = this.getPosY() + destY;

        Optional<BaseEntity> targetEntity = this.getEntityAt(newPosX, newPosY);
        // Checking if we're not stepping over a wall
        if (targetEntity.isEmpty() || targetEntity.get().getEntityType() != EntityTypeEnum.WALL) {

            Optional<BaseEntity> crate = this.getEntityAt(newPosX, newPosY, EntityTypeEnum.CRATE);
            if (crate.isPresent()) {
                this.updateFoundCrate((Crate) crate.get(), newPosX, newPosY);
            }

            if (!this._isPushingCrateToWall) {
                this.setPosition(newPosX, newPosY);
                this._recordedMovementList.add(new Vector2i(newPosX, newPosY));
                this._keyCooldown = 0;
            } else {
                this._isPushingCrateToWall = false;
            }
        }
    }

    private void updateFoundCrate(Crate crate, int newPosX, int newPosY) {
        if (crate.getEntityType() == EntityTypeEnum.CRATE) {
            int cratePosX = crate.getPosX();
            int cratePosY = crate.getPosY();

            if (this.getPosX() > newPosX) {
                cratePosX -= TILE_SIZE;
            }
            if (this.getPosX() < newPosX) {
                cratePosX += TILE_SIZE;
            }
            if (this.getPosY() > newPosY) {
                cratePosY -= TILE_SIZE;
            }
            if (this.getPosY() < newPosY) {
                cratePosY += TILE_SIZE;
            }

            Optional<BaseEntity> target = this.getEntityAt(cratePosX, cratePosY);

            if (target.isEmpty() || (target.get().getEntityType() != EntityTypeEnum.WALL && target.get().getEntityType() != EntityTypeEnum.CRATE)) {
                this._isPushingCrateToWall = false;
                crate.setPosition(cratePosX, cratePosY);

                // This will set a null goal if the crate is not inbound of any.
                Optional<BaseEntity> goal = this.getEntityAt(crate.getPosX(), crate.getPosY(), EntityTypeEnum.GOAL);
                if (goal.isPresent()) {
                    crate.setInGoal((Goal)goal.get());
                } else {
                    crate.setInGoal(null);
                }
            } else {
                this._isPushingCrateToWall = true;
            }
        }
    }

    private void handleMovementUpdate(float dt) {
        if (this._keyCooldown == MAX_KEY_COOLDOWN) {
            switch (Keyboard.getAction()) {
                case MOVE_UP -> this.movePlayer(0, 1, dt);
                case MOVE_DOWN -> this.movePlayer(0, -1, dt);
                case MOVE_LEFT -> this.movePlayer(-1, 0, dt);
                case MOVE_RIGHT -> this.movePlayer(1, 0, dt);
                case null, default -> {}
            }
        }

        if (this._keyCooldown < MAX_KEY_COOLDOWN) {
            this._keyCooldown++;
        }
    }

    @Override
    public void update(float dt) {
        if (!Game.instance().isPaused()) {
            this.handleMovementUpdate(dt);
        }
        Game.instance().handleProgress();
    }

    public void setEntityList(List<BaseEntity> entityList) {
        this._worldEntityList = entityList;
    }

    public List<Vector2i> getRecordedMovementList() {
        return this._recordedMovementList;
    }
}
