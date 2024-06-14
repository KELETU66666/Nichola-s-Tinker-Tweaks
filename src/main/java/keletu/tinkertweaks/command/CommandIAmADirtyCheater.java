package keletu.tinkertweaks.command;

import keletu.tinkertweaks.KeletuTinkerTweaks;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandIAmADirtyCheater extends CommandBase {

    @Override
    public String getName() {
        return "imadirtycheater";
    }

    @Override
    public String getUsage(ICommandSender p_71518_1_) {
        if (isCheater(p_71518_1_)) {
            return "I heard you like chicken";
        } else {
            return "Why are you saying that you're a dirty cheater?";
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!isCheater(sender)) {
            sender.sendMessage(new TextComponentTranslation(TextFormatting.ITALIC + "You don't look like a cheater to me"));
        } else {
            String cmd = getName();
            String arg = I18n.format("message.apology");

            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb.append(s);
                sb.append(" ");
            }

            if (arg.toLowerCase().trim().equals(sb.toString().toLowerCase().trim())) {
                sender.sendMessage(new TextComponentTranslation(I18n.format("message.apologyaccepted")));
                convertTool(sender, getCurrentItem(sender));
            } else {
                sender.sendMessage(
                        new TextComponentTranslation(TextFormatting.ITALIC + "If you're really sorry, type:"));
                sender.sendMessage(new TextComponentTranslation("/" + cmd + " " + arg));
            }
        }
    }

    public ItemStack getCurrentItem(ICommandSender sender) {
        EntityPlayerMP player = null;
        try {
            player = getCommandSenderAsPlayer(sender);
        } catch (PlayerNotFoundException e) {
            throw new RuntimeException(e);
        }

        return player.getHeldItemMainhand();
    }

    public boolean isCheater(ICommandSender sender) {
        ItemStack stack = getCurrentItem(sender);
        return stack != null && stack.getItem() == KeletuTinkerTweaks.rubberChicken;
    }

    private void convertTool(ICommandSender sender, ItemStack stack) {
        if (stack.getItem() != KeletuTinkerTweaks.rubberChicken)
            return;

        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Original"))
            return;


        String unloc = stack.getTagCompound().getString("Original");
        Item.REGISTRY.containsKey(new ResourceLocation(unloc));
        Item item = null;
        try {
            item = getItemByText(sender, unloc);
        } catch (NumberInvalidException e) {
            throw new RuntimeException(e);
        }
        if (item == null)
            return;

        ItemStack itemStack = new ItemStack(item, 1, stack.getTagCompound().getInteger("durationMeta"));
        itemStack.setTagCompound(stack.getTagCompound());
        try {
            getCommandSenderAsPlayer(sender).inventory.setInventorySlotContents(getCommandSenderAsPlayer(sender).inventory.getSlotFor(stack), itemStack);
        } catch (PlayerNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}