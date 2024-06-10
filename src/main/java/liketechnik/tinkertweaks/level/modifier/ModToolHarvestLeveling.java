package liketechnik.tinkertweaks.level.modifier;

import liketechnik.tinkertweaks.LiketechniksTinkerTweaks;
import liketechnik.tinkertweaks.config.Config;
import liketechnik.tinkertweaks.level.ToolHarvestLevelNBT;
import static liketechnik.tinkertweaks.level.ToolHarvestLevelNBT.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.*;

import java.util.List;

public class ModToolHarvestLeveling extends ModifierTrait {

    public ModToolHarvestLeveling() {
        super("harvest_leveling_k", 0xffffff);

        aspects.clear();
        addAspects(new ModifierAspect.DataAspect(this), ModifierAspect.harvestOnly);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return true;
    }

    /* Actions that award XP */

    @Override
    public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
        if (wasEffective && player instanceof EntityPlayer) {
            addXp(tool, 1, (EntityPlayer) player);
        }
    }

    public int getRequiredBoostXp(long level, ItemStack tool) {
        return getXpForLevelup((int) level, tool);
    }

    public int getXpForLevelup(int level, ItemStack tool) {
        return Config.getBaseXpForTool(tool.getItem());
    }

    private ToolHarvestLevelNBT getLevelData(ItemStack itemStack) {
        return getLevelData(TinkerUtil.getModifierTag(itemStack, getModifierIdentifier()));
    }

    private static ToolHarvestLevelNBT getLevelData(NBTTagCompound modifierNBT) {
        return new ToolHarvestLevelNBT(modifierNBT);
    }

    public void addXp(ItemStack tool, int amount, EntityPlayer player) {
        NBTTagList tagList = TagUtil.getModifiersTagList(tool);
        int index = TinkerUtil.getIndexInCompoundList(tagList, identifier);
        NBTTagCompound modifierTag = tagList.getCompoundTagAt(index);

        NBTTagCompound tags = TagUtil.getTagSafe(tool);

        ToolHarvestLevelNBT data = getLevelData(modifierTag);

        if (data.isBoosted) {
            return;
        }

        // Special case for max(int) damage, prevent overflow by not granting any XP
        if (amount + data.bxp < 0) {
            amount = 0;
        }
        data.bxp += amount;

        if (!Config.levelingPickaxeBoost()) {
            return;
        }

        boolean pickLeveled = false;

        int xpForLevelup = getRequiredBoostXp(data.level, tool);

        // we can only if we have a proper material (>stone) and are not max mining level already
        if (data.bxp > xpForLevelup) {
            data.bxp = 0; // Do not carry over extra XP; max 1 levelup per instance of XP gain
            data.level++;
            pickLeveled = true;
            data.isBoosted=true;
        }
        data.write(modifierTag);
        TagUtil.setModifiersTagList(tool, tagList);
        if (pickLeveled) {
            this.apply(tool);
            //System.out.println("Tool nbt after apply:"+TagUtil.getTagSafe(tool));
            try {
                //System.out.println("Tool nbt before rebuild:"+TagUtil.getTagSafe(tool));
                ToolBuilder.rebuildTool(tags, (TinkersItem) tool.getItem());
                //if(Config.addBonusStatsOnLevelup()) {
                //    // All we need to do for stat growth:
                //    tool = updateStats(tool);
                //}
                levelUpMiningLevel(tags, player);
                //System.out.println("Final tool nbt:"+TagUtil.getTagSafe(tool));
            } catch (TinkerGuiException e) {
                // this should never happen
                e.printStackTrace();
            }
            if (!player.world.isRemote)
                LiketechniksTinkerTweaks.proxy.playLevelupDing(player);
        }
    }
}
