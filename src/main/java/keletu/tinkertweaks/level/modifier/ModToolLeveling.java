package keletu.tinkertweaks.level.modifier;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import keletu.tinkertweaks.config.Config;
import keletu.tinkertweaks.level.ToolLevelNBT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.tools.SwordCore;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.utils.*;
import slimeknights.tconstruct.tools.common.entity.EntityShuriken;
import slimeknights.tconstruct.tools.melee.TinkerMeleeWeapons;
import slimeknights.tconstruct.tools.ranged.item.Shuriken;

import java.util.List;

public class ModToolLeveling extends ProjectileModifierTrait {

    public ModToolLeveling() {
        super("tinkertweaks", 0xffffff);

        aspects.clear();
        addAspects(new ModifierAspect.DataAspect(this));

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

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        super.applyEffect(rootCompound, modifierTag);

        ToolLevelNBT data = getLevelData(modifierTag);

        // apply bonus modifiers
        NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
        int modifiers = toolTag.getInteger(Tags.FREE_MODIFIERS) + data.bonusModifiers;
        toolTag.setInteger(Tags.FREE_MODIFIERS, Math.max(0, modifiers));
        TagUtil.setToolTag(rootCompound, toolTag);
    }

    /* Actions that award XP */

    @Override
    public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
        if (wasEffective && player instanceof EntityPlayer) {
            addXp(tool, 1, (EntityPlayer) player, false);
        }
    }

    @Override
    public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
        if (player != null && !player.world.isRemote && player.getActiveItemStack() == tool) {
            int xp = Math.round(event.getAmount());
            addXp(tool, xp, player, false);
        }
    }

    @SubscribeEvent
    public void onMattock(TinkerToolEvent.OnMattockHoe event) {
        addXp(event.itemStack, 1, event.player, false);
    }

    @SubscribeEvent
    public void onScythe(TinkerToolEvent.OnScytheHarvest event) {
        if (!event.isCanceled()) {
            addXp(event.itemStack, 1, event.player, false);
        }
    }

    @SubscribeEvent
    public void onPath(TinkerToolEvent.OnShovelMakePath event) {
        addXp(event.itemStack, 1, event.player, false);
    }


    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onHurt(LivingHurtEvent event) {
        // only player caused damage
        if (!(event.getSource().damageType.equals("player") || event.getSource().damageType.equals("arrow")))
            return;

        // only players
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        // but no fake players
        if (player instanceof FakePlayer)
            return;

        ItemStack stack = player.getHeldItemMainhand();
        if (event.getSource().getImmediateSource() instanceof EntityShuriken) {
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof Shuriken)) {
                if (player.inventory.currentItem == 0)
                    stack = player.inventory.getStackInSlot(8);
                else
                    stack = player.inventory.getStackInSlot(player.inventory.currentItem + 1);
            }

            if (stack != ItemStack.EMPTY && !(stack.getItem() instanceof Shuriken))
                stack = ItemStack.EMPTY;
        }

        if (stack == ItemStack.EMPTY || !stack.hasTagCompound())
            return;

        if (stack.getItem() == null || !(stack.getItem() instanceof ToolCore))
            return;

        int xp = 0;
        // is a weapon?
        if (stack.getItem() instanceof SwordCore)
            xp = Math.round(event.getAmount());
        else
            xp = Math.round((event.getAmount() - 0.1f) / 2);

        // reduce xp for hitting poor animals
        if (event.getEntityLiving() instanceof EntityAnimal)
            xp = Math.max(1, xp / 2);

        // dead stuff gives little xp
        boolean cheatyXP = false;
        if (!event.getEntityLiving().isEntityAlive()) {
            xp = Math.max(1, Math.round(xp / 4f));
            cheatyXP = true;
        }

        ItemStack ammo = ItemStack.EMPTY;
        // projectile weapons also get xp on their ammo!
        if (stack.getItem() instanceof BowCore && event.getSource().damageType.equals("arrow")) {
            ammo = ((BowCore) stack.getItem()).getAmmoToRender(stack, player);
            if (ammo != ItemStack.EMPTY && !(ammo.getItem() instanceof ToolCore))
                ammo = ItemStack.EMPTY;
        }

        // projectile weapons and ammo only get xp when they're shot
        if (!event.getSource().damageType.equals("arrow")) {
            if (stack.getItem() instanceof BowCore)
                return;
            if (stack.getItem() instanceof ProjectileCore)
                return;
        }

        for (ItemStack itemstack : new ItemStack[]{stack, ammo})
            addXp(itemstack, xp, player, cheatyXP);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onLivingHurt(LivingAttackEvent event) {
        // if it's cancelled it got handled by the battlesign (or something else. but it's a prerequisite.)
        if (!event.isCanceled()) {
            return;
        }
        if (event.getSource().isUnblockable() || !event.getSource().isProjectile() || event.getSource().getTrueSource() == null) {
            return;
        }
        // hit entity is a player?
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        // needs to be blocking with a battlesign
        if (!player.isActiveItemStackBlocking() || player.getActiveItemStack().getItem() != TinkerMeleeWeapons.battleSign) {
            return;
        }
        // broken battlesign.
        if (ToolHelper.isBroken(player.getActiveItemStack())) {
            return;
        }

        // at this point we duplicated all the logic if the battlesign should reflect a projectile.. bleh.
        int xp = Math.max(1, Math.round(event.getAmount()));
        addXp(player.getActiveItemStack(), xp, player, false);
    }

    /* XP Handling */

    public void addXp(ItemStack tool, int amount, EntityPlayer player, boolean isCheat) {
        NBTTagList tagList = TagUtil.getModifiersTagList(tool);
        int index = TinkerUtil.getIndexInCompoundList(tagList, identifier);
        NBTTagCompound modifierTag = tagList.getCompoundTagAt(index);
        IModifier modifier = null;
        ToolLevelNBT data = getLevelData(modifierTag);
        // Special case for max(int) damage, prevent overflow by not granting any XP
        if (amount + data.xp < 0) {
            amount = 0;
        }

        int cheat = 0;
        if (isCheat) {
            cheat = data.cheat_xp + data.xp;
        }

        data.xp += amount;

        // is max level?
        if (!Config.canLevelUp(data.level)) {
            return;
        }

        int xpForLevelup = getXpForLevelup(data.level, tool);

        boolean leveledUp = false;
        // check for levelup
        if (data.xp + data.cheat_xp >= xpForLevelup) {
            // anti cheater check!
            if (cheat * 2 >= xpForLevelup) {
                //you just got rubber chicken'd
                if (player != null && !player.world.isRemote) {
                    String text = I18n.format("message.levelup.chicken");
                    player.sendStatusMessage(new TextComponentTranslation(TextFormatting.DARK_RED + text), false);
                    KeletuTinkerTweaks.proxy.playLevelupChicken(player);
                    ItemStack chicken = new ItemStack(KeletuTinkerTweaks.rubberChicken);
                    tool.getTagCompound().setString("Original", Item.REGISTRY.getNameForObject(tool.getItem()).toString());
                    tool.getTagCompound().setInteger("durationMeta", tool.getItemDamage());
                    chicken.setTagCompound(tool.getTagCompound());

                    // rubber chicken yaaaaay
                    player.inventory.setInventorySlotContents(player.inventory.getSlotFor(tool), chicken);
                }

                data.xp = 0;
                data.cheat_xp = 0;
            } else {
                data.xp = 0; // Do not carry over extra XP; max 1 levelup per instance of XP gain
                data.level++;
                leveledUp = true;
                List<IModifier> modifiers = Config.getModifiers(tool.getItem());
                int modifierIndex;
                boolean applied = false;
                if (Config.addRandomModifierOnLevelup() && Config.bonusModifier().contains(data.level)) {
                    //System.out.println("Doing random modifier on levelup");
                    do {
                        if (Config.addModifierSlotOnLevelup()) {
                            modifierIndex = random.nextInt(modifiers.size());
                        } else {
                            modifierIndex = random.nextInt(modifiers.size() + 1);
                        }

                        //if (modifierIndex == modifiers.size() || Config.addModifierSlotOnLevelup()) {
                        //    data.bonusModifiers++;
                        //}
                        if (modifierIndex != modifiers.size()) {
                            modifier = modifiers.get(modifierIndex);

                            int freeModifiers = ToolHelper.getFreeModifiers(tool);

                            try {
                                if (modifier.canApply(tool, tool)) {
                                    modifier.apply(tool);
                                    applied = true;
                                } else {
                                    modifiers.remove(modifierIndex);
                                    continue;
                                }
                            } catch (TinkerGuiException e) {
                                modifiers.remove(modifierIndex);
                                continue;
                            }

                            data.bonusModifiers += freeModifiers - ToolHelper.getFreeModifiers(tool);
                        }
                    } while (!applied && !modifiers.isEmpty());
                }
                //TODO
                if (Config.bonusEmptyModifier().contains(data.level) && Config.addModifierSlotOnLevelup()) {
                    //System.out.println("Doing extra slot on levelup");
                    // All we need to do for extra free modifier:
                    data.bonusModifiers++;
                }
            }
        }
        data.write(modifierTag);
        //tagList.set(index, modifierTag);
        TagUtil.setModifiersTagList(tool, tagList);
        if (leveledUp) {
            this.apply(tool);
            //System.out.println("Tool nbt after apply:"+TagUtil.getTagSafe(tool));
            try {
                //System.out.println("Tool nbt before rebuild:"+TagUtil.getTagSafe(tool));
                NBTTagCompound rootTag = TagUtil.getTagSafe(tool);
                ToolBuilder.rebuildTool(rootTag, (TinkersItem) tool.getItem());
                tool.setTagCompound(rootTag);
                //System.out.println("Final tool nbt:"+TagUtil.getTagSafe(tool));
            } catch (TinkerGuiException e) {
                // this should never happen
                e.printStackTrace();
            }
            if (!player.world.isRemote) {
                // for some reason the proxy is messed up. cba to fix now
                KeletuTinkerTweaks.proxy.playLevelupDing(player);
                KeletuTinkerTweaks.proxy.sendLevelUpMessage(data.level, tool, player);
                // add extra message for the modifier
                if (modifier != null) KeletuTinkerTweaks.proxy.sendModifierMessage(modifier, tool, player);
            }
        }
    }

    public int getXpForLevelup(int level, ItemStack tool) {
        if (level <= 1) {
            return Config.getBaseXpForTool(tool.getItem());
        }
        return (int) ((float) getXpForLevelup(level - 1, tool) * Config.getLevelMultiplier());
    }

    private ToolLevelNBT getLevelData(ItemStack itemStack) {
        return getLevelData(TinkerUtil.getModifierTag(itemStack, getModifierIdentifier()));
    }

    private static ToolLevelNBT getLevelData(NBTTagCompound modifierNBT) {
        return new ToolLevelNBT(modifierNBT);
    }
}