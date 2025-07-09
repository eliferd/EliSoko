package fr.eliferd.engine.utils;

public enum LoggerLevel {
    INFO("[INFO]"),
    WARNING("[WARNING]"),
    ERROR("[ERROR]");

    private String _levelString;

    LoggerLevel(String level) {
        this._levelString = level;
    }

    public String toString() {
        return this._levelString;
    }
}
