package com.comonier.gpft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MenuManager implements InventoryHolder {
   private final Main plugin;
   private final FlagManager flagManager;
   private final Map<String, Material> iconMap = new HashMap();

   public MenuManager(Main plugin, FlagManager flagManager) {
      this.plugin = plugin;
      this.flagManager = flagManager;
      this.setupIcons();
   }

   private void setupIcons() {
      this.iconMap.put("monster_spawn", Material.ZOMBIE_HEAD);
      this.iconMap.put("passive_spawn", Material.SHEEP_SPAWN_EGG);
      this.iconMap.put("leaf_decay", Material.OAK_LEAVES);
      this.iconMap.put("weather_lock", Material.LIGHTNING_ROD);
      this.iconMap.put("ice_form", Material.ICE);
      this.iconMap.put("liquid_flow", Material.WATER_BUCKET);
      this.iconMap.put("pvp", Material.DIAMOND_SWORD);
      this.iconMap.put("fire_spread", Material.CAMPFIRE);
      this.iconMap.put("explosions", Material.TNT);
      this.iconMap.put("grass_spread", Material.GRASS_BLOCK);
      this.iconMap.put("monster_damage", Material.IRON_SWORD);
      this.iconMap.put("villager_trade", Material.EMERALD);
      this.iconMap.put("spawner_spawn", Material.SPAWNER);
      this.iconMap.put("ender_pearl", Material.ENDER_PEARL);
      this.iconMap.put("item_drop", Material.STICK);
      this.iconMap.put("egg_spawn", Material.EGG);
      this.iconMap.put("kill_passive", Material.BONE);
      this.iconMap.put("wooden_doors", Material.OAK_DOOR);
      this.iconMap.put("iron_doors", Material.IRON_DOOR);
      this.iconMap.put("open_chests", Material.CHEST);
      this.iconMap.put("ender_chests", Material.ENDER_CHEST);
      this.iconMap.put("set_home", Material.RECOVERY_COMPASS);
      this.iconMap.put("visitor_fly", Material.FEATHER);
      this.iconMap.put("buttons_levers", Material.LEVER);
      this.iconMap.put("pressure_plates", Material.OAK_PRESSURE_PLATE);
      this.iconMap.put("pickup_items", Material.HOPPER);
      this.iconMap.put("lead_mobs", Material.LEAD);
      this.iconMap.put("use_bows", Material.BOW);
   }

   public Inventory getInventory() {
      return null;
   }

   public void openMenu(Player player, Claim claim) {
      Inventory gui = Bukkit.createInventory(this, 54, "\u00a70Grief Prevention Flag Toggle");
      this.fillBorders(gui);
      String[] keys = new String[]{"monster_spawn", "passive_spawn", "leaf_decay", "weather_lock", "ice_form", "liquid_flow", "pvp", "fire_spread", "explosions", "grass_spread", "monster_damage", "villager_trade", "spawner_spawn", "ender_pearl", "item_drop", "egg_spawn", "kill_passive", "wooden_doors", "iron_doors", "open_chests", "ender_chests", "set_home", "visitor_fly", "buttons_levers", "pressure_plates", "pickup_items", "lead_mobs", "use_bows"};
      int slot = 10;
      String[] var6 = keys;
      int var7 = keys.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String fKey = var6[var8];
         if (slot % 9 == 0) {
            ++slot;
         }

         if (slot % 9 == 8) {
            slot += 2;
         }

         if (slot > 43) {
            break;
         }

         boolean state = this.flagManager.getFlagState(claim, fKey);
         boolean isEnabled = this.plugin.isModuleEnabled(fKey);
         boolean hasPerm = player.hasPermission("gpft.flag." + fKey) || player.hasPermission("gpft.admin");
         boolean showAsEnabled = (isEnabled || player.hasPermission("gpft.admin")) && hasPerm;
         gui.setItem(slot, this.createFlagItem(fKey, state, showAsEnabled));
         ++slot;
      }

      player.openInventory(gui);
   }

   public ItemStack createFlagItem(String flagKey, boolean status, boolean isEnabled) {
      Material mat = isEnabled ? (Material)this.iconMap.getOrDefault(flagKey, Material.PAPER) : Material.RED_STAINED_GLASS_PANE;
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         Main var10001 = this.plugin;
         meta.setDisplayName("\u00a76" + var10001.getMsgRaw("flag_" + flagKey));
         List<String> lore = new ArrayList();
         String var9 = this.plugin.getMsgRaw("gui_mode_label");
         lore.add(var9 + (status ? this.plugin.getMsgRaw("gui_status_on") : this.plugin.getMsgRaw("gui_status_off")));
         if (isEnabled) {
            lore.add(this.plugin.getMsgRaw("gui_click_to_toggle"));
            NamespacedKey key = new NamespacedKey(this.plugin, "flag_key");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, flagKey);
         } else {
            lore.add(this.plugin.getMsgRaw("gui_module_disabled"));
         }

         meta.setLore(lore);
         item.setItemMeta(meta);
      }

      return item;
   }

   private void fillBorders(Inventory gui) {
      ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
      ItemMeta m = glass.getItemMeta();
      if (m != null) {
         m.setDisplayName(" ");
         glass.setItemMeta(m);
      }

      for(int i = 0; i < 54; ++i) {
         if (i <= 8 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
            gui.setItem(i, glass);
         }
      }

   }
}
