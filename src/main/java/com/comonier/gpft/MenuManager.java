package com.comonier.gpft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import me.ryanhamshire.GriefPrevention.Claim;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MenuManager implements InventoryHolder {
	private final Main plugin;
	private final FlagManager flagManager;
	private final Map<String, Material> iconMap = new HashMap<>();

	public MenuManager(Main plugin, FlagManager flagManager) {
		this.plugin = plugin;
		this.flagManager = flagManager;
		setupIcons();
	}

	private void setupIcons() {
		iconMap.put("monster_spawn", Material.ZOMBIE_HEAD);
		iconMap.put("passive_spawn", Material.SHEEP_SPAWN_EGG);
		iconMap.put("leaf_decay", Material.OAK_LEAVES);
		iconMap.put("weather_lock", Material.LIGHTNING_ROD);
		iconMap.put("ice_form", Material.ICE);
		iconMap.put("liquid_flow", Material.WATER_BUCKET);
		iconMap.put("pvp", Material.DIAMOND_SWORD);
		iconMap.put("fire_spread", Material.CAMPFIRE);
		iconMap.put("explosions", Material.TNT);
		iconMap.put("grass_spread", Material.GRASS_BLOCK);
		iconMap.put("monster_damage", Material.IRON_SWORD);
		iconMap.put("villager_trade", Material.EMERALD);
		iconMap.put("spawner_spawn", Material.SPAWNER);
		iconMap.put("ender_pearl", Material.ENDER_PEARL);
		iconMap.put("item_drop", Material.STICK);
		iconMap.put("egg_spawn", Material.EGG);
		iconMap.put("kill_mobs", Material.BONE);
		iconMap.put("wooden_doors", Material.OAK_DOOR);
		iconMap.put("iron_doors", Material.IRON_DOOR);
		iconMap.put("open_chests", Material.CHEST);
		iconMap.put("ender_chests", Material.ENDER_CHEST);
		iconMap.put("set_home", Material.RECOVERY_COMPASS);
		iconMap.put("visitor_fly", Material.FEATHER);
		iconMap.put("buttons_levers", Material.LEVER);
		iconMap.put("pressure_plates", Material.OAK_PRESSURE_PLATE);
		iconMap.put("pickup_items", Material.HOPPER);
		iconMap.put("lead_mobs", Material.LEAD);
		iconMap.put("use_bows", Material.BOW);
	}

	@Override
	public Inventory getInventory() { return null; }

	public void openMenu(Player player, Claim claim) {
		Inventory gui = Bukkit.createInventory(this, 54, "§0Grief Prevention Flag Toggle");
		fillBorders(gui);
		String[] keys = {
			"monster_spawn", "passive_spawn", "leaf_decay", "weather_lock", "ice_form", "liquid_flow", "pvp",
			"fire_spread", "explosions", "grass_spread", "monster_damage", "villager_trade", "spawner_spawn", "ender_pearl",
			"item_drop", "egg_spawn", "kill_mobs", "wooden_doors", "iron_doors", "open_chests", "ender_chests",
			"set_home", "visitor_fly", "buttons_levers", "pressure_plates", "pickup_items", "lead_mobs", "use_bows"
		};
		int slot = 10;
		for (String fKey : keys) {
			if (slot % 9 == 0) slot++;
			if (slot % 9 == 8) slot = slot + 2;
			if (slot > 43) break;
			
			boolean state = flagManager.getFlagState(claim, fKey);
			boolean isEnabled = plugin.isModuleEnabled(fKey);
			
			gui.setItem(slot, createFlagItem(fKey, state, isEnabled));
			slot++;
		}
		player.openInventory(gui);
	}

	public ItemStack createFlagItem(String flagKey, boolean status, boolean isEnabled) {
		Material mat = isEnabled ? iconMap.getOrDefault(flagKey, Material.PAPER) : Material.RED_STAINED_GLASS_PANE;
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setDisplayName("§6" + plugin.getMsgRaw("flag_" + flagKey));
			List<String> lore = new ArrayList<>();
			lore.add(plugin.getMsgRaw("gui_mode_label") + (status ? plugin.getMsgRaw("gui_status_on") : plugin.getMsgRaw("gui_status_off")));
			
			if (isEnabled) {
				lore.add(plugin.getMsgRaw("gui_click_to_toggle"));
				NamespacedKey key = new NamespacedKey(plugin, "flag_key");
				meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, flagKey);
			} else {
				lore.add(plugin.getMsgRaw("gui_module_disabled"));
			}
			
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}

	private void fillBorders(Inventory gui) {
		ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta m = glass.getItemMeta();
		if (m != null) { m.setDisplayName(" "); glass.setItemMeta(m); }
		for (int i = 0; i < 54; i++) { if (i <= 8 || i >= 45 || i % 9 == 0 || i % 9 == 8) gui.setItem(i, glass); }
	}
}
