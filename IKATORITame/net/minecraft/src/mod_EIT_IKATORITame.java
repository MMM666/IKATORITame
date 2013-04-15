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
	public String getName() {
		return "IKATORITame";
	}

	@Override
	public String getVersion() {
		return "1.5.1-3";
	}

	@Override
	public void load() {
		// MMMLibのRevisionチェック
		MMM_Helper.checkRevision("1");
		
		if (isReplaceSquid) {
			// イカの置き換え
			replaceEntityList(EntitySquid.class, EIT_EntitySquid.class);
			Debug("Replace IKA.");
		}
		
		if (isReplaceChicken) {
			// トリの置き換え
			replaceEntityList(EntityChicken.class, EIT_EntityChicken.class);
			Debug("Replace Chicken.");
			
			// 卵の置き換え
			Item.itemsList[256 + 88] = null;
			Item.egg = (new EIT_ItemEgg(88)).setUnlocalizedName("egg");
			ModLoader.addDispenserBehavior(Item.egg, new EIT_DispenserBehaviorEgg());
			ModLoader.registerEntityID(EIT_EntityEgg.class, "egg", MMM_Helper.getNextEntityID(false));
			ModLoader.addEntityTracker(this, EIT_EntityEgg.class, MMM_Helper.getNextEntityID(false), 64, 10, true);
			Debug("Replace Egg.");
		}
	}

	@Override
	public void modsLoaded() {
		// バイオームに設定されたスポーン情報を置き換え。
		if (isReplaceSquid) {
			replaceBaiomeSpawn(EntitySquid.class, EIT_EntitySquid.class);
		}
		if (isReplaceChicken) {
			replaceBaiomeSpawn(EntityChicken.class, EIT_EntityChicken.class);
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

	public void replaceEntityList(Class pSrcClass, Class pDestClass) {
		// EntityList登録情報を置き換え
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
			lmap = (Map)ModLoader.getPrivateValue(EntityList.class, null, 2);
			for (Entry<Integer, Class> le : ((Map<Integer, Class>)lmap).entrySet()) {
				if (le.getValue() == pSrcClass) {
					le.setValue(pDestClass);
				}
			}
			// classToIDMapping
			lmap = (Map)ModLoader.getPrivateValue(EntityList.class, null, 3);
			if (lmap.containsKey(pSrcClass)) {
				lint = (Integer)lmap.get(pSrcClass);
				lmap.remove(pSrcClass);
				lmap.put(pDestClass, lint);
			}
			Debug("Replace %s -> %s", pSrcClass.getSimpleName(), pDestClass.getSimpleName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void replaceCreatureList(Class pSrcClass, Class pDestClass, List<SpawnListEntry> pMobs) {
		if (pMobs == null) return;
		for (int j = 0; j < pMobs.size(); j++) {
			if (pMobs.get(j).entityClass == pSrcClass) {
				pMobs.get(j).entityClass = pDestClass;
				Debug("ReplaceCreatureList: %s -> %s", pSrcClass.getSimpleName(), pDestClass.getSimpleName());
			}
		}
	}

	public void replaceBaiomeSpawn(Class pSrcClass, Class pDestClass) {
		// バイオームの発生処理をのっとる
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			if (BiomeGenBase.biomeList[i] == null) continue;
			List<SpawnListEntry> mobs;
			Debug("ReplaceBaiomeSpawn:%s", BiomeGenBase.biomeList[i].biomeName);
			Debug("Creature.");
			replaceCreatureList(pSrcClass, pDestClass, BiomeGenBase.biomeList[i].spawnableCreatureList);
			Debug("WaterCreature.");
			replaceCreatureList(pSrcClass, pDestClass, BiomeGenBase.biomeList[i].spawnableWaterCreatureList);
			Debug("CaveCreature.");
			replaceCreatureList(pSrcClass, pDestClass, BiomeGenBase.biomeList[i].spawnableCaveCreatureList);
			Debug("Monster.");
			replaceCreatureList(pSrcClass, pDestClass, BiomeGenBase.biomeList[i].spawnableMonsterList);
		}
	}

}
