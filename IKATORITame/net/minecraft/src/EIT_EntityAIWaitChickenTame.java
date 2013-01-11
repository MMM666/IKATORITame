package net.minecraft.src;

public class EIT_EntityAIWaitChickenTame extends EntityAIBase {

	private EIT_EntityChicken ownerEntity;

	
	public EIT_EntityAIWaitChickenTame(EIT_EntityChicken parEntity) {
		super();
		
		ownerEntity = parEntity;
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		return ownerEntity.isIncubator();
	}

	@Override
	public void updateTask() {
       	ownerEntity.getNavigator().clearPathEntity();
	}
}
