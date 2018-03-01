package sonar.calculator.mod.common.item.misc;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.calculator.mod.api.items.IStability;
import sonar.calculator.mod.common.entities.EntitySoil;
import sonar.core.common.item.SonarItem;
import sonar.core.utils.SonarCompat;

public class Soil extends SonarItem implements IStability {

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!player.capabilities.isCreativeMode) {
			stack = SonarCompat.shrink(stack, 1);
		}
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		if (!world.isRemote) {
			EntitySoil entity = new EntitySoil(world, player);
			entity.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntityInWorld(entity);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			if (!player.canPlayerEdit(pos, side, stack)) {
				return EnumActionResult.PASS;
			}
			Block block = world.getBlockState(pos).getBlock();

			if (block == Blocks.DIRT) {
				world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
				stack = SonarCompat.shrink(stack, 1);
			}
			if (block == Blocks.GRASS) {
				world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
				stack = SonarCompat.shrink(stack, 1);
			} else {
				return EnumActionResult.PASS;
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean getStability(ItemStack stack) {
		return true;
	}

	@Override
	public void onFalse(ItemStack stack) {
	}
}
