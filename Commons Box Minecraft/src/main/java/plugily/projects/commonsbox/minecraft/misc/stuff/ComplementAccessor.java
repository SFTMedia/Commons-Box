package plugily.projects.commonsbox.minecraft.misc.stuff;

import plugily.projects.commonsbox.minecraft.compat.ServerVersion;

public final class ComplementAccessor {

	private static Complement complement;

	static {
		boolean kyoriSupported = false;
		try {
			Class.forName("net.kyori.adventure.text.Component");
			org.bukkit.inventory.InventoryView.class.getDeclaredMethod("title");
			kyoriSupported = true;
		} catch (NoSuchMethodException | ClassNotFoundException e) {
		}

		complement = (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R3) && kyoriSupported)
				? new Complement2()
				: new Complement1();
	}

	public static Complement getComplement() {
		return complement;
	}
}
