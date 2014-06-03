package photoreal.common.packet;

import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import photoreal.common.Photoreal;
import photoreal.common.entity.EntityPhotoreal;
import photoreal.common.item.ItemCamera;

public class PacketTakeSnapshot extends AbstractPacket
{
    public double posX;
    public double posY;
    public double posZ;

    public float yaw;
    public float pitch;

    public int width;
    public int height;

    public PacketTakeSnapshot(){}

    public PacketTakeSnapshot(double x, double y, double z, float ya, float pi, int w, int h)
    {
        posX = x;
        posY = y;
        posZ = z;
        yaw = ya;
        pitch = pi;
        width = w;
        height = h;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeDouble(posX);
        buffer.writeDouble(posY);
        buffer.writeDouble(posZ);

        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);

        buffer.writeInt(width);
        buffer.writeInt(height);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        posX = buffer.readDouble();
        posY = buffer.readDouble();
        posZ = buffer.readDouble();

        yaw = buffer.readFloat();
        pitch = buffer.readFloat();

        width = buffer.readInt();
        height = buffer.readInt();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        ItemStack is = player.getCurrentEquippedItem();
        if(is != null && is.getItem() instanceof ItemCamera && is.getTagCompound() != null)
        {
            is.getTagCompound().setInteger("recharge", Photoreal.config.getInt("cameraRecharge"));

            EntityPhotoreal photo = new EntityPhotoreal(player.worldObj);
            photo.setLocationAndAngles(posX, posY, posZ, yaw, pitch);
            photo.setup(posX, posY, posZ, yaw, pitch, width, height);

            player.worldObj.spawnEntityInWorld(photo);
        }
    }
}
