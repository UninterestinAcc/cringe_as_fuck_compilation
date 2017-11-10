package org.reborncraft.gtowny.cmds;

import org.reborncraft.gtowny.data.internal.TownPermissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface GTownySubcommand {
	boolean requirePlayer() default true;

	boolean requireTownOwner() default false;

	TownPermissions requireTownPermission() default TownPermissions.Member;

	boolean requireChunkOwner() default false;

	boolean chunkTownMustBeSame() default false;

	boolean requireInTown() default false;
}
