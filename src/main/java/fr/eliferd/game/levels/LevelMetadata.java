package fr.eliferd.game.levels;

public class LevelMetadata {
    private int _score;
    private String _levelFile;

    public LevelMetadata(String levelFile, int score) {
        this._levelFile = levelFile;
        this._score = score;
    }

    public int getScore() {
        return this._score;
    }

    public String getLevelFilePath() {
        return this._levelFile;
    }
}
