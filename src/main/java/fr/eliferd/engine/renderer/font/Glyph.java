package fr.eliferd.engine.renderer.font;

public class Glyph {
    public int id;
    public int x, y, width, height;
    public int xOffset, yOffset, xAdvance;

    public Glyph(int id, int x, int y, int width, int height, int xOffset, int yOffset, int xAdvance) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xAdvance = xAdvance;
    }
}