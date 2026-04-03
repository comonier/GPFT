# GPFT - GriefPrevention Flag Toggle

**GPFT** is a robust Minecraft plugin (1.21+) that allows GriefPrevention claim owners to manage permissions (flags) intuitively through a GUI. It offers granular control over visitor interactions, environmental events, and entry notifications.

---

## 📌 Main Features

*   **GUI Interface:** Interactive menu to toggle 28 different flags.
*   **Module System:** Administrators can globally disable flags via `modules.yml`.
*   **Dual Database:** Native support for **SQLite (local)** or **MySQL**.
*   **Entry Notifications:** Customizable alerts (Chat, ActionBar, Title, or BossBar) when entering claims.
*   **Weather Control:** Optional permanent "Sun" visual inside claims, even during global rain.
*   **Advanced Security:** Real blocking of bow/crossbow fire, gate interactions, and villager trading for visitors.

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

## 💻 Commands & Aliases


| Command | Alias | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/gpft` | `/flag`, `/flags` | Opens the flag management menu for the current claim. | `gpft.user` |
| `/gpft help` | - | Displays the command list. | `gpft.user` |
| `/gpft reload` | - | Reloads configs, messages, and modules. | `gpft.admin` |

---

## ⚙️ Configuration Example (`config.yml`)

```yaml
# GPFT - GriefPrevention Flag Toggle
# Language settings: 'pt' for messages_pt.yml or 'en' for messages_en.yml
language: 'en'
prefix: '&8[&6GPFT&8] '

# --- NOTIFICATION SETTINGS ---
# Enabled: true/false
# Type options: 'chat', 'actionbar', 'title', 'bossbar'
# Stay_time: Time in seconds to show the message
# Bossbar Colors: BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW, GOLD
# Bossbar Styles: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20
notifications:
  enabled: true
  type: 'title'
  stay_time: 3
  bossbar_color: 'GOLD'
  bossbar_style: 'SOLID'

# --- DATABASE SETTINGS ---
# To use MySQL, set enabled to true. If false, local SQLite/YAML will be used.
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
  kill_mobs: false
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

