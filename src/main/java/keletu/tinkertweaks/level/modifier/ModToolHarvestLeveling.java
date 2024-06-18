package keletu.tinkertweaks.level.modifier;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import keletu.tinkertweaks.config.Config;
import keletu.tinkertweaks.level.ToolHarvestLevelNBT;
import static keletu.tinkertweaks.util.HarvestLevels._0_stone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;

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
        return (int) (Config.getBaseXpForTool(tool.getItem()) * Config.harvestLevelMultiplier());
    }

    private ToolHarvestLevelNBT getLevelData(ItemStack itemStack) {
        return getLevelData(TinkerUtil.getModifierTag(itemStack, getModifierIdentifier()));
    }

    private static ToolHarvestLevelNBT getLevelData(NBTTagCompound modifierNBT) {
        return new ToolHarvestLevelNBT(modifierNBT);
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        super.applyEffect(rootCompound, modifierTag);

        ToolHarvestLevelNBT data = getLevelData(modifierTag);

        // apply bonus modifiers
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        if (data.isBoosted) {
            if (toolTag.getInteger(Tags.HARVESTLEVEL) == _0_stone)
                return;

            toolTag.setInteger(Tags.HARVESTLEVEL, toolTag.getInteger(Tags.HARVESTLEVEL) + 1);
        }
        TagUtil.setToolTag(rootCompound, toolTag);
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
            data.isBoosted = true;
        }
        data.write(modifierTag);
        TagUtil.setModifiersTagList(tool, tagList);
        if (pickLeveled) {
            this.apply(tool);
            if (!player.world.isRemote) {
                KeletuTinkerTweaks.proxy.playLevelupDing(player);
                player.sendStatusMessage(new TextComponentTranslation(TextFormatting.GOLD + I18n.format("message.levelup.miningboost")), false);
            }
        }
    }
}
