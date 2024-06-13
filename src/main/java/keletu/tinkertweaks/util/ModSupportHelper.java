package keletu.tinkertweaks.util;

import net.minecraftforge.fml.common.Loader;

public final class ModSupportHelper {
    private ModSupportHelper() {} // non-instantiable

    public static final boolean tiCTooltips = Loader.isModLoaded("TiCTooltips");
    public static final boolean PlusTic = Loader.isModLoaded("plustic");
    public static final boolean BiomesOPlenty = Loader.isModLoaded("BiomesOPlenty");
    public static final boolean Mekanism = Loader.isModLoaded("Mekanism");
    public static final boolean Metallurgy = Loader.isModLoaded("Metallurgy");
    public static final boolean Natura = Loader.isModLoaded("Natura");
    public static final boolean AppliedEnergistics2 = Loader.isModLoaded("appliedenergistics2");

    public static final boolean ThermalFoundation = Loader.isModLoaded("thermalfoundation");
}