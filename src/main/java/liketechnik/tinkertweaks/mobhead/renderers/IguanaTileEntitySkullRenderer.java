package liketechnik.tinkertweaks.mobhead.renderers;

import liketechnik.tinkertweaks.LiketechniksTinkerTweaks;
import liketechnik.tinkertweaks.mobhead.models.ModelBucketHelmet;
import liketechnik.tinkertweaks.mobhead.models.ModelEnderManHead;
import liketechnik.tinkertweaks.mobhead.models.ModelHeadwear;
import liketechnik.tinkertweaks.mobhead.tilenetities.IguanaSkullTileEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class IguanaTileEntitySkullRenderer extends TileEntitySkullRenderer {
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
    public void render(TileEntitySkull te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(!(te instanceof IguanaSkullTileEntity))
            return;

        EnumFacing enumfacing = EnumFacing.byIndex(te.getBlockMetadata() & 7);
        //float r = (te.getSkullRotation() * 360) / 16.0F;
        //renderSkull((float)x, (float)y, (float)z, r, te.getBlockMetadata(), te.getSkullType());
        float f = te.getAnimationProgress(partialTicks);
        this.renderSkull((float)x, (float)y, (float)z, enumfacing, (float)(te.getSkullRotation() * 360) / 16.0F, te.getSkullType(), destroyStage, f);
    }

    //public void renderSkull(float x, float y, float z, float r, int sidePlacement, int meta)
    //{
    //    if(meta == 0)
    //        renderSkull(x,y,z, r, sidePlacement, modelEnderManHead, textures[0]);
    //    else if(meta == 1)
    //        renderSkull(x,y,z, r, sidePlacement, modelZombie, textures[1]);
    //    else {
    //        // draw blaze if head doesn't exist (anymore)
    //        if(!IguanaSkull.isHeadRegistered(meta))
    //            meta = 2;
    //        renderSkull(x, y, z, r, sidePlacement, modelSkull, textures[meta]);
    //    }
    //}
//
    //public void renderBucket(float x, float y, float z, float r, int sidePlacement, int meta)
    //{
    //    if(meta == 0)
    //        renderSkull(x,y,z, r, sidePlacement, modelBucketHelmet, textureBucketHelmet);
    //    else if(meta == 1)
    //        renderSkull(x,y,z, r, sidePlacement, modelBucketHelmet, textureClayBucketHelmet);
    //    else if(meta == 2)
    //        renderSkull(x,y,z, r, sidePlacement, modelEnderManJaw, textures[0]);
    //    else if(meta == 3)
    //        renderSkull(x,y,z, r, sidePlacement, modelSkull, textures[0]);
    //}

    public void renderSkull(float x, float y, float z, EnumFacing facing, float rotationIn, int skullType, int destroyStage, float animateTicks)
    {
        ModelBase modelbase = this.modelSkull;

        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {
            switch (skullType)
            {
                case 0:
                default:
                    this.bindTexture(textures[0]);
                    modelbase = modelEnderManHead;
                    break;
                case 1:
                    this.bindTexture(textures[1]);
                    modelbase = modelZombie;
                    break;
                case 2:
                    this.bindTexture(textures[2]);
                    break;
                case 3:
                    this.bindTexture(textures[3]);
                    break;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        if (facing == EnumFacing.UP)
        {
            GlStateManager.translate(x + 0.5F, y, z + 0.5F);
        }
        else
        {
            switch (facing)
            {
                case NORTH:
                    GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.74F);
                    break;
                case SOUTH:
                    GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.26F);
                    rotationIn = 180.0F;
                    break;
                case WEST:
                    GlStateManager.translate(x + 0.74F, y + 0.25F, z + 0.5F);
                    rotationIn = 270.0F;
                    break;
                case EAST:
                default:
                    GlStateManager.translate(x + 0.26F, y + 0.25F, z + 0.5F);
                    rotationIn = 90.0F;
            }
        }

        float f = 0.0625F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();

        if (skullType == 3)
        {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }

        modelbase.render(null, animateTicks, 0.0F, 0.0F, rotationIn, 0.0F, 0.0625F);
        if(modelbase == modelEnderManHead)
        {
            this.bindTexture(enderManEyes);
            modelbase.render(null, 0.0f, 0.0f, 0.0f, rotationIn, 0.0f, 0.0625F);
        }
        GlStateManager.popMatrix();

        if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    /*public void renderSkull(float x, float y, float z, EnumFacing sidePlacement, float r, int destroyStage, ModelBase model, ResourceLocation texture)
    {
        // chose texture
        this.bindTexture(texture);

        // debug
        //model = new ModelSkeletonHead(0,16,64,32);
        //this.bindTexture(new ResourceLocation("textures/entity/enderman/enderman_eyes.png"));

        // begin rendering
        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
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

        GlStateManager.popMatrix();
    }*/
}