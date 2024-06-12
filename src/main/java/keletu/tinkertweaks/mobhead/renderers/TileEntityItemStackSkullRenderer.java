package keletu.tinkertweaks.mobhead.renderers;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileEntityItemStackSkullRenderer extends TileEntityItemStackRenderer {

	public void renderByItem(ItemStack stack, float p_192838_2_) {
		IguanaTileEntitySkullRenderer.renderer.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 180.0F, stack.getMetadata(), -1, 0.0F);
	}
}