package io.github.moonwolf287.ars_enderstorage;

import com.hollingsworth.arsnouveau.api.RegistryHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class ArsEnderStorageConfig {

    private ArsEnderStorageConfig() {}

    public static void registerGlyphConfigs(){
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
}
