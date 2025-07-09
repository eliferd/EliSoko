package fr.eliferd.engine.renderer.font;

import fr.eliferd.engine.ResourceManager;
import fr.eliferd.engine.renderer.Texture;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BitmapFont {
    private Map<Character, Glyph> glyphs = new HashMap<>();
    private Texture texture;

    public BitmapFont(String texturePath, String xmlPath) {
        this.texture = ResourceManager.getTexture(texturePath);
        loadGlyphs(xmlPath);
    }

    private void loadGlyphs(String xmlPath) {
        try {
            File file = new File(xmlPath);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList nodeList = doc.getElementsByTagName("char");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                int id = Integer.parseInt(element.getAttribute("id"));
                int x = Integer.parseInt(element.getAttribute("x"));
                int y = Integer.parseInt(element.getAttribute("y"));
                int width = Integer.parseInt(element.getAttribute("width"));
                int height = Integer.parseInt(element.getAttribute("height"));
                int xOffset = Integer.parseInt(element.getAttribute("xoffset"));
                int yOffset = Integer.parseInt(element.getAttribute("yoffset"));
                int xAdvance = Integer.parseInt(element.getAttribute("xadvance"));

                glyphs.put((char) id, new Glyph(id, x, y, width, height, xOffset, yOffset, xAdvance));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public Glyph getGlyph(char c) {
        return glyphs.get(c);
    }

    public Glyph[] getAllGlyphs() {
        return this.glyphs.values().toArray(new Glyph[0]);
    }
}