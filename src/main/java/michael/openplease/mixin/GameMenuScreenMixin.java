package michael.openplease.mixin;

import michael.openplease.OpenPlease;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin {

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void addConfigButton(CallbackInfo ci) {
        GameMenuScreen screen = (GameMenuScreen) (Object) this;

        int y = screen.height / 4 + 120 + 12; // Position under the "Return to Title" button
        int x = screen.width / 2 - 102;

        screen.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("menu.openplease.config"),
                        button -> {
                            OpenPlease.openConfigScreen(screen); // Call your config screen method
                        })
                .dimensions(x, y, 204, 20)
                .build());
    }
}