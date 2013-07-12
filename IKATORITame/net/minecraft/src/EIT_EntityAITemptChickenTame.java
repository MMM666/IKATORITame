package net.minecraft.src;

public class EIT_EntityAITemptChickenTame extends EntityAITempt {

	private EIT_EntityChicken temptedEntity;
	private float field_75282_b;
	private EntityPlayer temptingPlayer;
	private int breedingFood;
	private boolean scaredByPlayerMovement;
	private double range;

	public EIT_EntityAITemptChickenTame(EIT_EntityChicken par1EntityCreature,
			float par2, int par3, boolean par4, double pStorprange) {
		super(par1EntityCreature, par2, par3, par4);
		temptedEntity = par1EntityCreature;
		field_75282_b = par2;
		breedingFood = par3;
		scaredByPlayerMovement = par4;
		range = pStorprange;
	}

	@Override
	public boolean shouldExecute() {
		if (temptedEntity.attackTime > 0
				|| temptedEntity.func_110143_aJ() >= temptedEntity.func_110138_aP()) {
			return false;
		}
		boolean lflag = super.shouldExecute();
		try {
			temptingPlayer = (EntityPlayer) ModLoader.getPrivateValue(
					EntityAITempt.class, this, 7);
		} catch (Exception e) {
		}
		return lflag;
	}

	@Override
	public void updateTask() {
		temptedEntity.getLookHelper().setLookPositionWithEntity(temptingPlayer,
				30F, temptedEntity.getVerticalFaceSpeed());
		
		if (temptedEntity.getDistanceSqToEntity(temptingPlayer) < (temptedEntity
				.isTamed() ? range : 6.25D)) {
			temptedEntity.getNavigator().clearPathEntity();
		} else {
			temptedEntity.getNavigator().tryMoveToEntityLiving(temptingPlayer,
					field_75282_b);
		}
	}

}
