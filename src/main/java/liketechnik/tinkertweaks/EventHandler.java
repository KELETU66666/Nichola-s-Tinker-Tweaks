package liketechnik.tinkertweaks;

import com.google.common.collect.Lists;
import liketechnik.tinkertweaks.config.Config;
import liketechnik.tinkertweaks.level.modifier.ModToolLeveling;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.tools.Hammer;
import slimeknights.tconstruct.tools.tools.Pickaxe;

import java.util.List;

public final class EventHandler {

    public static EventHandler INSTANCE = new EventHandler();

    @SubscribeEvent
    public void onToolBuild(TinkerToolEvent.OnItemBuilding event) {
        // we build a dummy tool tag to get the base modifier amount, unchanged by traits
        List<Material> materials = Lists.newArrayList();
        for (int i = 0; i < event.tool.getRequiredComponents().size(); i++) {
            materials.add(Material.UNKNOWN);
        }
        NBTTagCompound baseTag = event.tool.buildTag(materials);

        // set free modifiers
        NBTTagCompound toolTag = TagUtil.getToolTag(event.tag);
        //modifiers = toolTag.getInteger(Tags.FREE_MODIFIERS);
        int modifiers = Math.max(0, Config.getStartingModifiers());
        toolTag.setInteger(Tags.FREE_MODIFIERS, modifiers);
        TagUtil.setToolTag(event.tag, toolTag);

        if (TinkerUtil.getModifierTag(event.tag, LiketechniksTinkerTweaks.modToolLeveling.getModifierIdentifier()).isEmpty()) {
            LiketechniksTinkerTweaks.modToolLeveling.apply(event.tag);
        }

        if (!TinkerUtil.hasModifier(event.tag, LiketechniksTinkerTweaks.modToolLeveling.getModifierIdentifier())) {
            LiketechniksTinkerTweaks.modToolLeveling.apply(event.tag);
        }

        if(event.tool instanceof Pickaxe) {
            if (TinkerUtil.getModifierTag(event.tag, LiketechniksTinkerTweaks.modHarvestLeveling.getModifierIdentifier()).isEmpty()) {
                LiketechniksTinkerTweaks.modHarvestLeveling.apply(event.tag);
            }

            if (!TinkerUtil.hasModifier(event.tag, LiketechniksTinkerTweaks.modHarvestLeveling.getModifierIdentifier())) {
                LiketechniksTinkerTweaks.modHarvestLeveling.apply(event.tag);
            }
        }
    }

    @SubscribeEvent
    public void onRepair(TinkerToolEvent.OnRepair event) {
        // Have to cancel the event and make a new one since they won't let us modify the tool
        if (Config.addBonusStatsOnLevelup()) {
            //System.out.println("OnRepair triggered");
            ItemStack tool = event.itemStack;
            tool = ModToolLeveling.updateStats(tool);
            // Only cancel and refire the event if it is the original event.
            // If the modification to the tool is already done, then this is our new event
            // So don't cancel it, or you get an infinite loop...
            if (!tool.equals(event.itemStack)) {
                //System.out.println("Tool has not been modified in this event! Replace the OnRepair event.");
                // Fire the new event with proper tool stack
                //event.setCanceled("Replacing event");
                TinkerToolEvent.OnRepair.fireEvent(tool, event.amount);
            } else {
                //System.out.println("Tool has already been modified in this event, proceeding.");
            }
        }
    }

    @SubscribeEvent
    public void toolPartReplaceEvent(TinkerCraftingEvent.ToolPartReplaceEvent event) {
        // Have to cancel the event and make a new one since they won't let us modify the tool
        if (Config.addBonusStatsOnLevelup()) {
            //System.out.println("ToolPartReplaceEvent triggered");
            ItemStack tool = event.getItemStack();
            tool = ModToolLeveling.updateStats(tool);
            // Only cancel and refire the event if it is the original event.
            // If the modification to the tool is already done, then this is our new event
            // So don't cancel it, or you get an infinite loop...
            if (!tool.equals(event.getItemStack())) {
                //System.out.println("Tool has not been modified in this event! Replace the ToolPartReplaceEvent event.");
                // Fire the new event with proper tool stack
                event.setCanceled("Replacing event");
                try {
                    TinkerCraftingEvent.ToolPartReplaceEvent.fireEvent(tool, event.getPlayer(), event.getToolParts());
                } catch (TinkerGuiException e) {
                    System.out.println("TinkerGUIException! " + e);
                }
            } else {
                //System.out.println("Tool has already been modified in this event, proceeding.");
            }
        }
    }

    @SubscribeEvent
    public void toolModifyEvent(TinkerCraftingEvent.ToolModifyEvent event) {
        // Have to cancel the event and make a new one since they won't let us modify the tool
        if (Config.addBonusStatsOnLevelup()) {
            //System.out.println("ToolModifyEvent triggered");
            ItemStack tool = event.getItemStack();
            tool = ModToolLeveling.updateStats(tool);
            // Only cancel and refire the event if it is the original event.
            // If the modification to the tool is already done, then this is our new event
            // So don't cancel it, or you get an infinite loop...
            if (!tool.equals(event.getItemStack())) {
                //System.out.println("Tool has not been modified in this event! Replace the ToolModifyEvent event.");
                // Fire the new event with proper tool stack
                event.setCanceled("Replacing event");
                try {
                    TinkerCraftingEvent.ToolModifyEvent.fireEvent(tool, event.getPlayer(), event.getToolBeforeModification());
                } catch (TinkerGuiException e) {
                    System.out.println("TinkerGUIException! " + e);
                }
            } else {
                //System.out.println("Tool has already been modified in this event, proceeding.");
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        Tooltips.addTooltips(event.getItemStack(), event.getToolTip());
    }

    private EventHandler() {
    }
}
