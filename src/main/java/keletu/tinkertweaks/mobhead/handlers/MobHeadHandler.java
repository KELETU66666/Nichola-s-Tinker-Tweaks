package keletu.tinkertweaks.mobhead.handlers;

import keletu.tinkertweaks.config.Config;
import keletu.tinkertweaks.mobhead.IguanaMobHeads;
import keletu.tinkertweaks.mobhead.items.IguanaSkull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.melee.TinkerMeleeWeapons;

import java.util.Iterator;

public class MobHeadHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void LivingDrops(LivingDropsEvent event)
    {
        // remove regular skull drops (because we modify its drop chance)
        Iterator<EntityItem> i = event.getDrops().iterator();
        while (i.hasNext()) {
            EntityItem eitem = i.next();

            if (eitem != null)
                if (eitem.getItem() != ItemStack.EMPTY)
                {
                    ItemStack item = eitem.getItem();
                    if (item.getItem() == Items.SKULL && item.getItemDamage() != 3)
                        i.remove();
                }
        }

        // add our own drops if the damage source was a player (and no fake player)
        Entity entity = event.getSource().getTrueSource();
        if(entity == null)
            return;
        if(!(entity instanceof EntityPlayer) || entity instanceof FakePlayer)
            return;

        // how much beheading chance do we have?
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack stack = player.getHeldItemMainhand();
        int beheading = 0;
        if(stack.hasTagCompound()) {
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ToolCore) {
                beheading = stack.getTagCompound().getCompoundTag("Stats").getInteger("Beheading");
                if (stack.getItem() == TinkerMeleeWeapons.cleaver)
                    beheading += 2;
            }
        }
        // roll the dice
        if(entity.world.rand.nextInt(100) > beheading * Config.beheadingHeadDropChance() + Config.baseHeadDropChance())
            return;

        Item skullItem = null;
        int skullId = -1;

        Entity mob = event.getEntityLiving();
        // skelly/witherskelly
        if (mob instanceof EntitySkeleton) {
            skullItem = Items.SKULL;
            skullId = 0;
        }
        else if (mob instanceof EntityWitherSkeleton) {
            skullItem = Items.SKULL;
            skullId = 1;
        }
        else if (mob instanceof EntityPigZombie) {
            skullItem = IguanaMobHeads.skullItem;
            skullId = IguanaSkull.META_PIGZOMBIE;
        }
        else if (mob instanceof EntityZombie) {
            skullItem = Items.SKULL;
            skullId = 2;
        }
        else if (mob instanceof EntityCreeper) {
            skullItem = Items.SKULL;
            skullId = 4;
        }
        else if (mob instanceof EntityEnderman) {
            skullItem = IguanaMobHeads.skullItem;
            skullId = IguanaSkull.META_ENDERMAN;

            // sometimes, very very rarely, you'll only get the jaw :D
            if(mob.world.rand.nextInt(1000) == 0)
            {
                skullItem = IguanaMobHeads.wearables;
                skullId = 2;
            }
        }
        else if (mob instanceof EntityBlaze) {
            skullItem = IguanaMobHeads.skullItem;
            skullId = IguanaSkull.META_BLAZE;
        }
        // mod support
        else {
            String mobName = EntityList.getEntityString(mob);
            // thermal expansion
            if("Blizz".equals(mobName))
            {
                skullItem = IguanaMobHeads.skullItem;
                skullId = IguanaSkull.META_BLIZZ;
            }
        }

        // no skull found?
        if(skullItem == null)
            return;

        // drop it like it's hot
        EntityItem entityitem = new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(skullItem, 1, skullId));
        entityitem.setPickupDelay(10);
        event.getDrops().add(entityitem);
    }
}