package net.minecraft.src;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import net.minecraft.server.MinecraftServer;

public class mod_EIT_IKATORITame extends BaseMod {

	@MLProp(info="Enable Squid Tame")
	public static boolean isReplaceSquid = true;
	@MLProp(info="Enable Chicken Tame")
	public static boolean isReplaceChicken = true;
	@MLProp(info="Enable Chiicken Gride")
	public static boolean isChickenGride = true;
	@MLProp(info="Enable Chick Texture")
	public static boolean isChickTexture = true;
	@MLProp(info="Enable plucked")
	public static boolean isPlucked = true;



	@Override
	public String getVersion() {
		return "1.5.1-1";
	}

	@Override
	public String getName() {
		return "IKATORITame";
	}

	@Override
	public void load() {
		if (isReplaceSquid) {
			// EntitySquidのメソッドをチェックしてSquidの置き換えが必要かどうか判定
			Method method1 = null;
			try {
				// Jarにいれてるのかい？
				method1 = EntitySquid.class.getDeclaredMethod("isTamed", new Class[] {});
				System.out.println("is IKA Tame.");
			}
			catch (Exception e) {
			}
			
			if (method1 == null) {
				Method method2;
				try {
					// イカの置き換え
					try {
						method2 = (net.minecraft.src.EntityList.class).getDeclaredMethod("a", new Class[] {
								java.lang.Class.class, java.lang.String.class, Integer.TYPE
						});
					}
					catch(NoSuchMethodException nosuchmethodexception2) {
						method2 = (net.minecraft.src.EntityList.class).getDeclaredMethod("addMapping", new Class[] {
								java.lang.Class.class, java.lang.String.class, Integer.TYPE
						});
					}
					method2.setAccessible(true);
					method2.invoke(null, new Object[] {EIT_EntitySquid.class, "Squid", 94});
					
					// バイオームの発生処理をのっとる
					for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
						List<SpawnListEntry> mobs = BiomeGenBase.biomeList[i].spawnableWaterCreatureList;
						for (int j = 0; j < mobs.size(); j++) {
							if (mobs.get(j).entityClass == EntitySquid.class) {
								mobs.get(j).entityClass = EIT_EntitySquid.class;
							}
						}
					}
				}
				catch (Exception e) {
				}
			}
		}

		if (isReplaceChicken) {
			// EntityCheckenのメソッドをチェックしてCheckenの置き換えが必要かどうか判定
			Method method1 = null;
			try {
				// Jarにいれてうのかい？
				method1 = EntityChicken.class.getDeclaredMethod("isTamed", new Class[] {});
				System.out.println("is TORI Tame.");
			}
			catch (Exception e) {
			}
			
			if (method1 == null) {
				Method method2;
				try {
					// トリの置き換え
					try {
						method2 = (net.minecraft.src.EntityList.class).getDeclaredMethod("a", new Class[] {
								java.lang.Class.class, java.lang.String.class, Integer.TYPE
						});
					}
					catch(NoSuchMethodException nosuchmethodexception2) {
						method2 = (net.minecraft.src.EntityList.class).getDeclaredMethod("addMapping", new Class[] {
								java.lang.Class.class, java.lang.String.class, Integer.TYPE
						});
					}
					method2.setAccessible(true);
					method2.invoke(null, new Object[] {EIT_EntityChicken.class, "Chicken", 93});
					
					// バイオームの発生処理をのっとる
					for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
						List<SpawnListEntry> mobs = BiomeGenBase.biomeList[i].spawnableCreatureList;
						for (int j = 0; j < mobs.size(); j++) {
							if (mobs.get(j).entityClass == EntityChicken.class) {
								mobs.get(j).entityClass = EIT_EntityChicken.class;
							}
						}
					}
				}
				catch (Exception e) {
				}
				
				// 卵の置き換え
				Item.itemsList[256 + 88] = null;
				Item.egg = (new EIT_ItemEgg(88)).setUnlocalizedName("egg");
				ModLoader.addDispenserBehavior(Item.egg, new EIT_DispenserBehaviorEgg());
			}
		}
	}

	@Override
	public void addRenderer(Map map) {
		if (isReplaceChicken) {
			// 鶏のレンダーを追加、元のは別に消してない
			map.put(EIT_EntityChicken.class, new EIT_RenderChickenTame(new ModelChicken(), 0.3F));
		}
	}

}
