package keletu.tinkertweaks.mobhead.blocks;

import keletu.tinkertweaks.mobhead.IguanaMobHeads;
import keletu.tinkertweaks.mobhead.tilenetities.IguanaSkullTileEntity;
import keletu.tinkertweaks.reference.Reference;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.crafting.IInfusionStabiliser")
public class IguanaSkullBlock extends BlockSkull /*implements IInfusionStabiliser */{

	public IguanaSkullBlock() {
		super();

        this.setHardness(1.0F);
        this.setSoundType(SoundType.STONE);
        this.setTranslationKey(Reference.prefix("skull"));
        this.setRegistryName("skull");
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new IguanaSkullTileEntity();
	}

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(IguanaMobHeads.skullItem, 1, this.damageDropped(state));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return IguanaMobHeads.skullItem;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if(world.getTileEntity(pos) == null && !(world.getTileEntity(pos) instanceof IguanaSkullTileEntity))
            return;

        drops.add(new ItemStack(IguanaMobHeads.skullItem, 1, ((IguanaSkullTileEntity)world.getTileEntity(pos)).getSkullType()));
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(FACING, NODROP).build());
    }

    //@Optional.Method(modid = "Thaumcraft")
    //@Override
    //public boolean canStabaliseInfusion(World world, int x, int y, int z) {
    //    return true;
    //}
}