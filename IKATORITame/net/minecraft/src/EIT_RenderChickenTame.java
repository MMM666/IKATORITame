package net.minecraft.src;

public class EIT_RenderChickenTame extends RenderChicken {

    public EIT_RenderChickenTame(ModelBase modelbase, float f) {
        super(modelbase, f);
    }

    @Override
    public void renderChicken(EntityChicken entitychicken, double d, double d1, double d2, 
            float f, float f1) {
    	// �ǋL
        if (((EIT_EntityChicken)entitychicken).isShitting()) {
    		// �������A�ʒu����
            ((ModelChicken)mainModel).rightLeg.showModel = false;
            ((ModelChicken)mainModel).leftLeg.showModel = false;
//            d1 -= 0.29999999999999999D;
            d1 -= ((EIT_EntityChicken)entitychicken).isChild() ? 0.15D : 0.29999999999999999D;
        }

        super.renderChicken(entitychicken, d, d1, d2, f, f1);
        
        // ��n��
        ((ModelChicken)mainModel).rightLeg.showModel = true;
        ((ModelChicken)mainModel).leftLeg.showModel = true;
    }
    
	
}
