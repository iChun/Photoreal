package photoreal.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.client.render.RendererHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import photoreal.common.Photoreal;

public class EntityPhotoreal extends Entity
{

    public int age;
    public int loadTimeout;
    public int flashTimeout;
    public double fogR;
    public double fogG;
    public double fogB;

    public EntityPhotoreal(World par1World)
    {
        super(par1World);
        age = 0;
        loadTimeout = 0;
        setSize(0.05F, 0.05F);
        ignoreFrustumCheck = true;
        renderDistanceWeight = 20D;
        fogR = fogG = fogB = 1.0D;
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(16, 0.0F); //posX
        dataWatcher.addObject(17, 0.0F); //posY
        dataWatcher.addObject(18, 0.0F); //posZ

        dataWatcher.addObject(19, 0.0F); //rotYaw
        dataWatcher.addObject(20, 0.0F); //rotPitch

        dataWatcher.addObject(21, 1); //width
        dataWatcher.addObject(22, 1); //height

        dataWatcher.addObject(23, 0); //load timeout

        if(worldObj.isRemote)
        {
            createTextureRender();
        }
    }

    public void setup(double posX, double posY, double posZ, float rotYaw, float rotPitch, int width, int height)
    {
        dataWatcher.updateObject(16, (float)posX);
        dataWatcher.updateObject(17, (float)posY);
        dataWatcher.updateObject(18, (float)posZ);

        dataWatcher.updateObject(19, rotYaw);
        dataWatcher.updateObject(20, rotPitch);

        dataWatcher.updateObject(21, width);
        dataWatcher.updateObject(22, height);
    }

    @Override
    public void onUpdate()
    {
        age++;
        if(loadTimeout < 0)
        {
            loadTimeout++;
            if(loadTimeout == 0)
            {
                dataWatcher.updateObject(23, 10);
            }
        }
        if(age > Photoreal.config.getInt("photorealDuration"))
        {
            setDead();
            return;
        }
        if(flashTimeout > 0)
        {
            flashTimeout--;
        }
        if(dataWatcher.getWatchableObjectFloat(17) != 0.0F)
        {
            setLocationAndAngles((double)dataWatcher.getWatchableObjectFloat(16), (double)dataWatcher.getWatchableObjectFloat(17), (double)dataWatcher.getWatchableObjectFloat(18), dataWatcher.getWatchableObjectFloat(19), dataWatcher.getWatchableObjectFloat(20));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1)
    {
        return 15728880;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
        age = tag.getInteger("age");
        setup(tag.getDouble("posX"), tag.getDouble("posY"), tag.getDouble("posZ"), tag.getFloat("rotYaw"), tag.getFloat("rotPitch"), tag.getInteger("width"), tag.getInteger("height"));
        loadTimeout = -40;
        dataWatcher.updateObject(23, -40);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
        tag.setInteger("age", age);
        tag.setDouble("posX", (double)dataWatcher.getWatchableObjectFloat(16));
        tag.setDouble("posY", (double)dataWatcher.getWatchableObjectFloat(17));
        tag.setDouble("posZ", (double)dataWatcher.getWatchableObjectFloat(18));

        tag.setFloat("rotYaw", dataWatcher.getWatchableObjectFloat(19));
        tag.setFloat("rotPitch", dataWatcher.getWatchableObjectFloat(20));

        tag.setInteger("width", dataWatcher.getWatchableObjectInt(21));
        tag.setInteger("height", dataWatcher.getWatchableObjectInt(21));
    }

    @SideOnly(Side.CLIENT)
    public Framebuffer tex;
    public boolean rendered;

    @SideOnly(Side.CLIENT)
    public void createTextureRender()
    {
        tex = RendererHelper.createFrameBuffer("Photoreal", true);
    }

    @Override
    public void setDead()
    {
        super.setDead();
        if(worldObj.isRemote)
        {
            RendererHelper.deleteFrameBuffer(tex);
        }
    }
}
