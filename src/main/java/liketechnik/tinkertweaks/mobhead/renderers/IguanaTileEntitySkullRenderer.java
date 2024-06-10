package liketechnik.tinkertweaks.mobhead.renderers;

import liketechnik.tinkertweaks.LiketechniksTinkerTweaks;
import liketechnik.tinkertweaks.mobhead.items.IguanaSkull;
import liketechnik.tinkertweaks.mobhead.models.ModelBucketHelmet;
import liketechnik.tinkertweaks.mobhead.models.ModelEnderManHead;
import liketechnik.tinkertweaks.mobhead.models.ModelHeadwear;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


@SideOnly(Side.CLIENT)
public class IguanaTileEntitySkullRenderer extends TileEntitySpecialRenderer {
    public static IguanaTileEntitySkullRenderer renderer = new IguanaTileEntitySkullRenderer();

    // Skull stuff
    private final ModelSkeletonHead modelSkull = new ModelSkeletonHead(0,0,64,32); // standard skull model
    private final ModelSkeletonHead modelZombie = new ModelSkeletonHead(0,0,64,64); // zombie skull model
    private final ModelEnderManHead modelEnderManHead = new ModelEnderManHead();

    private final ResourceLocation[] textures = new ResourceLocation[] {
            new ResourceLocation("textures/entity/enderman/enderman.png"),
            new ResourceLocation("textures/entity/zombie_pigman.png"),
            new ResourceLocation("textures/entity/blaze.png"),
            // modsupport: Thermal Expansion
            new ResourceLocation("thermalfoundation","textures/entity/Blizz.png")
    };

    private final ResourceLocation enderManEyes = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");


    // fun stuff ;)
    private final ModelHeadwear modelEnderManJaw = new ModelHeadwear(0,16,64,32);
    private final ModelBucketHelmet modelBucketHelmet = new ModelBucketHelmet();
    private final ResourceLocation textureBucketHelmet = new ResourceLocation(LiketechniksTinkerTweaks.MODID, "textures/models/bucket_helmet.png");
    private final ResourceLocation textureClayBucketHelmet = new ResourceLocation(LiketechniksTinkerTweaks.MODID, "textures/models/claybucket_helmet.png");

    public IguanaTileEntitySkullRenderer()
    {
        this.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }



    @Override
    public void render(TileEntity entity, double x, double y, double z, float f, int f1, float f2) {
        if(!(entity instanceof TileEntitySkull))
            return;
        TileEntitySkull entitySkull = (TileEntitySkull)entity;

        float r = (entitySkull.getSkullRotation() * 360) / 16.0F;
        renderSkull((float)x, (float)y, (float)z, r, entitySkull.getBlockMetadata(), entitySkull.getSkullType());
    }

    public void renderSkull(float x, float y, float z, float r, int sidePlacement, int meta)
    {
        if(meta == 0)
            renderSkull(x,y,z, r, sidePlacement, modelEnderManHead, textures[0]);
        else if(meta == 1)
            renderSkull(x,y,z, r, sidePlacement, modelZombie, textures[1]);
        else {
            // draw blaze if head doesn't exist (anymore)
            if(!IguanaSkull.isHeadRegistered(meta))
                meta = 2;
            renderSkull(x, y, z, r, sidePlacement, modelSkull, textures[meta]);
        }
    }

    public void renderBucket(float x, float y, float z, float r, int sidePlacement, int meta)
    {
        if(meta == 0)
            renderSkull(x,y,z, r, sidePlacement, modelBucketHelmet, textureBucketHelmet);
        else if(meta == 1)
            renderSkull(x,y,z, r, sidePlacement, modelBucketHelmet, textureClayBucketHelmet);
        else if(meta == 2)
            renderSkull(x,y,z, r, sidePlacement, modelEnderManJaw, textures[0]);
        else if(meta == 3)
            renderSkull(x,y,z, r, sidePlacement, modelSkull, textures[0]);
    }

    public void renderSkull(float x, float y, float z, float r, int sidePlacement, ModelBase model, ResourceLocation texture)
    {
        // chose texture
        this.bindTexture(texture);

        // debug
        //model = new ModelSkeletonHead(0,16,64,32);
        //this.bindTexture(new ResourceLocation("textures/entity/enderman/enderman_eyes.png"));

        // begin rendering
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);


        if (sidePlacement != 1)
        {
            switch (sidePlacement)
            {
                case 2:
                    GL11.glTranslatef(x + 0.5F, y + 0.25F, z + 0.74F);
                    break;
                case 3:
                    GL11.glTranslatef(x + 0.5F, y + 0.25F, z + 0.26F);
                    r = 180.0F;
                    break;
                case 4:
                    GL11.glTranslatef(x + 0.74F, y + 0.25F, z + 0.5F);
                    r = 270.0F;
                    break;
                case 5:
                default:
                    GL11.glTranslatef(x + 0.26F, y + 0.25F, z + 0.5F);
                    r = 90.0F;
            }
        }
        else
        {
            GL11.glTranslatef(x + 0.5F, y, z + 0.5F);
        }


        float f4 = 0.0625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        model.render(null, 0.0f, 0.0f, 0.0f, r, 0.0f, f4);

        // also render enderman eyes!
        if(model == modelEnderManHead)
        {
            this.bindTexture(enderManEyes);
            model.render(null, 0.0f, 0.0f, 0.0f, r, 0.0f, f4);
        }

        GL11.glPopMatrix();
    }
}