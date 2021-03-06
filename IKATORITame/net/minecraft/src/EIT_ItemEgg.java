package net.minecraft.src;

public class EIT_ItemEgg extends ItemEgg {

	public EIT_ItemEgg(int i) {
		super(i);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack pItemstack, World pWorld,
			EntityPlayer pEntityplayer) {
		if (!pEntityplayer.capabilities.isCreativeMode) {
			--pItemstack.stackSize;
		}
		
		pWorld.playSoundAtEntity(pEntityplayer, "random.bow", 0.5F,
				0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		
		if (!pWorld.isRemote) {
			try {
				EIT_EntityEgg lentity = (EIT_EntityEgg)mod_EIT_IKATORITame.classEggEntity.getConstructor(World.class, EntityLivingBase.class).newInstance(pWorld, pEntityplayer);
				pWorld.spawnEntityInWorld(lentity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return pItemstack;
	}

}
