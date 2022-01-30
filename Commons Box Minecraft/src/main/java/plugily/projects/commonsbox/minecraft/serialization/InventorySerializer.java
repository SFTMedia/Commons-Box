package plugily.projects.commonsbox.minecraft.serialization;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class InventorySerializer {

  private InventorySerializer() {
  }

  /**
   * Saves player inventory to file in plugin directory
   *
   * @param plugin javaplugin to get data folder
   * @param player player to save data
   * @return true if saved properly, false if inventory is null or couldn't save
   */
  public static boolean saveInventoryToFile(JavaPlugin plugin, Player player) {
    PlayerInventory inventory = player.getInventory();

    File path = new File(plugin.getDataFolder(), "inventories");
    path.mkdirs();

    try {
      File invFile = new File(path, player.getUniqueId().toString() + ".invsave");
      if(invFile.exists()) {
        invFile.delete();
      }

      FileConfiguration invConfig = YamlConfiguration.loadConfiguration(invFile);

      invConfig.set("ExperienceProgress", player.getExp());
      invConfig.set("ExperienceLevel", player.getLevel());
      invConfig.set("Current health", player.getHealth());

      int max_health = VersionUtils.getMaxHealth(player)

      PotionEffect effect = entity.getPotionEffect(PotionEffectType.HEALTH_BOOST);
      if (effect != null) {
        // Health boost effect has a base of 2 extra hearts then adds for 2 hearts for every level beyond
        // 2 hearts (per level) is 4 half hearts (MAX_HEALTH is stored as half hearts)
        max_health -= (effect.getAmplifier() + 1) * 4;
      }

      invConfig.set("Max health", max_health);
      invConfig.set("Food", player.getFoodLevel());
      invConfig.set("Saturation", player.getSaturation());
      invConfig.set("Fire ticks", player.getFireTicks());
      invConfig.set("GameMode", player.getGameMode().name());
      invConfig.set("Allow flight", player.getAllowFlight());

      invConfig.set("Size", inventory.getSize());
      invConfig.set("Max stack size", inventory.getMaxStackSize());

      java.util.Collection<PotionEffect> activeEffects = player.getActivePotionEffects();
      List<String> activePotions = new ArrayList<>(activeEffects.size());

      for(PotionEffect potion : activeEffects) {
        activePotions.add(potion.getType().getName() + "#" + potion.getDuration() + "#" + potion.getAmplifier());
      }
      invConfig.set("Active potion effects", activePotions);

      org.bukkit.entity.HumanEntity holder = inventory.getHolder();
      if(holder instanceof Player) {
        invConfig.set("Holder", holder.getName());
      }

      ItemStack[] invContents = inventory.getContents();
      for(int i = 0; i < invContents.length; i++) {
        ItemStack itemInInv = invContents[i];
        if(itemInInv != null && itemInInv.getType() != Material.AIR) {
          invConfig.set("Slot " + i, itemInInv);
        }
      }

      if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
        invConfig.set("Offhand", inventory.getItemInOffHand());
      }

      ItemStack[] armorContents = inventory.getArmorContents();
      for(int b = 0; b < armorContents.length; b++) {
        ItemStack itemStack = armorContents[b];
        if(itemStack != null && itemStack.getType() != Material.AIR) {
          invConfig.set("Armor " + b, itemStack);
        }
      }

      invConfig.save(invFile);
      return true;
    } catch(Exception ex) {
      ex.printStackTrace();
      Bukkit.getConsoleSender().sendMessage("Cannot save inventory of player!");
      Bukkit.getConsoleSender().sendMessage("Disable inventory saving option in config.yml or restart the server!");
      return false;
    }
  }

  private static Inventory getInventoryFromFile(JavaPlugin plugin, String uuid) {
    File file = new File(plugin.getDataFolder(), "inventories" + File.separator + uuid + ".invsave");
    if(!file.exists() || file.isDirectory() || !file.getAbsolutePath().endsWith(".invsave")) {
      return Bukkit.createInventory(null, 9);
    }
    try {
      FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);
      int invSize = invConfig.getInt("Size", 36);
      if(invSize > 36 || invSize < 1) {
        invSize = 36;
      }

      String holder = invConfig.getString("Holder");
      Inventory inventory = plugin.getServer().createInventory(holder != null ? Bukkit.getPlayer(holder) : null, InventoryType.PLAYER);
      inventory.setMaxStackSize(invConfig.getInt("Max stack size", 64));

      ItemStack[] invContents = new ItemStack[invSize];
      for(int i = 0; i < invSize; i++) {
        if(invConfig.contains("Slot " + i)) {
          invContents[i] = invConfig.getItemStack("Slot " + i);
        } else {
          invContents[i] = new ItemStack(Material.AIR);
        }
      }

      try {
        inventory.setContents(invContents);
      } catch(IllegalArgumentException ex) {
        Bukkit.getConsoleSender().sendMessage("Cannot get inventory of player! Inventory has more items than the default content size.");
        Bukkit.getConsoleSender().sendMessage("Disable inventory saving option in config.yml or restart the server!");
      }

      file.delete();
      return inventory;
    } catch(Exception ex) {
      ex.printStackTrace();
      Bukkit.getConsoleSender().sendMessage("Cannot save inventory of player!");
      Bukkit.getConsoleSender().sendMessage("Disable inventory saving option in config.yml or restart the server!");
      return Bukkit.createInventory(null, 9);
    }
  }

  /**
   * Loads inventory of player from data folder
   *
   * @param plugin javaplugin to get data folder
   * @param player load inventory of this player
   */
  public static void loadInventory(JavaPlugin plugin, Player player) {
    String stringId = player.getUniqueId().toString();

    File file = new File(plugin.getDataFolder(), "inventories" + File.separator + stringId + ".invsave");
    if(!file.exists() || file.isDirectory() || !file.getAbsolutePath().endsWith(".invsave")) {
      return;
    }

    FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);
    PlayerInventory playerInventory = player.getInventory();

    try {
      try {
        ItemStack[] armor = new ItemStack[playerInventory.getArmorContents().length];
        for(int i = 0; i < armor.length; i++) {
          if(invConfig.contains("Armor " + i)) {
            armor[i] = invConfig.getItemStack("Armor " + i);
          } else {
            armor[i] = new ItemStack(Material.AIR);
          }
        }

        playerInventory.setArmorContents(armor);

        if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
          playerInventory.setItemInOffHand(invConfig.getItemStack("Offhand", new ItemStack(Material.AIR)));
        }

        VersionUtils.setMaxHealth(player, invConfig.getDouble("Max health"));
        player.setExp(0);
        player.setLevel(0);
        player.setLevel(invConfig.getInt("ExperienceLevel"));

        try {
          player.setExp(Float.parseFloat(invConfig.getString("ExperienceProgress", "0")));
        } catch (NumberFormatException ex) {
        }

        player.setHealth(invConfig.getDouble("Current health"));
        player.setFoodLevel(invConfig.getInt("Food"));

        try {
          player.setSaturation(Float.parseFloat(invConfig.getString("Saturation", "0")));
        } catch (NumberFormatException ex) {
        }

        player.setFireTicks(invConfig.getInt("Fire ticks"));

        GameMode gameMode = GameMode.SURVIVAL;
        try {
          gameMode = GameMode.valueOf(invConfig.getString("GameMode", "").toUpperCase(java.util.Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
        }

        player.setGameMode(gameMode);
        player.setAllowFlight(invConfig.getBoolean("Allow flight"));

        List<String> activePotions = invConfig.getStringList("Active potion effects");
        for(String potion : activePotions) {
          String[] splited = potion.split("#", 3);

          if (splited.length == 0)
            continue;

          PotionEffectType effectType = PotionEffectType.getByName(splited[0]);

          if (effectType != null) {
            try {
              player.addPotionEffect(new PotionEffect(effectType, splited.length < 2 ? 30 : Integer.parseInt(splited[1]),
                  splited.length < 3 ? 1 : Integer.parseInt(splited[2])));
            } catch (NumberFormatException ex) {
            }
          }
        }
      } catch(Exception ignored) {
      }

      Inventory inventory = getInventoryFromFile(plugin, stringId);

      for(int i = 0; i < inventory.getContents().length; i++) {
        ItemStack item = inventory.getItem(i);

        if(item != null) {
          playerInventory.setItem(i, item);
        }
      }

      player.updateInventory();
    } catch(Exception ignored) {
      //ignore any exceptions
    }
  }

}
