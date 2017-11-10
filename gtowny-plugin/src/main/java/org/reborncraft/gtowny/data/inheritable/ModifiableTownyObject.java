package org.reborncraft.gtowny.data.inheritable;


import org.reborncraft.gtowny.data.TownyDataHandler;

public interface ModifiableTownyObject {
	default void pushUpdate() {
		TownyDataHandler.pushObject(this);
	}
}