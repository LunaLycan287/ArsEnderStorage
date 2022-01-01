package io.github.moonwolf287.ars_enderstorage;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsEnderStorageMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ManaTextGUI extends AbstractGui {

    /**
     * TODO: Use new Events System in 1.18:
     * https://github.com/MinecraftForge/MinecraftForge/blob/1.18.x/src/main/java/net/minecraftforge/client/gui/OverlayRegistry.java
     * https://github.com/MinecraftForge/MinecraftForge/blob/1.18.x/src/main/java/net/minecraftforge/client/gui/ForgeIngameGui.java
     */

    /**
     * Render the current mana when the SpellBook is held in the players hand
     *
     * @param event
     *         The event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderSpellHUD(final RenderGameOverlayEvent.Post event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || player == null ||
            Boolean.FALSE.equals(ArsEnderStorageConfig.SHOW_MANA_NUM.get())) {
            return;
        }
        drawHUD(event.getMatrixStack(), player);
    }

    private static boolean shouldDisplayBar(ClientPlayerEntity player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return (mainHand.getItem() instanceof IDisplayMana &&
                ((IDisplayMana) mainHand.getItem()).shouldDisplay(mainHand)) ||
               (offHand.getItem() instanceof IDisplayMana && ((IDisplayMana) offHand.getItem()).shouldDisplay(offHand));
    }

    private static void drawHUD(MatrixStack ms, ClientPlayerEntity player) {
        Optional<IMana> manaOpt = ManaCapability.getMana(player).resolve();
        if (!shouldDisplayBar(player) || !manaOpt.isPresent()) {
            return;
        }
        IMana mana = manaOpt.get();

        boolean renderOnTop = ArsEnderStorageConfig.SHOW_MANA_ON_TOP.get();

        Minecraft minecraft = Minecraft.getInstance();

        int offsetLeft = 10;
        int height = minecraft.getWindow().getGuiScaledHeight() - 15;
        int max = mana.getMaxMana();

        String delimiter = renderOnTop ? "/" : "   /   ";

        String textMax = max + delimiter + max;
        String text = (int) mana.getCurrentMana() + delimiter + max;
        int maxWidth = minecraft.font.width(textMax);
        if (renderOnTop) {
            height -= 25;
        } else {
            offsetLeft = 67 - maxWidth / 2;
        }
        offsetLeft += maxWidth - minecraft.font.width(text);

        drawString(ms, minecraft.font, text, offsetLeft, height, 0xFFFFFF);
        if (!renderOnTop) {
            minecraft.textureManager.bind(
                    new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border" + ".png"));
            blit(ms, 10, height - 8, 0, 18, 108, 20, 256, 256);
        }
    }
}
