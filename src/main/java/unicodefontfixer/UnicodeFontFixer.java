package unicodefontfixer;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = UnicodeFontFixer.MODID, useMetadata = true)
public class UnicodeFontFixer {
	
	public static final String MODID = "UnicodeFontFixer";
	
	@Instance(MODID)
	public static UnicodeFontFixer instance;
	
	@SideOnly(Side.CLIENT)
	public FontRendererEx fontRendererStandard, fontRendererGalactic;
	
	@SideOnly(Side.CLIENT)
	public ConfigManager configManager;
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent event) {
		configManager = new ConfigManager(event.getModConfigurationDirectory());
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void initialize(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(configManager);
		configManager.reload();
		Minecraft mc = Minecraft.getMinecraft();
		fontRendererStandard = new FontRendererEx(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
		fontRendererGalactic = new FontRendererEx(mc.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), mc.renderEngine, false);
		mc.fontRendererObj = setDelegate(mc.fontRendererObj, fontRendererStandard);
		mc.standardGalacticFontRenderer = setDelegate(mc.standardGalacticFontRenderer, fontRendererGalactic);
	}
	
	@SideOnly(Side.CLIENT)
	public FontRenderer setDelegate(FontRenderer inner, FontRendererEx outer) {
		outer.proxy = inner;
		outer.FONT_HEIGHT = inner.FONT_HEIGHT;
		outer.fontRandom  = inner.fontRandom;
		return outer;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiOpen(GuiOpenEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		FontRendererEx.checkingStackDepth = true;
		mc.fontRendererObj.drawString("", 0, 0, 0);
		mc.standardGalacticFontRenderer.drawString("", 0, 0, 0);
		FontRendererEx.checkingStackDepth = false;
		if (event.getGui() instanceof GuiLanguage)
			event.setGui( new GuiLanguageEx((GuiLanguage)event.getGui()) );
	}
	
}
