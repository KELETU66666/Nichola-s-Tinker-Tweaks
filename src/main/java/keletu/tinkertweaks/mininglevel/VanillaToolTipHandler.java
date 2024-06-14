package keletu.tinkertweaks.mininglevel;

import keletu.tinkertweaks.config.Config;
import keletu.tinkertweaks.tweaks.handler.VanillaHoeNerfHandler;
import keletu.tinkertweaks.tweaks.handler.VanillaToolNerfHandler;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VanillaToolTipHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemToolTip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        if(!(event.getItemStack().getItem() instanceof ItemTool))
            return;

        if((Config.nerfVanillaTools() && VanillaToolNerfHandler.isUselessTool(event.getItemStack().getItem()))
         || (Config.nerfVanillaHoes() && VanillaHoeNerfHandler.isUselessHoe(event.getItemStack().getItem())))
            return;

        // we're only interested in stuff that's basically a pickaxe
        int hlvl = event.getItemStack().getItem().getHarvestLevel(event.getItemStack(), "pickaxe", null, null);
        if (hlvl >= 0)
            event.getToolTip().add(1, "LevelingTooltips.getMiningLevelTooltip(hlvl)");

        // well.. let's check the other things too /o\
        /* disabled because it'll probably cause more confusion than help..
        hlvl = event.itemStack.getItem().getHarvestLevel(event.itemStack, "shovel");
        if (hlvl >= 0)
            event.toolTip.add(1, LevelingTooltips.getMiningLevelTooltip(hlvl));

        hlvl = event.itemStack.getItem().getHarvestLevel(event.itemStack, "axe");
        if (hlvl >= 0)
            event.toolTip.add(1, LevelingTooltips.getMiningLevelTooltip(hlvl));
        */
    }
}