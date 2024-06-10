package liketechnik.tinkertweaks.claybuckets.proxy;

import javax.annotation.Nonnull;

import liketechnik.tinkertweaks.claybuckets.IguanaItems;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ItemsClientProxy extends ItemsCommonProxy {
	@Override
	public void registerModels() {
		registerItemModel(IguanaItems.clayBucketFired);
		ModelLoader.setCustomModelResourceLocation(IguanaItems.clayBucketFired, 1,
				new ModelResourceLocation(IguanaItems.clayBucketFired.getRegistryName(), "milk"));
		registerItemModel(IguanaItems.clayBucketUnfired);
	}

	private void registerItemModel(Item item) {
		if(item != null) {
			final ResourceLocation location = item.getRegistryName();
			ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
				@Nonnull
				@Override
				public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
					return new ModelResourceLocation(location, "inventory");
				}
			});
			ModelLoader.registerItemVariants(item, location);
		}
	}
}