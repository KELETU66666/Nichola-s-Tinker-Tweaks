package liketechnik.tinkertweaks.mobhead.models;

import liketechnik.tinkertweaks.LiketechniksTinkerTweaks;
import liketechnik.tinkertweaks.mobhead.IguanaMobHeads;
import liketechnik.tinkertweaks.mobhead.blocks.IguanaSkullBlock;
import liketechnik.tinkertweaks.mobhead.tilenetities.IguanaSkullTileEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = LiketechniksTinkerTweaks.MODID)
public final class ModelHandler {
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent evt) {

		((IguanaSkullBlock) IguanaMobHeads.skullBlock).registerModels();

		for(int i = 0; i < 4;i++) {
			ModelLoader.setCustomModelResourceLocation(IguanaMobHeads.skullItem, i, new ModelResourceLocation(IguanaMobHeads.skullItem.getRegistryName(), "inventory"));

			ForgeHooksClient.registerTESRItemStack(IguanaMobHeads.skullItem, i, IguanaSkullTileEntity.class);
		}
	}
}