package io.github.moonwolf287.ars_enderstorage;

import com.hollingsworth.arsnouveau.api.RegistryHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public final class ArsEnderStorageConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static       ForgeConfigSpec         CLIENT_CONFIG;

    private ArsEnderStorageConfig() {}

    /*
     * CLIENT
     */
    public static ForgeConfigSpec.BooleanValue SHOW_MANA_NUM;
    public static ForgeConfigSpec.BooleanValue SHOW_MANA_ON_TOP;

    /*
     * Server
     */

    public static void registerGlyphConfigs() {
        RegistryHelper.generateConfig(ArsEnderStorageMod.MODID, ArsNouveauRegistry.registeredSpells);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        // Nothing to do here (yet)
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
        // Nothing to do here (yet)
    }

    static {
        BUILDER.push("Display mana amount numerical");
        SHOW_MANA_NUM = BUILDER.comment("Display numbers").define("showManaNumerical", true);
        SHOW_MANA_ON_TOP = BUILDER.comment("Display numbers above the bar instead of on it")
                                  .define("displayAboveBar", false);
        BUILDER.pop();
        CLIENT_CONFIG = BUILDER.build();
    }
}
