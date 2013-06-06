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
	
	public static Class classEggEntity = null;



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
		return "1.5.2-3";
	}

	@Override
	public void load() {
		// MMMLibのRevisionチェック
		MMM_Helper.checkRevision("4");
		
		if (isReplaceSquid) {
			// イカの置き換え
			MMM_Helper.replaceEntityList(EntitySquid.class, EIT_EntitySquid.class);
			Debug("Replace IKA.");
		}
		
		if (isReplaceChicken) {
			// トリの置き換え
			MMM_Helper.replaceEntityList(EntityChicken.class, EIT_EntityChicken.class);
			Debug("Replace Chicken.");
			
			// 卵の置き換え
			Item.itemsList[256 + 88] = null;
			Item.egg = (new EIT_ItemEgg(88)).setUnlocalizedName("egg");
			ModLoader.addDispenserBehavior(Item.egg, new EIT_DispenserBehaviorEgg());
			classEggEntity = MMM_Helper.getForgeClass(this, "EIT_EntityEgg");
			MMM_Helper.registerEntity(classEggEntity, "egg", 0, this, 64, 10, true);
//			ModLoader.registerEntityID(EIT_EntityEgg.class, "egg", MMM_Helper.getNextEntityID(false));
//			ModLoader.addEntityTracker(this, EIT_EntityEgg.class, MMM_Helper.getNextEntityID(false), 64, 10, true);
			Debug("Replace Egg.");
		}
	}

	@Override
	public void addRenderer(Map map) {
		EIT_Client.addRenderer(map);
	}
/*
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
*/
	//Modloader
	@Override
	public Packet23VehicleSpawn getSpawnPacket(Entity var1, int var2) {
		// Forge環境下では呼ばれない
		EntityLiving lentity = ((EIT_EntityEgg)var1).getThrower();
		return new EIT_PacketEggSpawn(var1, lentity == null ? 0 : lentity.entityId);
	}

}
