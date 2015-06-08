package sonar.calculator.mod.client.gui.generators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.common.containers.ContainerStarchExtractor;
import sonar.calculator.mod.common.tileentity.generators.TileEntityGenerator;
import sonar.core.utils.helpers.FontHelper;



public class GuiStarchExtractor
  extends GuiContainer
{
  public static final ResourceLocation bground = new ResourceLocation("Calculator:textures/gui/starchgenerator.png");
  
  public TileEntityGenerator.StarchExtractor entity;
  

  public GuiStarchExtractor(InventoryPlayer inventoryPlayer, TileEntityGenerator.StarchExtractor entity)
  {
    super(new ContainerStarchExtractor(inventoryPlayer, entity));
    

    this.entity = entity;
    

    this.xSize = 176;
    this.ySize = 166;
  }
  




  @Override
public void drawGuiContainerForegroundLayer(int par1, int par2)
  {
	FontHelper.textCentre(StatCollector.translateToLocal(entity.getInventoryName()), xSize, 6, 0);
	String power= null;
    switch(CalculatorConfig.energyStorageType){
	case 1: power = this.entity.storage.getEnergyStored() + " RF";	break;
	case 2: power = (this.entity.storage.getEnergyStored()/4) + " EU";	break;
	}
    this.fontRendererObj.drawString(power, this.xSize / 2 - this.fontRendererObj.getStringWidth(power) / 2, 64, -1);
    
  }
  

  @Override
protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
  {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    

    Minecraft.getMinecraft().getTextureManager().bindTexture(bground);
    drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    
    int k = this.entity.storage.getEnergyStored() * 160 / 1000000;
    drawTexturedModalRect(this.guiLeft + 8, this.guiTop + 63, 0, 186, k, 10);
    int k2 = this.entity.itemLevel * 138 / 5000;
    drawTexturedModalRect(this.guiLeft + 30, this.guiTop + 17, 0, 166, k2, 10);

    if(this.entity.maxBurnTime>0){
    int k3 = this.entity.burnTime * 138 / this.entity.maxBurnTime;
    drawTexturedModalRect(this.guiLeft + 30, this.guiTop + 41, 0, 176, k3, 10);}
  }
}