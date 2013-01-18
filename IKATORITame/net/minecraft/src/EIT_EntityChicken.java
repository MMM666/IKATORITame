package net.minecraft.src;

import java.util.List;

public class EIT_EntityChicken extends EntityChicken {

	protected final int flag_isTamed		= 4;
	protected final int flag_isIncubate		= 8;
	protected final int flag_isFullfrontal	= 16;
	protected final int flag_isHPMax		= 32;
	
	
	private ItemStack incubatoregg;
	private boolean doGride;
	private float grideTime;
	private boolean tamed;
	private EntityPlayer targetplayer;
	protected String textureChick;
	protected String textureChickenFrontal;
	public boolean fFullFrontal;
	public float ridingHeight;

	public EIT_EntityChicken(World world) {
		super(world);
		
		health = 4;
		incubatoregg = null;
		doGride = false;
		grideTime = 0.0F;
		tamed = false;
		targetplayer = null;
		textureChick = "/mob/chick.png";
		textureChickenFrontal = "/mob/chicken_frontal.png";
		setFullFrontal(false);
		
		try {
			// 既存のAIをクリア
			List llist = (List) ModLoader.getPrivateValue(EntityAITasks.class, tasks, 0);
			llist.clear();
		} catch (Error e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		float f = 0.25F;
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EIT_EntityAIWaitChickenTame(this));
		tasks.addTask(2, new EntityAIPanic(this, 0.38F));
		tasks.addTask(3, new EntityAIMate(this, f));
		tasks.addTask(4, new EIT_EntityAIEatFoodChickenTame(this, f));
		tasks.addTask(5, new EIT_EntityAITemptChickenTame(this, 0.25F, Item.seeds.itemID, false, 0.0D));
		tasks.addTask(6, new EntityAIFollowParent(this, 0.28F));
		tasks.addTask(7, new EntityAIWander(this, f));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6F));
		tasks.addTask(9, new EntityAILookIdle(this));

	}

	@Override
	protected void entityInit() {
		super.entityInit();
		// リアルタイムフラグ
		// 00000100 : Tame
		// 00001000 : Incubator
		// 00010000 : FullFrontal
		// 00100000 : HPMax
		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
		// mounted height
		this.dataWatcher.addObject(17, Integer.valueOf(0));
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Tamed", isTamed());
		nbttagcompound.setBoolean("FullFrontal", isFullFrontal());
		nbttagcompound.setInteger("NextEgg", timeUntilNextEgg);
		if (isIncubator()) {
			nbttagcompound.setCompoundTag("Incubator", getIncubator()
					.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setTamed(nbttagcompound.getBoolean("Tamed"));
		setFullFrontal(nbttagcompound.getBoolean("FullFrontal"));
		timeUntilNextEgg = nbttagcompound.getInteger("NextEgg");
		setIncubator(ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("Incubator")));
	}

	@Override
	protected boolean isMovementCeased() {
		// 移動抑制
		return isShitting();
	}

	@Override
	protected boolean canDespawn() {
		return isTamed() ? false : super.canDespawn();
	}

	@Override
	public double getYOffset() {
		if (ridingEntity != null) {
			if (ridingEntity instanceof EntityPlayer) {
				return (double) (yOffset - 1.1F);
			}
			if (ridingEntity instanceof EntitySquid) {
				return (double) (yOffset + 0.0F);
			}
			if (ridingEntity instanceof EntityChicken) {
				// 足の非表示による高さ変更の回避
				if (ridingEntity.ridingEntity == null) {
					return (double) (yOffset) + 0.15D;
				} else {
					return (double) (yOffset - 0.15F);
				}
			}
		}
		return super.getYOffset();
	}

	@Override
	public double getMountedYOffset() {
		if (riddenByEntity instanceof EntityPlayer) {
			return (double) height * 0.90000000000000002D;
		}
		return super.getMountedYOffset();
	}

	@Override
	public String getTexture() {
		// ひよこのテクスチャは別
		return (mod_EIT_IKATORITame.isChickTexture && isChild()) ? textureChick
				: fFullFrontal ? textureChickenFrontal : texture;
	}

	@Override
	protected int getDropItemId() {
		return isFullFrontal() ? 0 : Item.feather.itemID;
	}

	@Override
	protected void dropFewItems(boolean flag, int i) {
		int j = isFullFrontal() ? 0 : rand.nextInt(3) + rand.nextInt(1 + i);
		for (int k = 0; k < j; k++) {
			dropItem(Item.feather.itemID, 1);
		}

		if (isBurning()) {
			dropItem(Item.chickenCooked.itemID, 1);
		} else {
			dropItem(Item.chickenRaw.itemID, 1);
		}
		if (incubatoregg != null) {
			entityDropItem(incubatoregg, 0.0F);
		}
	}

	@Override
	public EntityChicken spawnBabyAnimal(EntityAgeable par1EntityAgeable) {
		return new EIT_EntityChicken(worldObj);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		// なでり判定
		if (entityplayer != null) {
			ItemStack itemstack = entityplayer.getCurrentEquippedItem();
			if (!worldObj.isRemote) {
				// むしる
				if (itemstack != null
						&& itemstack.getItem() instanceof ItemShears) {
					if (canFrontal()) {
						setFullFrontal(true);
						int i = 2 + rand.nextInt(3);
						for (int j = 0; j < i; j++) {
							EntityItem entityitem = dropItem(Item.feather.itemID, 1);
							entityitem.motionY += rand.nextFloat() * 0.05F;
							entityitem.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
							entityitem.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						}
						itemstack.damageItem(1, entityplayer);
						// 生え変わりカウンタ
						setGrowingAge(36000);
					}
					return true;
				}
				
				// RIDE-OFF
				if (entityplayer.ridingEntity == this) {
					entityplayer.mountEntity(this);
					return true;
				}
				if (itemstack == null && isTamed()) {
					// 無手
					// RIDE-ON
					Entity entity1 = entityplayer;
					if (entity1.riddenByEntity != this) {
						for (; entity1.riddenByEntity != null; entity1 = entity1.riddenByEntity) {
						}
					}
					if (isIncubator() && ridingEntity == null) {
						// 待機解除で卵をドロップ
						entityDropItem(getIncubator(), 0.0F);
						setIncubator(null);
					}
					mountEntity(entity1);
					return true;
				}
				
			} else {
				// Client
			}
			if (itemstack != null && itemstack.stackSize > 0) {
				// 餌
				if (itemstack.getItem() instanceof ItemSeeds) {
					if (eatBeans()) {
						if (!super.interact(entityplayer)) {
							if (!entityplayer.capabilities.isCreativeMode) {
								if (--itemstack.stackSize <= 0) {
									entityplayer.inventory.setInventorySlotContents(
											entityplayer.inventory.currentItem, null);
								}
							}
						}
						if (isTamed()) {
							showLoveOrHappyFX(false);
						} else {
							setTamed(true);
							showLoveOrHappyFX(true);
						}
					}
					return true;
				}
				if (isTamed()) {
					// 待機
					if (itemstack.getItem() instanceof ItemEgg) {
						if (!isIncubator()) {
							setIncubator(itemstack.splitStack(1));
							if (itemstack.stackSize <= 0) {
								entityplayer.inventory.setInventorySlotContents(
										entityplayer.inventory.currentItem, null);
							}
							return true;
						}
					} else if (itemstack.itemID == Item.feather.itemID) {
						// RIDE-ON
						// トリに乗る
						if (!entityplayer.capabilities.isCreativeMode) {
							if (--itemstack.stackSize <= 0) {
								entityplayer.inventory.setInventorySlotContents(
										entityplayer.inventory.currentItem, null);
							}
						}
						if (worldObj.isRemote) {
							entityplayer.mountEntity(this);
						}
						return true;
					}
				}
			}
			if (isIncubator()) {
				// 卵の開放
				if (!worldObj.isRemote) {
					entityDropItem(getIncubator(), 0.0F);
				}
				setIncubator(null);
				return true;
			}
		}
		return super.interact(entityplayer);
	}

	@Override
	public void onLivingUpdate() {
		// Ride-onで接地判定
		if (isRiding() && mod_EIT_IKATORITame.isChickenGride && ridingEntity != null) {
			// グライド判定
			Entity entity;
			for (entity = ridingEntity; entity.ridingEntity != null; entity = entity.ridingEntity) {
			}
			onGround = entity.onGround | (!doGride);
			if (!onGround && doGride && entity.motionY < 0.0D && !fFullFrontal) {
				entity.motionY *= 0.78000000000000003D;
				grideTime -= 0.30000001192092896D - entity.motionY * 3D;
				if (grideTime < 0.0F) {
					doGride = false;
				}
			}
			rotationYaw = entity.rotationYaw;
			if (entity.motionY > -0.5D) {
				// 落下ダメージキャンセル
				entity.updateFallState(1, true);
			}
		}
		
		if (!worldObj.isRemote) {
			// 産卵判定の横取り
			if (!isChild() && timeUntilNextEgg <= 1) {
				worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F,
						(rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
				dropItem(Item.egg.itemID, 1);
				// ライフが多いと産みがいい
				if (setNextLayEgg()) {
					health--;
				}
			}
			// 羽が生えた
			if (fFullFrontal && getGrowingAge() == 0) {
				System.out.println("Growup feather.");
				setFullFrontal(false);
			}
			// System.out.println(String.format("%d", getGrowingAge()));
			//
		}
		if (grideTime < (float) (health * 2)) {
			grideTime += 0.3F;
		} else {
			doGride = true;
		}

		float bk_wingRot = field_70886_e;
		float bk_destPos = destPos;
		float bk_field_755_h = field_70889_i;
		double bk_motionY = motionY;
		boolean bk_onGround = onGround;
		
		super.onLivingUpdate();
		
		if (isRiding()) {
			// 羽ばたきキャンセル
			field_70886_e = bk_wingRot;
			destPos = bk_destPos;
			field_70889_i = bk_field_755_h;
			motionY = bk_motionY;
			onGround = bk_onGround;
			
			field_70888_h = field_70886_e;
			field_70884_g = destPos;
			destPos += (double) (onGround ? -1 : 4) * 0.3D;
			if (destPos < 0.0F) {
				destPos = 0.0F;
			}
			if (destPos > 1.0F) {
				destPos = 1.0F;
			}
			if (!onGround && field_70889_i < 1.0F) {
				field_70889_i = 1.0F;
			}
			field_70889_i *= 0.9D;
			if (!onGround && motionY < 0.0D) {
				motionY *= 0.6D;
			}
			field_70886_e += field_70889_i * 2.0F;
		}
		if (fFullFrontal) {
			// 羽が無ければ当然飛べない
			if (!onGround && motionY < 0.0D) {
				motionY *= 1.67D;
			}
		}
	}

	@Override
	protected void fall(float f) {
		// 落下ダメージ
		if (fFullFrontal) {
			if (riddenByEntity != null) {
				riddenByEntity.fall(f);
			}
			int i = (int) Math.ceil(f - 3F);
			if (i > 0) {
				if (i > 4) {
					worldObj.playSoundAtEntity(this, "damage.fallbig", 1.0F,
							1.0F);
				} else {
					worldObj.playSoundAtEntity(this, "damage.fallsmall", 1.0F,
							1.0F);
				}
				attackEntityFrom(DamageSource.fall, i);
				int j = worldObj.getBlockId(
						MathHelper.floor_double(posX),
						MathHelper.floor_double(posY - 0.20000000298023224D
								- (double) yOffset),
						MathHelper.floor_double(posZ));
				if (j > 0) {
					StepSound stepsound = Block.blocksList[j].stepSound;
					worldObj.playSoundAtEntity(this, stepsound.getStepSound(),
							stepsound.getVolume() * 0.5F,
							stepsound.getPitch() * 0.75F);
				}
			}
		}
	}

	public void showLoveOrHappyFX(boolean flag) {
		// アイコン表示
		if (flag) {
			// Love
			for (int i = 0; i < 7; i++) {
				double d1 = rand.nextGaussian() * 1.0D;
				double d3 = rand.nextGaussian() * 1.0D;
				double d5 = rand.nextGaussian() * 1.0D;
				worldObj.spawnParticle("heart",
						(posX + (double) (rand.nextFloat() * width * 2.0F)) - (double) width,
						posY + 0.5D + (double) (rand.nextFloat() * height),
						(posZ + (double) (rand.nextFloat() * width * 2.0F)) - (double) width, d1, d3, d5);
			}
		} else {
			// Happy
			double d = rand.nextGaussian() * 1.0D;
			double d2 = rand.nextGaussian() * 1.0D;
			double d4 = rand.nextGaussian() * 1.0D;
			worldObj.spawnParticle("note", posX, posY + (double) height + 0.1D, posZ, d, d2, d4);
		}
	}

	// 飼い慣らし
	public boolean isTamed() {
		return getMyFlag(flag_isTamed);
	}

	public void setTamed(boolean flag) {
		setMyFlag(flag_isTamed, flag);
	}

	public boolean isShitting() {
		// 座っているか？
		return isIncubator() || isRiding();
	}

	public boolean isIncubator() {
		// 托卵状態か？
		return getMyFlag(flag_isIncubate);
	}

	public ItemStack getIncubator() {
		// 卵は？
		return incubatoregg;
	}

	public boolean setIncubator(ItemStack itemstack) {
		// 托卵
		if (itemstack != null && (itemstack.getItem() instanceof ItemEgg)) {
			incubatoregg = itemstack;
			setMyFlag(flag_isIncubate, true);
			return true;
		} else {
			incubatoregg = null;
			setMyFlag(flag_isIncubate, false);
			return false;
		}
	}

	public boolean canFrontal() {
		// 羽を毟れるか
		return mod_EIT_IKATORITame.isPlucked && !isChild() && !isFullFrontal();
	}

	public boolean isFullFrontal() {
		return getMyFlag(flag_isFullfrontal);
	}

	public void setFullFrontal(boolean flag) {
		setMyFlag(flag_isFullfrontal, flag);
	}

	public boolean isHPMax() {
		return getMyFlag(flag_isHPMax);
	}

	public boolean updateHPMax() {
		if (worldObj.isRemote) {
			return isHPMax();
		}
		boolean lflag = health >= getMaxHealth();
		if (getMyFlag(flag_isHPMax) != lflag) {
			setMyFlag(flag_isHPMax, lflag);
		}
		return lflag;
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

	public boolean eatBeans() {
		if (attackTime > 0 || isHPMax()) {
			return false;
		}
		if (isChild() || isFullFrontal()) {
			// えさを食べると成長早い
			setGrowingAge((int) ((float) getGrowingAge() * 0.9F));
		}
		heal(1);
		worldObj.playSoundAtEntity(this, "random.pop", 0.2F,
				((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		// ライフが多いと産みがいい
		setNextLayEgg();
		attackTime = 5;
		return true;
	}

	public boolean setNextLayEgg() {
		// 卵を産む間隔
		timeUntilNextEgg = rand.nextInt(6000) + 6000;
		if (health > 4) {
			timeUntilNextEgg = (16 * timeUntilNextEgg) / (5 * health - 4);
			return true;
		} else {
			return false;
		}
	}

	public void chickenOnLivingUpdate() {
	}

	@Override
	public void onUpdate() {
		fFullFrontal = isFullFrontal();
		if (!worldObj.isRemote) {
			// Server
			if (ridingEntity != null) {
				if (ridingEntity.height != ridingHeight) {
					ridingHeight = ridingEntity.height;
					dataWatcher.updateObject(17, Float.floatToIntBits(ridingHeight));
				}
			}
		} else {
			ridingHeight = Float.intBitsToFloat(dataWatcher.getWatchableObjectInt(17));
		}
		updateHPMax();
		super.onUpdate();
		updateHPMax();
	}

}
