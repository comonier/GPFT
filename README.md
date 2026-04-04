# GPFT - GriefPrevention Flag Toggle (v1.3)

**GPFT** is a powerful addon for the [GriefPrevention](https://github.com) plugin, allowing claim owners to manage protection flags through an intuitive Graphical User Interface (GUI).

## 🚀 What's New in v1.3

- **Per-Flag Permission System:** Now you can allow or block specific flags for player groups (e.g., VIPs can have access to `pvp` or `visitor_fly` while default players cannot).
- **Flight Safety (Fly):** Implemented a 10-second countdown with screen alerts (Title/Subtitle) when leaving a claim or disabling the fly flag, preventing fall damage deaths.
- **VIP Protection:** The plugin now detects the `essentials.fly` permission. If a player has flight enabled via Essentials/VIP systems, GPFT will not interfere.
- **Spawn Logic Fixes:** Adjusted `Spawner Egg` and `Command` logic to allow administrators or server systems to spawn mobs even when the spawn flag is disabled.
- **Damage Logic Fixes:** Full blocking of monster damage (including projectiles like skeleton arrows) when the `monster_damage` flag is disabled.

## 🛠️ Commands and Permissions

### Commands:
- `/gpft` or `/flags` - Opens the flag management menu in the current claim.
- `/gpft help` - Lists all available commands.
- `/gpft reload` - Reloads configurations and translations (Admins only).

### General Permissions:
- `gpft.user` - Allows opening the flag menu (Default: Everyone).
- `gpft.admin` - Allows `/gpft reload` and managing any claim (Default: OP).
- `gpft.flag.*` - Grants access to all flags in the menu (Default: Everyone).

### Individual Permissions (Examples):
To block a specific flag for a group (e.g., using LuckPerms):
- `/lp group default permission set gpft.flag.pvp false`
- `/lp group vip permission set gpft.flag.visitor_fly true`

## 📋 Manageable Flags (28 Total)
The plugin includes flags for:
- **Spawn:** Monsters, Animals, Spawners, and Eggs.
- **Environment:** Liquid flow, explosions, fire spread, ice/snow formation, and grass spread.
- **Interaction:** Opening chests, doors (wood/iron), buttons, levers, and villager trading.
- **Player:** Bow usage, Ender Pearl, item drop/pickup, flight mode, and sethome.
- **World:** Per-claim weather control and leaf decay.

## ⚙️ Installation
1. Ensure **GriefPrevention** is installed.
2. Place `GPFT.jar` in your `plugins` folder.
3. Restart your server.
4. Configure messages and modules in `config.yml`, `modules.yml`, and `messages_en.yml`.

---
Developed by **Comonier**.


---

## 🛠 Modules & All 28 Flags

The flags are categorized based on their impact on the claim environment and visitor permissions:

### 1. Spawning & Mobs (7 Flags)
*   `monster_spawn`: Toggles monster spawning (Default: Off).
*   `passive_spawn`: Toggles animal spawning (Default: On).
*   `spawner_spawn`: Toggles mob spawning from Spawners.
*   `egg_spawn`: Toggles spawning from projectile/thrown eggs.
*   `monster_damage`: Toggles damage dealt by monsters to players.
*   `kill_mobs`: Allows visitors to kill animals/mobs.
*   `lead_mobs`: Allows visitors to use leads on animals.

### 2. Environment & Physics (7 Flags)
*   `leaf_decay`: Toggles leaves disappearing naturally.
*   `grass_spread`: Toggles grass spreading to dirt blocks.
*   `fire_spread`: Toggles fire spreading and block burning (Default: Off).
*   `explosions`: Toggles TNT, Creeper, and Ghast explosions.
*   `liquid_flow`: Toggles water and lava flow.
*   `ice_form`: Toggles ice and snow formation (Default: Off).
*   `weather_lock`: Locks weather to "Clear" visually inside the claim (Default: Off).

### 3. Visitor Access & Doors (4 Flags)
*   `wooden_doors`: Access to wooden doors, fence gates, and trapdoors.
*   `iron_doors`: Access to iron doors and iron trapdoors.
*   `buttons_levers`: Usage of buttons and levers.
*   `pressure_plates`: Activation of pressure plates.

### 4. Containers & Trading (3 Flags)
*   `open_chests`: Access to Chests, Barrels, and Shulker Boxes.
*   `ender_chests`: Access to Ender Chests (Default: On).
*   `villager_trade`: Interaction and trading with Villagers.

### 5. Combat, Movement & Items (7 Flags)
*   `pvp`: Toggles Player vs Player combat within the claim.
*   `use_bows`: Usage of bows and crossbows (with warning message).
*   `ender_pearl`: Usage of Ender Pearls for teleportation.
*   `visitor_fly`: Allows visitors to use flight mode.
*   `set_home`: Allows visitors to set homes within the claim.
*   `item_drop`: Allows visitors to drop items from their inventory.
*   `pickup_items`: Allows visitors to pick up items from the ground.

---

## ⚙️ Configuration Example (`config.yml`)

```yaml
# GPFT - GriefPrevention Flag Toggle
# Language settings: 'pt' for messages_pt.yml or 'en' for messages_en.yml
language: 'en'
prefix: '&8[&6GPFT&8] '

# --- PERMISSION INFO ---
# By default, all players have the permission 'gpft.flag.*' (set to true).
# To block a specific flag for a group, use your permission plugin (e.g. LuckPerms)
# to set the permission to false. Example: /lp group default permission set gpft.flag.pvp false
# If a module is set to 'false' in modules.yml, only players with 'gpft.admin' or 
# the specific flag permission can see/use it.

# --- NOTIFICATION SETTINGS ---
# Enabled: true/false
notifications:
  enabled: true
  use_chat: false
  use_actionbar: true
  use_title: true
  use_bossbar: false
  stay_time: 3
  bossbar_color: 'GOLD'
  bossbar_style: 'SOLID'

# --- DATABASE SETTINGS ---
mysql:
  enabled: false
  host: 'localhost'
  port: '3306'
  database: 'minecraft'
  username: 'root'
  password: ''

# NOTICE: To enable or disable specific modules globally for the entire server,
# please edit the 'modules.yml' file. Settings below are for claim defaults only.

flags:
  monster_spawn: false
  passive_spawn: true
  leaf_decay: true
  weather_lock: false
  ice_form: false
  liquid_flow: true
  pvp: false
  fire_spread: false
  explosions: true
  grass_spread: true
  monster_damage: true
  villager_trade: false
  item_drop: false
  ender_pearl: false
  spawner_spawn: true
  egg_spawn: true
  kill_passive: false
  wooden_doors: false
  iron_doors: false
  open_chests: false
  ender_chests: true
  set_home: false
  visitor_fly: false
  buttons_levers: false
  pressure_plates: false
  pickup_items: false
  lead_mobs: false
  use_bows: false

gui:
  title: '&8Grief Prevention Flag Toggle'
  fill_item: 'BLACK_STAINED_GLASS_PANE'

```

## 🚀 How to Compile
Ensure you have Maven installed.
Run the command: mvn clean package
The .jar file will be in the target/ folder

