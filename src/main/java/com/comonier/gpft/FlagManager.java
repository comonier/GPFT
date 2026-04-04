package com.comonier.gpft;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
      this.setupStorage();
   }

   private void setupStorage() {
      if (this.useMySQL) {
         this.setupMySQL();
      } else {
         if (!this.dataFile.exists()) {
            try {
               this.dataFile.getParentFile().mkdirs();
               this.dataFile.createNewFile();
            } catch (IOException var2) {
               var2.printStackTrace();
            }
         }

         this.dataConfig = YamlConfiguration.loadConfiguration(this.dataFile);
      }

   }

   private void setupMySQL() {
      String host = this.plugin.getConfig().getString("mysql.host");
      String port = this.plugin.getConfig().getString("mysql.port");
      String db = this.plugin.getConfig().getString("mysql.database");
      String user = this.plugin.getConfig().getString("mysql.username");
      String pass = this.plugin.getConfig().getString("mysql.password");

      try {
         if (this.connection != null && !this.connection.isClosed()) {
            return;
         }

         this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false", user, pass);
         Statement st = this.connection.createStatement();
         st.executeUpdate("CREATE TABLE IF NOT EXISTS gpft_flags (claim_id BIGINT, flag_name VARCHAR(64), state BOOLEAN, PRIMARY KEY (claim_id, flag_name))");
      } catch (SQLException var7) {
         this.plugin.getLogger().severe("Could not connect to MySQL! Falling back to YAML.");
         this.useMySQL = false;
         this.setupStorage();
      }

   }

   public Claim getClaimAt(Location loc) {
      return GriefPrevention.instance.dataStore.getClaimAt(loc, true, (Claim)null);
   }

   public boolean getFlagState(Claim claim, String flagName) {
      if (claim == null) {
         return true;
      } else {
         if (this.useMySQL) {
            try {
               PreparedStatement ps = this.connection.prepareStatement("SELECT state FROM gpft_flags WHERE claim_id = ? AND flag_name = ?");
               ps.setLong(1, claim.getID());
               ps.setString(2, flagName);
               ResultSet rs = ps.executeQuery();
               if (rs.next()) {
                  return rs.getBoolean("state");
               }
            } catch (SQLException var5) {
               var5.printStackTrace();
            }
         } else {
            Long var10000 = claim.getID();
            String path = "claims." + var10000 + "." + flagName;
            if (this.dataConfig.contains(path)) {
               return this.dataConfig.getBoolean(path);
            }
         }

         if (flagName.equals("ender_chests")) {
            return true;
         } else {
            List<String> worldEventsEnabled = Arrays.asList("passive_spawn", "leaf_decay", "liquid_flow", "explosions", "grass_spread", "monster_damage", "spawner_spawn", "egg_spawn");
            return worldEventsEnabled.contains(flagName);
         }
      }
   }

   public void setFlagState(Claim claim, String flagName, boolean newState) {
      if (claim != null) {
         if (this.useMySQL) {
            try {
               PreparedStatement ps = this.connection.prepareStatement("REPLACE INTO gpft_flags (claim_id, flag_name, state) VALUES (?, ?, ?)");
               ps.setLong(1, claim.getID());
               ps.setString(2, flagName);
               ps.setBoolean(3, newState);
               ps.executeUpdate();
            } catch (SQLException var6) {
               var6.printStackTrace();
            }
         } else {
            this.dataConfig.set("claims." + claim.getID() + "." + flagName, newState);

            try {
               this.dataConfig.save(this.dataFile);
            } catch (IOException var5) {
               var5.printStackTrace();
            }
         }

      }
   }

   public boolean canManage(Player player, Claim claim) {
      if (claim == null) {
         return false;
      } else {
         return player.getUniqueId().equals(claim.ownerID) ? true : player.hasPermission("gpft.admin");
      }
   }
}
