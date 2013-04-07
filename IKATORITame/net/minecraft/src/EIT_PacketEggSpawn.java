package net.minecraft.src;

public class EIT_PacketEggSpawn extends Packet23VehicleSpawn {

	public EIT_PacketEggSpawn(Entity par1Entity, int pThrowerID) {
//		super(par1Entity, 0, pThrowerID);
		super(par1Entity, 0, 0);
	}

	@Override
	public void processPacket(NetHandler par1NetHandler) {
		WorldClient lwc = MMM_Helper.mc.theWorld;
		double var2 = (double)xPosition / 32.0D;
		double var4 = (double)yPosition / 32.0D;
		double var6 = (double)zPosition / 32.0D;
		EIT_EntityEgg lentity = new EIT_EntityEgg(lwc, var2, var4, var6);
		lentity.serverPosX = xPosition;
		lentity.serverPosY = yPosition;
		lentity.serverPosZ = zPosition;
		lentity.rotationPitch = (float)(pitch * 360) / 256.0F;
		lentity.rotationYaw = (float)(yaw * 360) / 256.0F;
		lentity.entityId = entityId;
		lwc.addEntityToWorld(entityId, lentity);
		if (throwerEntityId > 0) {
			lentity.setVelocity((double)speedX / 8000.0D, (double)speedY / 8000.0D, (double)speedZ / 8000.0D);
		}
	}

}
