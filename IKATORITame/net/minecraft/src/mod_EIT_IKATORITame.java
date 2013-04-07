package net.minecraft.src;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	@MLProp
	public static boolean isDebugMessage = true;


	public static void Debug(String pText, Object... pData) {
		// デバッグメッセージ
		if (isDebugMessage) {
			System.out.println(String.format("IKATORITame-" + pText, pData));
		}
	}

	@Override
	public String getVersion() {
		return "1.5.1-2";
	}

	@Override
	public String getName() {
		return "IKATORITame";
	}

	public void replaceEntityList(Class pSrcClass, Class pDestClass) {
		try {
			// stringToClassMapping
			Map lmap;
			int lint;
			String ls;
			lmap = (Map)ModLoader.getPrivateValue(EntityList.class, null, 0);
			for (Entry<String, Class> le : ((Map<String, Class>)lmap).entrySet()) {
				if (le.getValue() == pSrcClass) {
					le.setValue(pDestClass);
				}
			}
			// classToStringMapping
			lmap = (Map)ModLoader.getPrivateValue(EntityList.class, null, 1);
			if (lmap.containsKey(pSrcClass)) {
				ls = (String)lmap.get(pSrcClass);
				lmap.remove(pSrcClass);
				lmap.put(pDestClass, ls);
			}
			// IDtoClassMapping
			lmap = (Map)ModLoader.getPrivateValue(EntityList.class, null, 0);
			for (Entry<Integer, Class> le : ((Map<Integer, Class>)lmap).entrySet()) {
				if (le.getValue() == pSrcClass) {
					le.setValue(pDestClass);
				}
			}
			// classToIDMapping
			lmap = (Map)ModLoader.getPrivateValue(EntityList.class, null, 1);
			if (lmap.containsKey(pSrcClass)) {
				lint = (Integer)lmap.get(pSrcClass);
				lmap.remove(pSrcClass);
				lmap.put(pDestClass, lint);
			}
			Debug("Replace %s -> %s", pSrcClass.getSimpleName(), pDestClass.getSimpleName());
		} catch (Exception e) {
			
		}
	}
	
	public void replaceBaiomeSpawn(Class pSrcClass, Class pDestClass) {
		// バイオームの発生処理をのっとる
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			if (BiomeGenBase.biomeList[i] == null) continue;
			List<SpawnListEntry> mobs = BiomeGenBase.biomeList[i].spawnableWaterCreatureList;
			if (mobs == null) continue;
			for (int j = 0; j < mobs.size(); j++) {
				if (mobs.get(j).entityClass == pSrcClass) {
					mobs.get(j).entityClass = pDestClass;
				}
			}
		}
	}

	@Override
	public void load() {
		// 置換え用メソッドの獲得
		Method method2 = null;
		try {
			try {
				try {
					method2 = (EntityList.class).getDeclaredMethod("a", new Class[] {
							java.lang.Class.class, java.lang.String.class,
							Integer.TYPE, Integer.TYPE, Integer.TYPE
					});
				} catch(NoSuchMethodException nosuchmethodexception2) {
					method2 = (EntityList.class).getDeclaredMethod("addMapping", new Class[] {
							java.lang.Class.class, java.lang.String.class,
							Integer.TYPE, Integer.TYPE, Integer.TYPE
					});
				}
			} catch(NoSuchMethodException nosuchmethodexception2) {
				method2 = (EntityList.class).getDeclaredMethods()[1];
			}
			method2.setAccessible(true);
		} catch(Exception e) {
			Debug("can't find Method.");
			return;
		}
		
		if (isReplaceSquid) {
			// EntitySquidのメソッドをチェックしてSquidの置き換えが必要かどうか判定
			Method method1 = null;
			try {
				// Jarにいれてるのかい？
				method1 = EntitySquid.class.getDeclaredMethod("isTamed", new Class[] {});
				Debug("is IKA Tame.");
			} catch (Exception e) {
			}
			
			if (method1 == null) {
				try {
					// イカの置き換え
//					method2.invoke(null, new Object[] {EIT_EntitySquid.class, "Squid", 94, 2243405, 7375001});
					replaceEntityList(EntitySquid.class, EIT_EntitySquid.class);
					// バイオームの発生処理をのっとる
					for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
						if (BiomeGenBase.biomeList[i] == null) continue;
						List<SpawnListEntry> mobs = BiomeGenBase.biomeList[i].spawnableWaterCreatureList;
						if (mobs == null) continue;
						for (int j = 0; j < mobs.size(); j++) {
							if (mobs.get(j).entityClass == EntitySquid.class) {
								mobs.get(j).entityClass = EIT_EntitySquid.class;
							}
						}
					}
					Debug("Replace IKA.");
				}
				catch (Exception e) {
					Debug("Failed IKA.");
					e.printStackTrace();
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
				try {
					// トリの置き換え
//					method2.invoke(null, new Object[] {EIT_EntityChicken.class, "Chicken", 93, 10592673, 16711680});
					replaceEntityList(EntityChicken.class, EIT_EntityChicken.class);
					
					// バイオームの発生処理をのっとる
					for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
						if (BiomeGenBase.biomeList[i] == null) continue;
						List<SpawnListEntry> mobs = BiomeGenBase.biomeList[i].spawnableCreatureList;
						if (mobs == null) continue;
						for (int j = 0; j < mobs.size(); j++) {
							if (mobs.get(j).entityClass == EntityChicken.class) {
								mobs.get(j).entityClass = EIT_EntityChicken.class;
							}
						}
					}
					Debug("Replace Chicken.");
				}
				catch (Exception e) {
					Debug("Failed Chicken.");
					e.printStackTrace();
				}
				
				// 卵の置き換え
				Item.itemsList[256 + 88] = null;
				Item.egg = (new EIT_ItemEgg(88)).setUnlocalizedName("egg");
				ModLoader.addDispenserBehavior(Item.egg, new EIT_DispenserBehaviorEgg());
				ModLoader.registerEntityID(EIT_EntityEgg.class, "egg", MMM_Helper.getNextEntityID(false));
				ModLoader.addEntityTracker(this, EIT_EntityEgg.class, MMM_Helper.getNextEntityID(false), 64, 10, true);
			}
		}
	}

	@Override
	public void addRenderer(Map map) {
		EIT_Client.addRenderer(map);
	}

	// Forge
	@Override
	public Entity spawnEntity(int entityId, World world, double scaledX, double scaledY, double scaledZ) {
		// Modloader下では独自に生成するので要らない。
		// というかModLoader環境ではIDが3000以上になるのでここは呼ばれない。
		if (!MMM_Helper.isForge) return null;
		EIT_EntityEgg lentity = new EIT_EntityEgg(world, scaledX, scaledY, scaledZ);
		lentity.entityId = entityId;
//		lentity.setPosition(scaledX, scaledY, scaledZ);
		return lentity;
	}

	//Modloader
	@Override
	public Packet23VehicleSpawn getSpawnPacket(Entity var1, int var2) {
		// Forge環境下では呼ばれない
		EntityLiving lentity = ((EIT_EntityEgg)var1).getThrower();
		return new EIT_PacketEggSpawn(var1, lentity == null ? 0 : lentity.entityId);
	}

}
