package net.minecraft.src;

import java.util.List;

public class EIT_EntitySquid extends EntitySquid implements EntityOwnable {

	public EIT_EntitySquid(World world) {
		super(world);
		// System.out.println("tamedSquid.");
		setEntityHealth(10F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		// ���A���^�C���t���O

		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
		// ownerName
		this.dataWatcher.addObject(17, "");
	}

	protected void func_110147_ax() {
		super.func_110147_ax();
		// �ő�HP
		this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0D);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Tamed", isTamed());
		nbttagcompound.setString("Owner", getOwnerName());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setOwner(nbttagcompound.getString("Owner"));
		if (getOwnerName().isEmpty() && nbttagcompound.getBoolean("Tamed")) {
			// �Â��̑΍�
			setOwner("notch");
		}
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		return squidInteract(entityplayer);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!worldObj.isRemote) {
			if (isInWater()) {
				// �ǋL�F�����ɏ���Ă��鎞�͏c�u��
				if (ridingEntity == null) {
					// field_70861_d += (double)(-90F - field_70861_d) * 0.02D;
				} else {
					rotationYaw = ridingEntity.rotationYaw;
					tentacleAngle = 0.0F;
					onGround = ridingEntity.onGround;
				}
			}
		} else {
		}
	}

	@Override
	protected void updateEntityActionState() {
		if (!worldObj.isRemote) {
			squidUpdateEntityActionState();
		} else {
			super.updateEntityActionState();
		}
	}

	// �ǉ���
	@Override
	protected boolean isAIEnabled() {
		return false;
	}

	@Override
	protected boolean canDespawn() {
		return isTamed() ? false : super.canDespawn();
	}

	@Override
	public double getYOffset() {
		if (ridingEntity != null) {
			if (ridingEntity instanceof EntityPlayer) {
				return (double) (yOffset - 1.0F);
			}
			if (ridingEntity instanceof EntityChicken) {
				return (double) (yOffset - 0.05F);
			}
			if (ridingEntity instanceof EntitySquid) {
				return (double) (yOffset + 0.28F);
			}
		}
		return super.getYOffset();
	}

	@Override
	public double getMountedYOffset() {
		return super.getMountedYOffset();
	}

	public void showLoveOrHappyFX(boolean flag) {
		if (flag) {
			// Love
			for (int i = 0; i < 7; i++) {
				double d1 = rand.nextGaussian() * 1.0D;
				double d3 = rand.nextGaussian() * 1.0D;
				double d5 = rand.nextGaussian() * 1.0D;
				worldObj.spawnParticle("heart",
						(posX + (double) (rand.nextFloat() * width * 2.0F))
								- (double) width,
						posY + 0.5D + (double) (rand.nextFloat() * height),
						(posZ + (double) (rand.nextFloat() * width * 2.0F))
								- (double) width, d1, d3, d5);
			}
		} else {
			// Happy
			double d = rand.nextGaussian() * 1.0D;
			double d2 = rand.nextGaussian() * 1.0D;
			double d4 = rand.nextGaussian() * 1.0D;
			worldObj.spawnParticle("note", posX, posY + (double) height
					+ 0.10000000000000001D, posZ, d, d2, d4);
		}
	}

	// �����Ȃ炵
	public boolean isTamed() {
		return !getOwnerName().isEmpty();
	}

	@Override
	public String getOwnerName() {
		return dataWatcher.getWatchableObjectString(17);
	}

	public void setOwner(String par1Str) {
		this.dataWatcher.updateObject(17, par1Str);
	}

	@Override
	public Entity getOwner() {
		return this.worldObj.getPlayerEntityByName(this.getOwnerName());
	}


	public boolean getMyFlag(int pindex) {
		return (this.dataWatcher.getWatchableObjectByte(16) & pindex) != 0;
	}

	public void setMyFlag(int pindex, boolean pflag) {
		if (worldObj.isRemote)
			return;
		byte lval = this.dataWatcher.getWatchableObjectByte(16);

		if (pflag) {
			this.dataWatcher.updateObject(16,
					Byte.valueOf((byte) (lval | pindex)));
		} else {
			this.dataWatcher.updateObject(16,
					Byte.valueOf((byte) (lval & ~pindex)));
		}
	}

	public boolean eatFish() {
		// ����H�ׂĉ�
		if (attackTime > 0 || func_110143_aJ() > func_110138_aP()) {
			return false;
		}
		heal(((ItemFood) Item.fishRaw).getHealAmount());
		playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		attackTime = 15;
		return true;
	}

	public boolean squidInteract(EntityPlayer entityplayer) {
		// �G�������̔���
		if (entityplayer != null) {
			ItemStack itemstack = entityplayer.getCurrentEquippedItem();
			if (!worldObj.isRemote) {
				// Server
				if (itemstack == null && isTamed()) {
					// ����
					// RIDE-OFF
					if (entityplayer.ridingEntity == this) {
						entityplayer.mountEntity(this);
						return true;
					}
					// RIDE-ON
					Entity entity1 = entityplayer;
					if (entity1.riddenByEntity != this) {
						while (entity1.riddenByEntity != null) {
							entity1 = entity1.riddenByEntity;
						}
					}
					// �Ȃ񂩍~����Ȃ��̂�
					if (ridingEntity == entityplayer) {
						mountEntity(null);
					} else {
						mountEntity(entity1);
					}
					return true;
				}
			}
			if (itemstack != null && itemstack.stackSize > 0) {
				// �����i
				if (itemstack.itemID == Item.fishRaw.itemID) {
					if (eatFish()) {
						if (!super.interact(entityplayer)) {
							if (!entityplayer.capabilities.isCreativeMode) {
								if (--itemstack.stackSize <= 0) {
									entityplayer.inventory
											.setInventorySlotContents(
													entityplayer.inventory.currentItem,
													(ItemStack) null);
								}
							}
						}
						if (isTamed()) {
							showLoveOrHappyFX(false);
						} else {
							showLoveOrHappyFX(true);
						}
						// �K�y
						setOwner(entityplayer.username);
					} else {
						if (!worldObj.isRemote) {
							this.entityDropItem(new ItemStack(Item.dyePowder,
									1, 0), 0.0F);
							;
						}
					}
					return true;
				}
				if (isTamed() && itemstack.itemID == Item.dyePowder.itemID
						&& itemstack.getItemDamage() == 0) {
					// RIDE-ON
					// �C�J�ɏ��
					if (!entityplayer.capabilities.isCreativeMode) {
						if (--itemstack.stackSize <= 0) {
							entityplayer.inventory.setInventorySlotContents(
									entityplayer.inventory.currentItem,
									(ItemStack) null);
						}
					}
					entityplayer.mountEntity(this);
					return true;
				}
			}
		}
		return super.interact(entityplayer);
	}

	public void squidUpdateEntityActionState() {
		Entity entity = null;
		// ���̒T��
		double d = -1D;
		List list = worldObj.getEntitiesWithinAABB(
				net.minecraft.src.EntityItem.class,
				boundingBox.copy().expand(16D, 16D, 16D));
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity1 = (Entity) list.get(i);
				// TODO:�A�C�e���̐��v���肪�����Ȃ��Ă�̂ł����ő�s
				boolean lflag = this.worldObj.handleMaterialAcceleration(
						entity1.boundingBox, Material.water, entity1);
				if (!entity1.isDead
						&& lflag
//						&& entity1.isInWater()
						&& (entity1 instanceof EntityItem)
						&& ((EntityItem) entity1).getEntityItem().itemID == Item.fishRaw.itemID) {
					double d2 = getDistanceSqToEntity(entity1);
					if (d == -1D || d2 < d) {
						d = d2;
						entity = entity1;
					}
				}
			}
			
			if (entity != null && d < 1.0D
					&& ((EntityItem) entity).delayBeforeCanPickup <= 0
					&& eatFish()) {
				// �A�C�e���ɐڐG
				ItemStack lstack = ((EntityItem) entity).getEntityItem();
				if (--lstack.stackSize <= 0) {
					entity.setDead();
				} else {
					((EntityItem) entity).setEntityItemStack(lstack);
				}
				entity = null;
			}
		}
		if (entity == null && isTamed()) {
			double d1 = -1D;
			List list1 = worldObj.loadedEntityList;
			for (int j = 0; j < list1.size(); j++) {
				Entity entity2 = (Entity) list1.get(j);
				if ((entity2 instanceof EntityPlayer) && entity2.inWater == inWater) {
					ItemStack itemstack = ((EntityPlayer) entity2).getCurrentEquippedItem();
					if (itemstack != null && itemstack.itemID == Item.fishRaw.itemID) {
						// ���̒��̋����������v���[���[�Ɍ�����
						double d3 = getDistanceSqToEntity(entity2);
						if (d1 == -1D || d3 < d1) {
							d1 = d3;
							entity = entity2;
						}
					}
				}
			}
		}
		if (entity != null) {
			try {
				float randomMotionSpeed = (Float) ModLoader.getPrivateValue(
						EntitySquid.class, this, 8);
				if (randomMotionSpeed == 1.0F) {
					float randomMotionVecX;
					float randomMotionVecY;
					float randomMotionVecZ;
					
					// ���Ɍ������Ă��[
					double d4 = entity.posX - posX;
					double d5 = entity.posY - posY
							- (double) entity.getEyeHeight();
					double d6 = entity.posZ - posZ;
					double d7 = MathHelper.sqrt_double(d4 * d4 + d5 * d5 + d6
							* d6);
					if (!worldObj.getBlockMaterial(
							MathHelper.floor_double(posX + d4 / d7),
							MathHelper.floor_double(posY),
							MathHelper.floor_double(posZ + d6 / d7)).isSolid()) {
						randomMotionVecX = (float) (d4 / d7) * 0.2F;
						randomMotionVecY = (float) (d5 / d7) * 0.2F;
						randomMotionVecZ = (float) (d6 / d7) * 0.2F;
					} else {
						// ��Q�����L�鎞�͏ォ���ɉ��
						randomMotionVecX = 0.0F;
						randomMotionVecY = d5 <= 0.0D ? -0.2F : 0.2F;
						randomMotionVecZ = 0.0F;
					}
					ModLoader.setPrivateValue(EntitySquid.class, this, 11,
							Float.valueOf(randomMotionVecX));
					ModLoader.setPrivateValue(EntitySquid.class, this, 12,
							Float.valueOf(randomMotionVecY));
					ModLoader.setPrivateValue(EntitySquid.class, this, 13,
							Float.valueOf(randomMotionVecZ));
				}
			} catch (Exception e) {
			}
			entityAge++;
			despawnEntity();
		} else {
			super.updateEntityActionState();
		}
	}

}
