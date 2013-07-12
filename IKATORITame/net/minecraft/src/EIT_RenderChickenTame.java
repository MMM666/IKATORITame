package net.minecraft.src;

public class EIT_RenderChickenTame extends RenderChicken {

	protected static final ResourceLocation ftexChick = new ResourceLocation("textures/entity/chick.png");
	protected static final ResourceLocation ftexFrontal = new ResourceLocation("textures/entity/chicken_frontal.png");

	public EIT_RenderChickenTame() {
		super(new ModelChicken(), 0.3F);
	}

	@Override
	public void renderChicken(EntityChicken entitychicken, double d, double d1,
			double d2, float f, float f1) {
		// 追記
		if (((EIT_EntityChicken) entitychicken).isShitting()) {
			// 足消し、位置下げ
			((ModelChicken) mainModel).rightLeg.showModel = false;
			((ModelChicken) mainModel).leftLeg.showModel = false;
			// d1 -= 0.29999999999999999D;
			d1 -= ((EIT_EntityChicken) entitychicken).isChild() ? 0.15D : 0.3D;
		}
		
		super.renderChicken(entitychicken, d, d1, d2, f, f1);
		
		// 後始末
		((ModelChicken) mainModel).rightLeg.showModel = true;
		((ModelChicken) mainModel).leftLeg.showModel = true;
	}

	@Override
	protected ResourceLocation func_110919_a(EntityChicken par1EntityChicken) {
		// ひよこのテクスチャは別
		EIT_EntityChicken lentity = (EIT_EntityChicken)par1EntityChicken;
		return (mod_EIT_IKATORITame.isChickTexture && lentity.isChild()) ? ftexChick
				: lentity.isFullFrontal() ? ftexFrontal
						: super.func_110919_a(par1EntityChicken);
	}

}
