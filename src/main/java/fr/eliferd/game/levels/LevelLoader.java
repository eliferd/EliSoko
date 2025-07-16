package fr.eliferd.game.levels;

import fr.eliferd.engine.ResourceManager;
import java.util.Map;

import static java.util.Map.entry;

public class LevelLoader {
    private static Map<Integer, LevelMetadata> _levelList = Map.ofEntries(
            entry(1, new LevelMetadata("assets/levels/level1.lvl", 3000)),
            entry(2, new LevelMetadata("assets/levels/level2.lvl", 4000)),
            entry(3, new LevelMetadata("assets/levels/level3.lvl", 2000)),
            entry(4, new LevelMetadata("assets/levels/level4.lvl", 3500)),
            entry(5, new LevelMetadata("assets/levels/level5.lvl", 4000)),
            entry(6, new LevelMetadata("assets/levels/level6.lvl", 5000)),
            entry(7, new LevelMetadata("assets/levels/level7.lvl", 2000)),
            entry(8, new LevelMetadata("assets/levels/level8.lvl", 4000)),
            entry(9, new LevelMetadata("assets/levels/level9.lvl", 500)),
            entry(10, new LevelMetadata("assets/levels/level10.lvl", 2500))
    );

    public static String[] loadLevel(int id) {
        return ResourceManager.getLevel(LevelLoader.getLevelMetadata(id).getLevelFilePath());
    }

    public static LevelMetadata getLevelMetadata(int levelId) {
        return LevelLoader._levelList.get(levelId);
    }

    public static int levelCount() {
        return LevelLoader._levelList.size();
    }
}
