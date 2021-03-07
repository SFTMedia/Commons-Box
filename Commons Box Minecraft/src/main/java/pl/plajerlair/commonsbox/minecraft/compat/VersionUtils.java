package pl.plajerlair.commonsbox.minecraft.compat;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion.Version;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XParticle;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XParticleLegacy;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.plajerlair.commonsbox.minecraft.compat.PacketUtils.getNMSClass;
import static pl.plajerlair.commonsbox.minecraft.compat.PacketUtils.sendPacket;

@SuppressWarnings("deprecation")
public class VersionUtils {

  private static boolean isPaper = false;

  static {
    try {
      Class.forName("com.destroystokyo.paper.PaperConfig");
      isPaper = true;
    } catch (ClassNotFoundException e) {
      isPaper = false;
    }
  }

  public static boolean checkOffHand(EquipmentSlot equipmentSlot) {
    return Version.isCurrentEqualOrHigher(Version.v1_9_R1) && equipmentSlot == EquipmentSlot.OFF_HAND;
  }

  public static SkullMeta setPlayerHead(Player player, SkullMeta meta) {
    if(ServerVersion.Version.isCurrentHigher(ServerVersion.Version.v1_12_R1)) {
      if(isPaper && player.getPlayerProfile().hasTextures()) {
        return CompletableFuture.supplyAsync(() -> {
          meta.setPlayerProfile(player.getPlayerProfile());
          return meta;
        }).exceptionally(e -> {
          Bukkit.getConsoleSender().sendMessage("[Commons Box] Retrieving player profile of " + player.getName() + " failed!");
          return meta;
        }).join();
      }
      meta.setOwningPlayer(player);
    } else {
      meta.setOwner(player.getName());
    }
    return meta;
  }

  public static void sendParticles(String particle, Player player, Location location, int count) {
    if (!isPaper) {
      MiscUtils.spawnParticle(Particle.valueOf(particle), location, count, 0, 0, 0, 0);
    } else if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      XParticle.getParticle(particle).builder().location(location).count(count).spawn();
    } else {
      try {
        XParticleLegacy.valueOf(particle).sendToPlayer(player, location, 0, 0, 0, 0, count);
      } catch(Exception ignored) {
      }
    }
  }

  public static void sendParticles(String particle, Set<Player> players, Location location, int count) {
    if (!isPaper) {
      MiscUtils.spawnParticle(Particle.valueOf(particle), location, count, 0, 0, 0, 0);
    } else if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      XParticle.getParticle(particle).builder().location(location).count(count).spawn();
    } else {
      try {
        XParticleLegacy.valueOf(particle).sendToPlayers(players == null ? Bukkit.getOnlinePlayers() : players, location, 0, 0, 0, 0, count, true);
      } catch(Exception ignored) {
      }
    }
  }

  public static void sendParticles(String particle, Set<Player> players, Location location, int count, double offsetX, double offsetY, double offsetZ) {
    if(Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      XParticle.getParticle(particle).builder().location(location).count(count).offset(offsetX, offsetY, offsetZ).spawn();
    } else {
      try {
        XParticleLegacy.valueOf(particle).sendToPlayers(players == null ? Bukkit.getOnlinePlayers() : players, location, (float) offsetX, (float) offsetY, (float) offsetZ, 0, count, true);
      } catch(Exception ignored) {
      }
    }
  }

  public static List<String> getParticleValues() {
    if(Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      return Stream.of(XParticle.getParticles()).map(Enum::toString).collect(Collectors.toList());
    }

    return Stream.of(XParticleLegacy.values()).map(Enum::toString).collect(Collectors.toList());
  }

  public static void updateNameTagsVisibility(Player player, Player other, String tag, boolean remove) {
    Scoreboard scoreboard = other.getScoreboard();
    if(scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
      scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }
    Team team = scoreboard.getTeam(tag);
    if(team == null) {
      team = scoreboard.registerNewTeam(tag);
    }
    team.setCanSeeFriendlyInvisibles(false);
    if(Version.isCurrentEqualOrHigher(Version.v1_11_R1)) {
      team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    } else {
      team.setNameTagVisibility(NameTagVisibility.NEVER);
    }
    if(!remove) {
      team.addEntry(player.getName());
    } else {
      team.removeEntry(player.getName());
    }
    other.setScoreboard(scoreboard);
  }


  public static Entity getPassenger(Entity ent) {
    if(Version.isCurrentLower(Version.v1_13_R2)) {
      return ent.getPassenger();
    } else if(!ent.getPassengers().isEmpty()) {
      return ent.getPassengers().get(0);
    }

    return null;
  }

  public static void setDurability(ItemStack item, short durability) {
    if(Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      ItemMeta meta = item.getItemMeta();
      if(meta != null) {
        ((Damageable) meta).setDamage(durability);
      }
    } else {
      item.setDurability(durability);
    }
  }

  public static void hidePlayer(JavaPlugin plugin, Player to, Player p) {
    if(Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      to.hidePlayer(plugin, p);
    } else {
      to.hidePlayer(p);
    }
  }

  public static void showPlayer(JavaPlugin plugin, Player to, Player p) {
    if(Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      to.showPlayer(plugin, p);
    } else {
      to.showPlayer(p);
    }
  }

  public static void setPassenger(Entity to, Entity... passengers) {
    if(Version.isCurrentLower(Version.v1_13_R2)) {
      for(Entity ps : passengers) {
        to.setPassenger(ps);
      }
    } else {
      for(Entity ps : passengers) {
        to.addPassenger(ps);
      }
    }
  }

  public static void sendTextComponent(CommandSender sender, TextComponent component) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      if(sender instanceof Player) {
        ((Player) sender).spigot().sendMessage(component);
      } else {
        sender.sendMessage(component.getText());
      }
    } else {
      sender.spigot().sendMessage(component);
    }
  }

  public static void setGlowing(Player player, boolean value) {
    if(Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      player.setGlowing(value);
    }
  }

  public static void setCollidable(Player player, boolean value) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      player.spigot().setCollidesWithEntities(value);
    } else {
      player.setCollidable(value);
    }
  }

  public static void setCollidable(ArmorStand stand, boolean value) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      //stand.spigot().setCollidesWithEntities(value);
    } else {
      stand.setCollidable(value);
    }
  }

  @Deprecated //bad naming
  public static double getHealth(Player player) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      return player.getMaxHealth();
    }

    if(MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).isPresent()) {
      return MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).get().getValue();
    }

    return 20D;
  }

  public static double getMaxHealth(Player player) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      return player.getMaxHealth();
    }

    if(MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).isPresent()) {
      return MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).get().getValue();
    }

    return 20D;
  }

  public static double getMaxHealth(LivingEntity entity) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      return entity.getMaxHealth();
    }

    if(MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_MAX_HEALTH).isPresent()) {
      return MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_MAX_HEALTH).get().getValue();
    }

    return 20D;
  }

  public static void setMaxHealth(Player player, double health) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      player.setMaxHealth(health);
    } else {
      MiscUtils.getEntityAttribute(player, Attribute.GENERIC_MAX_HEALTH).ifPresent(ai -> ai.setBaseValue(health));
    }
  }

  public static void setMaxHealth(LivingEntity entity, double health) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      entity.setMaxHealth(health);
    } else {
      MiscUtils.getEntityAttribute(entity, Attribute.GENERIC_MAX_HEALTH).ifPresent(ai -> ai.setBaseValue(health));
    }
  }

  public static ItemStack getItemInHand(Player player) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      return player.getItemInHand();
    }
    return player.getInventory().getItemInMainHand();
  }

  public static void sendActionBar(Player player, String message) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      try {
        Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);

        Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
        Object packet = constructor.newInstance(icbc, (byte) 2);
        sendPacket(player, packet);
      } catch(ReflectiveOperationException e) {
        e.printStackTrace();
      }
    } else if(Version.isCurrentEqualOrHigher(Version.v1_16_R3)) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, player.getUniqueId(), new ComponentBuilder(message).create());
    } else {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
    }
  }

  public static void sendTitles(Player player, String title, String subtitle, int fadeInTime, int showTime,
                                int fadeOutTime) {
    //avoid null on title
    if(title == null) {
      title = "";
    }
    if(subtitle == null) {
      subtitle = "";
    }
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      sendTitle(player, title, fadeInTime, showTime, fadeOutTime);
      sendSubTitle(player, subtitle, fadeInTime, showTime, fadeOutTime);
    } else {
      player.sendTitle(title, subtitle, fadeInTime, showTime, fadeOutTime);
    }
  }

  public static void sendTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      try {
        Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\"}");

        Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);
        sendPacket(player, packet);
      } catch(Exception ignored) {
      }
    } else {
      player.sendTitle(text, null, fadeInTime, showTime, fadeOutTime);
    }
  }

  public static void sendSubTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime) {
    if(Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      try {
        Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\"}");

        Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);
        sendPacket(player, packet);
      } catch(Exception ignored) {
      }
    } else {
      player.sendTitle(null, text, fadeInTime, showTime, fadeOutTime);
    }
  }

}
