package fr.eliferd.engine;

import fr.eliferd.engine.renderer.Texture;
import fr.eliferd.engine.utils.Logger;
import fr.eliferd.engine.utils.LoggerLevel;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ResourceManager {
    private static Map<String, Texture> _textureList = new HashMap<>();
    private static Map<String, String[]> _levelList = new HashMap<>();

    public static Texture getTexture(String path) {
        File file = new File(path);
        final String key = file.getAbsolutePath();

        if (ResourceManager._textureList.containsKey(key)) {
            return ResourceManager._textureList.get(key);
        } else {
            Texture tex = new Texture();
            tex.init(key);
            ResourceManager._textureList.put(key, tex);
            return tex;
        }
    }

    public static String[] getLevel(String path) {
        File file = new File(path);
        final String key = file.getAbsolutePath();

        if (ResourceManager._levelList.containsKey(key)) {
            return ResourceManager._levelList.get(key);
        } else {
            try {
                List<String> lines = Files.readAllLines(file.toPath());

                String[] levelMap = new String[lines.size()];
                lines.toArray(levelMap);

                Collections.reverse(Arrays.asList(levelMap));

                ResourceManager._levelList.put(key, levelMap);

                return levelMap;
            } catch (IOException ex) {
                Logger.print(ex.getMessage(), LoggerLevel.ERROR);
            }
        }
        return null;
    }
}
