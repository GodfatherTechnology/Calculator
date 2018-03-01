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
import sonar.calculator.mod.common.entities.EntitySmallStone;
import sonar.core.common.item.SonarItem;
import sonar.core.utils.SonarCompat;

public class SmallStone extends SonarItem {

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!player.capabilities.isCreativeMode) {
			stack = SonarCompat.shrink(stack, 1);
		}
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!world.isRemote) {
			EntitySmallStone entity = new EntitySmallStone(world, player);
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
				world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
				stack = SonarCompat.shrink(stack, 1);
			}
			if (block == Blocks.GRASS) {
				world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
				stack = SonarCompat.shrink(stack, 1);
			} else {
				return EnumActionResult.PASS;
			}
		}
		return EnumActionResult.SUCCESS;
	}
}
