package keletu.tinkertweaks.mobhead.proxy;

import keletu.tinkertweaks.mobhead.IguanaMobHeads;
import keletu.tinkertweaks.mobhead.renderers.IguanaTileEntitySkullRenderer;
import keletu.tinkertweaks.mobhead.renderers.TileEntityItemStackSkullRenderer;
import keletu.tinkertweaks.mobhead.tilenetities.IguanaSkullTileEntity;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

public class MobHeadClientProxy extends MobHeadCommonProxy {
    @Override
    public void initialize() {
        ClientRegistry.bindTileEntitySpecialRenderer(IguanaSkullTileEntity.class, new IguanaTileEntitySkullRenderer());
        IguanaMobHeads.skullItem.setTileEntityItemStackRenderer(new TileEntityItemStackSkullRenderer());
    }

    public void postInit() {
        // register the renderer

        //if(Loader.isModLoaded("NotEnoughItems")) {
        //    codechicken.nei.api.API.hideItem(new ItemStack(IguanaMobHeads.wearables, 1, 0));
        //    codechicken.nei.api.API.hideItem(new ItemStack(IguanaMobHeads.wearables, 1, 1));
        //    codechicken.nei.api.API.hideItem(new ItemStack(IguanaMobHeads.wearables, 1, 2));
        //    codechicken.nei.api.API.hideItem(new ItemStack(IguanaMobHeads.wearables, 1, 3));
//
        //    codechicken.nei.api.API.hideItem(new ItemStack(Item.getItemFromBlock(IguanaMobHeads.skullBlock)));
        //}
    }
}