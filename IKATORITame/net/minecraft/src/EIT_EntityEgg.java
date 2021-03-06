package net.minecraft.src;

public class EIT_EntityEgg extends EntityThrowable {

	public EIT_EntityEgg(World world) {
		super(world);
	}

	public EIT_EntityEgg(World world, EntityLivingBase entityliving) {
		super(world, entityliving);
	}

	public EIT_EntityEgg(World world, double d, double d1, double d2) {
		super(world, d, d1, d2);
	}

	protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
		if (par1MovingObjectPosition.entityHit != null) {
			par1MovingObjectPosition.entityHit.attackEntityFrom(
					DamageSource.causeThrownDamage(this, getThrower()), 0);
		}
		
		if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0) {
			byte var2 = 1;
			
			if (this.rand.nextInt(32) == 0) {
				var2 = 4;
			}
			
			for (int var3 = 0; var3 < var2; ++var3) {
				EIT_EntityChicken var4 = new EIT_EntityChicken(this.worldObj);
				var4.setGrowingAge(-24000);
				var4.setLocationAndAngles(this.posX, this.posY, this.posZ,
						this.rotationYaw, 0.0F);
				this.worldObj.spawnEntityInWorld(var4);
			}
		}
		
		for (int var5 = 0; var5 < 8; ++var5) {
			this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY,
					this.posZ, 0.0D, 0.0D, 0.0D);
		}
		
		if (!this.worldObj.isRemote) {
			this.setDead();
		}
	}

}
