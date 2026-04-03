package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class FlagManager {
    private final Main plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private Connection connection;
    private boolean useMySQL;

    public FlagManager(Main plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        this.useMySQL = plugin.getConfig().getBoolean("mysql.enabled", false);
        setupStorage();
    }

    private void setupStorage() {
        if (useMySQL) {
            setupMySQL();
        } else {
            if (dataFile.exists() == false) {
                try { 
                    dataFile.getParentFile().mkdirs(); 
                    dataFile.createNewFile(); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                }
            }
            this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        }
    }

    private void setupMySQL() {
        String host = plugin.getConfig().getString("mysql.host");
        String port = plugin.getConfig().getString("mysql.port");
        String db = plugin.getConfig().getString("mysql.database");
        String user = plugin.getConfig().getString("mysql.username");
        String pass = plugin.getConfig().getString("mysql.password");
        try {
            if (connection != null && connection.isClosed() == false) return;
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false", user, pass);
            Statement st = connection.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS gpft_flags (claim_id BIGINT, flag_name VARCHAR(64), state BOOLEAN, PRIMARY KEY (claim_id, flag_name))");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to MySQL! Falling back to YAML.");
            useMySQL = false;
            setupStorage();
        }
    }

    public Claim getClaimAt(Location loc) {
        return GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
    }

    public boolean getFlagState(Claim claim, String flagName) {
        if (claim == null) return true;
        
        if (useMySQL) {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT state FROM gpft_flags WHERE claim_id = ? AND flag_name = ?");
                ps.setLong(1, claim.getID());
                ps.setString(2, flagName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getBoolean("state");
            } catch (SQLException e) { 
                e.printStackTrace(); 
            }
        } else {
            String path = "claims." + claim.getID() + "." + flagName;
            if (dataConfig.contains(path)) return dataConfig.getBoolean(path);
        }

        // Default values logic
        if (flagName.equals("ender_chests")) return true;
        
        List<String> worldEventsEnabled = Arrays.asList(
            "passive_spawn", "leaf_decay", "liquid_flow", 
            "explosions", "grass_spread", "monster_damage", "spawner_spawn", "egg_spawn"
        );
        
        if (worldEventsEnabled.contains(flagName)) return true;
        
        return false;
    }

    public void setFlagState(Claim claim, String flagName, boolean newState) {
        if (claim == null) return;
        
        if (useMySQL) {
            try {
                PreparedStatement ps = connection.prepareStatement("REPLACE INTO gpft_flags (claim_id, flag_name, state) VALUES (?, ?, ?)");
                ps.setLong(1, claim.getID());
                ps.setString(2, flagName);
                ps.setBoolean(3, newState);
                ps.executeUpdate();
            } catch (SQLException e) { 
                e.printStackTrace(); 
            }
        } else {
            dataConfig.set("claims." + claim.getID() + "." + flagName, newState);
            try { 
                dataConfig.save(dataFile); 
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
        }
    }

    public boolean canManage(Player player, Claim claim) {
        if (claim == null) return false;
        if (player.getUniqueId().equals(claim.ownerID)) return true;
        return player.hasPermission("gpft.admin");
    }
}
