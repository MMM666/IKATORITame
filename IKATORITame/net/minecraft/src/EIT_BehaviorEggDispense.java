package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class EIT_BehaviorEggDispense extends BehaviorEggDispense {

	public EIT_BehaviorEggDispense(MinecraftServer par1MinecraftServer) {
		super(par1MinecraftServer);
	}

	@Override
	protected IProjectile getProjectileEntity(World par1World, IPosition par2iPosition) {
        return new EIT_EntityEgg(par1World, par2iPosition.getX(), par2iPosition.getY(), par2iPosition.getZ());
	}
	
}
