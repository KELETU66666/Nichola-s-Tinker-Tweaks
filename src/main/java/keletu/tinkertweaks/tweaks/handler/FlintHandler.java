package keletu.tinkertweaks.tweaks.handler;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ListIterator;

public class FlintHandler {

    @SubscribeEvent
    public void onBlockHarvested(BlockEvent.HarvestDropsEvent event)
    {
        // remove flint drop
        if(event.getState().getBlock() != null && event.getState().getBlock() == Blocks.GRAVEL)
        {
            ListIterator<ItemStack> iter = event.getDrops().listIterator();
            boolean hasGravel = false;
            while(iter.hasNext())
            {
                Item item = iter.next().getItem();
                if(item == Items.FLINT)
                    iter.remove();
                else if(item == Item.getItemFromBlock(Blocks.GRAVEL))
                    hasGravel = true;
            }

            // ensure that gravel drops
            if(!hasGravel)
                event.getDrops().add(new ItemStack(Blocks.GRAVEL));
        }
    }
}