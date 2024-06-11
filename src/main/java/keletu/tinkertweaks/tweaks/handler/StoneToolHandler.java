package keletu.tinkertweaks.tweaks.handler;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ranged.item.*;

public class StoneToolHandler {
    // we can initialize this statically, because it wont be initialized until PostInit, where all materials are already registered
    private static Material stoneMaterial = TinkerRegistry.getMaterial("Stone");

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void onTooltip(ItemTooltipEvent event)
    {
        if(event.getEntityPlayer() == null)
            return;

        // we're only interested if it's a tool part
        if(!(event.getItemStack().getItem() instanceof IToolPart) || event.getItemStack().getItem() == TinkerTools.bowString || event.getItemStack().getItem() == TinkerTools.fletching || event.getItemStack().getItem() == TinkerTools.arrowShaft)
            return;

        ItemStack stack = event.getItemStack();
        IToolPart part = (IToolPart)stack.getItem();

        // stone parts disabled?
        if(TinkerRegistry.getMaterial(part.getMaterialID(stack)) == stoneMaterial)
        {
            event.getToolTip().add(1, "");
            event.getToolTip().add(2, TextFormatting.DARK_RED + I18n.format("tooltip.part.castonly1"));
            event.getToolTip().add(3, TextFormatting.DARK_RED + I18n.format("tooltip.part.castonly2"));
            // we abuse the fact that the result is not used by anything to signal our other handlers to not add another tooltip
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onToolCraft(TinkerEvent.OnItemBuilding event)
    {
        for(int i = 0; i < event.materials.size(); i++)
        {
            // ignore bowstring and fletchings
            if(event.tool instanceof ShortBow && i == 1)
                continue;
            if(event.tool instanceof LongBow && i == 1)
                continue;
            if(event.tool instanceof CrossBow && i == 2)
                continue;
            if(event.tool instanceof Arrow && i >= 1)
                continue;
            if(event.tool instanceof Bolt && i == 2)
                continue;

            // don't allow stone tools
            if(event.materials.get(i) == stoneMaterial)
                event.setResult(Event.Result.DENY);
        }
    }
}