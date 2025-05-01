package me.vaan.cannonsEXP;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExpManager implements Listener {
    private static final Map<UUID, Integer> xpMap = new HashMap<>();
    private static final File experienceFile = new File(CannonsEXP.getInstance().getDataFolder(), "experience.yml");

    public static void load() {
        var folder = CannonsEXP.getInstance().getDataFolder();
        if(!folder.exists()) {
            folder.mkdir();
        }

        if (!experienceFile.exists()) {
            try {
                experienceFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return;
        }

        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(experienceFile);
        // Load the map data
        for (String uuidString : dataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            int experience = dataConfig.getInt(uuidString);
            xpMap.put(uuid, experience);
        }
    }

    public static void save() {
        YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(experienceFile);
        for (Map.Entry<UUID, Integer> entry : xpMap.entrySet()) {
            dataConfig.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            dataConfig.save(experienceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addXp(OfflinePlayer player, int amount) {
        if (!player.isOnline()) {
            UUID id = player.getUniqueId();
            int current = xpMap.getOrDefault(id, 0);
            xpMap.put(id, current + amount);
            return;
        }

        Player online = player.getPlayer();
        if (online != null) {
            online.setExperienceLevelAndProgress(online.calculateTotalExperiencePoints() + amount);
            online.saveData();
            System.out.println("Exp of player: " + online.getTotalExperience());
        }
    }

    public static boolean removeXp(OfflinePlayer player, int amount) {
        if (!player.isOnline()) {
            throw new IllegalArgumentException("removeXp called with offline player, you can't pay with offline player.");
        }

        Player online = player.getPlayer();
        if (online == null) {
            throw new IllegalArgumentException("removeXp called with offline player, you can't pay with offline player.");
        }

        int current = online.calculateTotalExperiencePoints();
        System.out.println("Exp of player: " + current);
        if (current < amount) {
            return false;
        }

        online.setExperienceLevelAndProgress(current - amount);
        online.saveData();
        System.out.println("Exp of player: " + online.getTotalExperience());
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        Integer xpToGive = xpMap.get(id);
        if(xpToGive == null) {
            return;
        }

        player.setExperienceLevelAndProgress( player.calculateTotalExperiencePoints() + xpToGive );
        xpMap.remove(id);
    }
}
