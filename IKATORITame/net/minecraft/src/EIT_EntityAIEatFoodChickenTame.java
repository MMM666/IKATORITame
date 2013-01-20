package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EIT_EntityAIEatFoodChickenTame extends EntityAIBase {

	// private int eatInterval;
	private float pathDelta;
	private EIT_EntityChicken ownerEntity;
	private EntityItem targetEntity;
	private World theWorld;
	private Random rand;

	public EIT_EntityAIEatFoodChickenTame(EIT_EntityChicken parEntity,
			float parDelta) {
		super();
		setMutexBits(3);

		// eatInterval = 0;
		pathDelta = parDelta;
		ownerEntity = parEntity;
		targetEntity = null;
		theWorld = parEntity.worldObj;
		rand = parEntity.rand;
	}

	@Override
	public boolean shouldExecute() {
		if (ownerEntity.health < ownerEntity.getMaxHealth()) {
			targetEntity = findFood();
		} else {
			targetEntity = null;
		}

		return targetEntity != null;
	}

	@Override
	public boolean continueExecuting() {
		return (targetEntity != null && targetEntity.isEntityAlive())
				|| ownerEntity.attackTime > 0;
	}

	@Override
	public void resetTask() {
		targetEntity = null;
	}

	@Override
	public void updateTask() {
		if (ownerEntity.getDistanceSqToEntity(targetEntity) < 0.5D) {
			ownerEntity.getNavigator().clearPathEntity();
			if (ownerEntity.attackTime <= 0) {
				if (ownerEntity.eatBeans()) {
					ownerEntity.getLookHelper().setLookPositionWithEntity(
							targetEntity, 10F, ownerEntity.getVerticalFaceSpeed());
					ownerEntity.attackTime = 5;
					ItemStack lstack = targetEntity.getEntityItem();
					if (--lstack.stackSize <= 0) {
						targetEntity.setDead();
					} else {
						targetEntity.func_92058_a(lstack);
					}
				} else {
					targetEntity = null;
				}
			}
		} else {
			ownerEntity.getNavigator().tryMoveToXYZ(
					targetEntity.posX, targetEntity.posY, targetEntity.posZ, pathDelta);
			ownerEntity.getLookHelper().setLookPositionWithEntity(
					targetEntity, 10F, ownerEntity.getVerticalFaceSpeed());
		}
	}

	private EntityItem findFood() {
		float f = 16F;
		List list = theWorld.getEntitiesWithinAABB(EntityItem.class,
				ownerEntity.boundingBox.expand(f, f, f));

		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Entity entity = (Entity) iterator.next();
			EntityItem entityitem = (EntityItem) entity;
			ItemStack litemstack = entityitem.getEntityItem();

			if (litemstack.stackSize > 0
					&& (litemstack.itemID == Item.wheat.itemID
					|| litemstack.getItem() instanceof ItemSeeds)) {
				return entityitem;
			}
		}

		return null;
	}

}
