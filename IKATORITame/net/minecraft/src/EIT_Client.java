package net.minecraft.src;

import java.util.Map;

public class EIT_Client {

	public static void addRenderer(Map map) {
		if (mod_EIT_IKATORITame.isReplaceChicken) {
			// 鶏のレンダーを追加、元のは別に消してない
			map.put(EIT_EntityChicken.class, new EIT_RenderChickenTame());
			map.put(EIT_EntityEgg.class, new RenderSnowball(Item.egg));
		}
	}

}
