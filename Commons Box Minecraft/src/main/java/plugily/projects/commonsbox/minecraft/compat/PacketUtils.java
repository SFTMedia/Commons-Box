package plugily.projects.commonsbox.minecraft.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class PacketUtils {

  private static Method playerHandleMethod, sendPacketMethod;
  private static Field playerConnectionField;

  public static void sendPacket(Player player, Object packet) {
    try {
      if (playerHandleMethod == null)
        playerHandleMethod = player.getClass().getMethod("getHandle");

      Object handle = playerHandleMethod.invoke(player);

      if (playerConnectionField == null)
        playerConnectionField = handle.getClass().getField(
                (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_17_R1) ? "b" : "playerConnection"));

      Object playerConnection = playerConnectionField.get(handle);

      if (sendPacketMethod == null)
        sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", classByName("net.minecraft.network.protocol", "Packet"));

      sendPacketMethod.invoke(playerConnection, packet);
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
