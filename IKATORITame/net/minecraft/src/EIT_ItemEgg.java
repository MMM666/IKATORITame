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
			pWorld.spawnEntityInWorld(new EIT_EntityEgg(pWorld, pEntityplayer));
		}
		
		return pItemstack;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack par1ItemStack,
			EntityLiving par2EntityLiving) {
		return super.itemInteractionForEntity(par1ItemStack, par2EntityLiving);
	}

}
