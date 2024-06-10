package liketechnik.tinkertweaks.mobhead.proxy;

import liketechnik.tinkertweaks.mobhead.tilenetities.IguanaSkullTileEntity;
import liketechnik.tinkertweaks.mobhead.renderers.IguanaTileEntitySkullRenderer;
import liketechnik.tinkertweaks.mobhead.handlers.RenderPlayerHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class MobHeadClientProxy extends MobHeadCommonProxy {
    @Override
    public void initialize() {
        ClientRegistry.bindTileEntitySpecialRenderer(IguanaSkullTileEntity.class, IguanaTileEntitySkullRenderer.renderer);
    }

    public void postInit() {
        // register the renderer
        MinecraftForge.EVENT_BUS.register(new RenderPlayerHandler());

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