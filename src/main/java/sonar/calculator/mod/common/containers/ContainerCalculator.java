package sonar.calculator.mod.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.recipes.CalculatorRecipes;
import sonar.calculator.mod.common.tileentity.misc.TileEntityCalculator;
import sonar.calculator.mod.utils.SlotPortableCrafting;
import sonar.calculator.mod.utils.SlotPortableResult;
import sonar.core.api.SonarAPI;
import sonar.core.api.utils.ActionType;
import sonar.core.common.item.InventoryItem;
import sonar.core.inventory.ContainerSonar;
import sonar.core.inventory.TransferSlotsManager;
import sonar.core.inventory.TransferSlotsManager.DisabledSlots;
import sonar.core.inventory.TransferSlotsManager.TransferSlots;
import sonar.core.inventory.TransferSlotsManager.TransferType;
import sonar.core.recipes.RecipeHelperV2;

public class ContainerCalculator extends ContainerSonar implements ICalculatorCrafter {
	private final InventoryItem inventory;
	public static TransferSlotsManager<InventoryItem> transfer = new TransferSlotsManager() {
		{
			addTransferSlot(new TransferSlots<InventoryItem>(TransferType.TILE_INV, 2));
			addTransferSlot(new DisabledSlots<InventoryItem>(TransferType.TILE_INV, 1));
			addPlayerInventory();
		}
	};

	private boolean isRemote;
	private EntityPlayer player;

	public ContainerCalculator(EntityPlayer player, InventoryItem inventoryItem) {
		this.player = player;
		this.inventory = inventoryItem;
		isRemote = player.getEntityWorld().isRemote;
		addSlotToContainer(new SlotPortableCrafting(this, inventory, 0, 25, 35, isRemote, Calculator.itemCalculator));
		addSlotToContainer(new SlotPortableCrafting(this, inventory, 1, 79, 35, isRemote, Calculator.itemCalculator));
		addSlotToContainer(new SlotPortableResult(player, inventory, this, new int[] { 0, 1 }, 2, 134, 35, isRemote));
		addInventoryWithLimiter(player.inventory, 8, 84, Calculator.itemCalculator);
		onItemCrafted();
	}

	@Override
	public void onItemCrafted() {
		inventory.setInventorySlotContents(2, RecipeHelperV2.getItemStackFromList(CalculatorRecipes.instance().getOutputs(player, inventory.getStackInSlot(0), inventory.getStackInSlot(1)), 0), isRemote);
	}

	public void removeEnergy(int remove) {
		if (player.capabilities.isCreativeMode) {
			return;
		}
		SonarAPI.getEnergyHelper().extractEnergy(player.getHeldItemMainhand(), remove, ActionType.PERFORM);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return transfer.transferStackInSlot(this, inventory, player, slotID);
	}

	@Override
	public ItemStack slotClick(int slot, int drag, ClickType click, EntityPlayer player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}
		return super.slotClick(slot, drag, click, player);
	}
}
