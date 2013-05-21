package net.minecraft.src;

import cpw.mods.fml.common.registry.IThrowableEntity;

public class EIT_EntityEgg_Forge extends EIT_EntityEgg implements IThrowableEntity {

	public EIT_EntityEgg_Forge(World world) {
		super(world);
	}

	public EIT_EntityEgg_Forge(World world, EntityLiving entityliving) {
		super(world, entityliving);
	}

	public EIT_EntityEgg_Forge(World world, double d, double d1, double d2) {
		super(world, d, d1, d2);
	}

	@Override
	public void setThrower(Entity entity) {
		// TODO Auto-generated method stub
	}

}
