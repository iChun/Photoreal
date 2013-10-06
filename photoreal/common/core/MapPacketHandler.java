package photoreal.common.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet131MapData;
import photoreal.common.Photoreal;
import photoreal.common.entity.EntityPhotoreal;
import photoreal.common.item.ItemCamera;
import cpw.mods.fml.common.network.ITinyPacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MapPacketHandler 
	implements ITinyPacketHandler 
{

	@Override
	public void handle(NetHandler handler, Packet131MapData mapData) 
	{
		int id = mapData.uniqueID;
		if(handler instanceof NetServerHandler)
		{
			handleServerPacket((NetServerHandler)handler, mapData.uniqueID, mapData.itemData, (EntityPlayerMP)handler.getPlayer());
		}
		else
		{
			handleClientPacket((NetClientHandler)handler, mapData.uniqueID, mapData.itemData);
		}
	}


	public void handleServerPacket(NetServerHandler handler, short id, byte[] data, EntityPlayerMP player)
	{
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data));
		try
		{
			switch(id)
			{
				case 0:
				{
					double posX = stream.readDouble();
					double posY = stream.readDouble();
					double posZ = stream.readDouble();
					
					float rotYaw = stream.readFloat();
					float rotPitch = stream.readFloat();
					
					int width = stream.readInt();
					int height = stream.readInt();
					
					ItemStack is = player.getCurrentEquippedItem();
					if(is != null && is.getItem() instanceof ItemCamera && is.getTagCompound() != null)
					{
				    	is.getTagCompound().setInteger("recharge", Photoreal.config.getInt("cameraRecharge"));

						EntityPhotoreal photo = new EntityPhotoreal(player.worldObj);
						photo.setLocationAndAngles(posX, posY, posZ, rotYaw, rotPitch);
						photo.setup(posX, posY, posZ, rotYaw, rotPitch, width, height);
						
						player.worldObj.spawnEntityInWorld(photo);
					}
					break;
				}
			}
		}
		catch(IOException e)
		{
		}

	}
	
	//TODO Side Split
	
	@SideOnly(Side.CLIENT)
	public void handleClientPacket(NetClientHandler handler, short id, byte[] data)
	{
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data));
		try
		{
			switch(id)
			{
				case 0:
				{
					stream.readUTF();
				}
			}
		}
		catch(IOException e)
		{
		}
	}
}