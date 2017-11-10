/*
 * Copyright (c) CraftInSpace (craftin.space) 2016. All rights reserved.
 */

package space.craftin.plugin.core.api.physical.vehicles;

import space.craftin.plugin.core.impl.core.utils.AxisAlignedBB;

public interface IDrone extends IVehicle {
	AxisAlignedBB getOperatingArea();

	boolean setOperatingArea(AxisAlignedBB aabb);

	DroneType getType();

	void setType(DroneType type);

	enum DroneType {
		MINER, DEFENDER, GENERIC;

		public static DroneType fromString(String type) {
			for (DroneType droneType : values()) {
				if (droneType.toString().equalsIgnoreCase(type)) {
					return droneType;
				}
			}
			return GENERIC;
		}
	}
}
