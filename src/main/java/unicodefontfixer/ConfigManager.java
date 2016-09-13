package unicodefontfixer;

import java.io.File;
import java.util.ArrayList;

import unicodefontfixer.mods.*;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigManager {

	public Configuration file;
	public Property fixDerpyFont;
	public Property blacklist;
	public ArrayList<ModHandler> mods = new ArrayList();
	
	public ConfigManager(File configDir) {
		file = new Configuration(new File(configDir, "UnicodeFontFixer.cfg"));
		mods.add(new Thaumcraft());
		mods.add(new Forestry());
		mods.add(new StevesFactoryManager());
		mods.add(new HardcoreQuestingMode());
		mods.add(new Automagy());
		mods.add(new ElectricalAge());
		mods.add(new TravellersGear());
		mods.add(new ModularPowersuits());
		mods.add(new MineTradingCards());
		mods.add(new TinkersConstruct());
		mods.add(new BiblioCraft());
		mods.add(new AppliedEnergistics());
		mods.add(new ThaumicHorizons());
		mods.add(new BetterRecords());
		mods.add(new LordOfTheRings());
		mods.add(new ArchitectureCraft());
		mods.add(new PneumaticCraft());
		mods.add(new Botania());
		mods.add(new Mekanism());
	}
	
	@SubscribeEvent
	public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(UnicodeFontFixer.MODID)) update();
	}
	
	public void reload() {
		file.load();
		blacklist = file.get("general", "blacklist", new String[] {"Example Mod", "ExampleCraft|Factory"});
		blacklist.setComment((new TextComponentTranslation("config.unicodefontfixer.blacklist")).getFormattedText());
		fixDerpyFont = file.get("general", "fixDerpyFont", "always");
		fixDerpyFont.setComment((new TextComponentTranslation("config.unicodefontfixer.fixDerpyFont").getFormattedText()));
		fixDerpyFont.setComment(fixDerpyFont.getComment()+"\n  disabled: " + new TextComponentTranslation("config.unicodefontfixer.fixDerpyFont.disabled").getFormattedText());
		fixDerpyFont.setComment(fixDerpyFont.getComment()+"\n  always: " + new TextComponentTranslation("config.unicodefontfixer.fixDerpyFont.always").getFormattedText());
		fixDerpyFont.setComment(fixDerpyFont.getComment()+"\n  moderate: " + new TextComponentTranslation("config.unicodefontfixer.fixDerpyFont.moderate").getFormattedText());
		fixDerpyFont.setComment(fixDerpyFont.getComment()+"\n" + new TextComponentTranslation("config.unicodefontfixer.fixDerpyFont.hint").getFormattedText());
		update();
	}
	
	public void update() {
		String[] blackNames = blacklist.getStringList();
		for (int i = 0; i < FontRendererEx.adapters.length; i++) FontRendererEx.adapters[i].clear();
		for (int i = 0; i < mods.size(); i++) {
			ModHandler mh = mods.get(i);
			boolean exclude = !Loader.isModLoaded(mh.getModID());
			for (int j = 0; j < blackNames.length && !exclude; j++) {
				String lhs = mh.getModID().replaceAll("\\s+","");
				String rhs = blackNames[j].replaceAll("\\s+","");
				if (lhs.equalsIgnoreCase(rhs)) exclude = true;
				lhs = mh.getClass().getSimpleName().replaceAll("\\s+",""); 
				if (lhs.equalsIgnoreCase(rhs)) exclude = true;
			}
			if (!exclude) mh.registerAdapters(FontRendererEx.adapters);
		}
		if (fixDerpyFont.getString().toLowerCase().equals("moderate")) {
			fixDerpyFont.set("moderate");
			FontRendererEx.policy = 2;
		} else if (fixDerpyFont.getString().toLowerCase().equals("disabled")) {
			fixDerpyFont.set("disabled");
			FontRendererEx.policy = 0;
		} else {
			fixDerpyFont.set("always");
			FontRendererEx.policy = 1;
		}
		file.save();
	}
	
}
