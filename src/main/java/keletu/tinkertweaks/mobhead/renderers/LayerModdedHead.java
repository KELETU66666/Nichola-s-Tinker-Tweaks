package keletu.tinkertweaks.mobhead.renderers;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerModdedHead implements LayerRenderer<EntityLivingBase>
{
    private final ModelRenderer modelRenderer;

    public LayerModdedHead(ModelRenderer p_i46120_1_)
    {
        this.modelRenderer = p_i46120_1_;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {

    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}