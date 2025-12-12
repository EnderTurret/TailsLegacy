package uk.kihira.tails.forge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiSlot;

import uk.kihira.tails.forge.client.gui.widget.SimpleGuiList;

@Mixin(GuiSlot.class)
public abstract class MixinGuiSlot {

	@SuppressWarnings("cast")
	@Inject(at = @At("HEAD"), method = "overlayBackground", cancellable = true)
	private void tails$stopDrawingBackgrounds(CallbackInfo ci) {
		if ((Object) this instanceof SimpleGuiList)
			ci.cancel();
	}
}