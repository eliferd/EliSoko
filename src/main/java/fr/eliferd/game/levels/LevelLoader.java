package fr.eliferd.game.levels;

import fr.eliferd.engine.ResourceManager;
import java.util.Map;

import static java.util.Map.entry;

public class LevelLoader {
    private static Map<Integer, String> _levelList = Map.ofEntries(
            entry(1, "assets/levels/level1.lvl"),
            entry(2, "assets/levels/level2.lvl"),
            entry(3, "assets/levels/level3.lvl"),
            entry(4, "assets/levels/level4.lvl"),
            entry(5, "assets/levels/level5.lvl"),
            entry(6, "assets/levels/level6.lvl"),
            entry(7, "assets/levels/level7.lvl"),
            entry(8, "assets/levels/level8.lvl"),
            entry(9, "assets/levels/level9.lvl"),
            entry(10, "assets/levels/level10.lvl")
    );
    public static String[] loadLevel(int id) {
        return ResourceManager.getLevel(LevelLoader._levelList.get(id));
    }
    public static int levelCount() {
        return LevelLoader._levelList.size();
    }
}
