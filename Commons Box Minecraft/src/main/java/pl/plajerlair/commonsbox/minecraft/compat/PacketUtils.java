package pl.plajerlair.commonsbox.minecraft.compat;

import org.bukkit.entity.Player;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class PacketUtils {

  private static Class<?> packetClass;

  static {
    packetClass = classByName("net.minecraft.network.protocol", "Packet");
  }

  public static void sendPacket(Player player, Object packet) {
    try {
      Object handle = player.getClass().getMethod("getHandle").invoke(player);
      Object playerConnection = handle.getClass().getField(
          (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_17_R1) ? "b" : "playerConnection")).get(handle);
      playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
    } catch (ReflectiveOperationException ex) {
      ex.printStackTrace();
    }
  }

  public static Class<?> classByName(String newPackageName, String className) {
    if (ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_17_R1) || newPackageName == null) {
      newPackageName = "net.minecraft.server." + ServerVersion.Version.getPackageVersion()[3];
    }

    try {
      return Class.forName(newPackageName + "." + className);
    } catch(ClassNotFoundException ex) {
      return null;
    }
  }

}
