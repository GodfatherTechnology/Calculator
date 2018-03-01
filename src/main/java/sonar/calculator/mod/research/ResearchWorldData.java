package sonar.calculator.mod.research;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import sonar.core.helpers.NBTHelper.SyncType;

public class ResearchWorldData extends WorldSavedData {

	public static final String tag = "sonar.calculator.research";

	public ResearchWorldData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		PlayerResearchRegistry.readData(nbt, SyncType.SAVE);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		PlayerResearchRegistry.writeData(nbt, SyncType.SAVE);
		return nbt;
	}

    @Override
	public boolean isDirty() {
		return true;
	}
}
