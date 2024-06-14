package keletu.tinkertweaks.tweaks.handler;

import keletu.tinkertweaks.config.Config;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.smeltery.events.TinkerCastingEvent;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ranged.item.*;

public class StoneToolHandler {
    // we can initialize this statically, because it wont be initialized until PostInit, where all materials are already registered
    private static Material stoneMaterial = TinkerRegistry.getMaterial("stone");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;

        // we're only interested if it's a tool part
        if (!(event.getItemStack().getItem() instanceof IToolPart) || event.getItemStack().getItem() == TinkerTools.bowString || event.getItemStack().getItem() == TinkerTools.fletching || event.getItemStack().getItem() == TinkerTools.arrowShaft)
            return;

        ItemStack stack = event.getItemStack();
        IToolPart part = (IToolPart) stack.getItem();

        // stone parts disabled?
        if (TinkerRegistry.getMaterial(part.getMaterialID(stack)) == stoneMaterial) {
            event.getToolTip().add(1, "");
            event.getToolTip().add(2, TextFormatting.DARK_RED + I18n.format("tooltip.part.castonly1"));
            event.getToolTip().add(3, TextFormatting.DARK_RED + I18n.format("tooltip.part.castonly2"));
            // we abuse the fact that the result is not used by anything to signal our other handlers to not add another tooltip
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onToolCraft(TinkerCraftingEvent.ToolCraftingEvent event) {
        for (int i = 0; i < event.getToolParts().size(); i++) {
            // ignore bowstring and fletchings
            if (event.getItemStack().getItem() instanceof ShortBow && i == 1)
                continue;
            if (event.getItemStack().getItem() instanceof LongBow && i == 1)
                continue;
            if (event.getItemStack().getItem() instanceof CrossBow && i == 2)
                continue;
            if (event.getItemStack().getItem() instanceof Arrow && i >= 1)
                continue;
            if (event.getItemStack().getItem() instanceof Bolt && i == 2)
                continue;

            // don't allow stone tools
            if (TinkerUtil.getMaterialFromStack(event.getToolParts().get(i)) == stoneMaterial)
                event.setCanceled(I18n.format("message.tool.disabled"));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPartReplace(TinkerCraftingEvent.ToolPartReplaceEvent event) {
        for (int i = 0; i < event.getToolParts().size(); i++) {
            // ignore bowstring and fletchings
            if (event.getItemStack().getItem() instanceof ShortBow && i == 1)
                continue;
            if (event.getItemStack().getItem() instanceof LongBow && i == 1)
                continue;
            if (event.getItemStack().getItem() instanceof CrossBow && i == 2)
                continue;
            if (event.getItemStack().getItem() instanceof Arrow && i >= 1)
                continue;
            if (event.getItemStack().getItem() instanceof Bolt && i == 2)
                continue;

            // don't allow stone tools
            if (TinkerUtil.getMaterialFromStack(event.getToolParts().get(i)) == stoneMaterial)
                event.setCanceled(I18n.format("message.tool.disabled"));
        }
    }

    @SubscribeEvent
    public void onToolPartCrafting(TinkerCraftingEvent.ToolPartCraftingEvent event) {
        for (String entry : Config.loadAllowedParts()) {
            String[] entries = entry.split(":");
            if (TinkerUtil.getMaterialFromStack(event.getItemStack()) != null && TinkerUtil.getMaterialFromStack(event.getItemStack()).equals(TinkerRegistry.getMaterial(entries[0]))) {
                if(event.getItemStack().getItem().getRegistryName().getPath().equals(entries[1])) {
                    event.setCanceled(false);
                    break;
                }
                event.setCanceled(I18n.format("message.toolpart.disallow"));
            }
        }
    }

    @SubscribeEvent
    public void onToolPartCrafting(TinkerCastingEvent.OnCasting event) {
        for (String entry : Config.loadAllowedParts()) {
            String[] entries = entry.split(":");
            Fluid fluid = null;
            if(event.tile.tank.getFluid() != null) {
                fluid = event.tile.tank.getFluid().getFluid();
            }
            ItemStack result = event.recipe.getResult(event.tile.getStackInSlot(0), fluid);
            if (TinkerUtil.getMaterialFromStack(result) != null && TinkerUtil.getMaterialFromStack(result).equals(TinkerRegistry.getMaterial(entries[0]))) {
                if(result.getItem().getRegistryName().getPath().equals(entries[1])) {
                    event.setCanceled(false);
                    break;
                }
                event.setCanceled(true);
            }
        }
    }
}