package ru.roughcipher.spike.mixin.client.fix.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.TextFieldElement;
import net.minecraft.client.gui.text.TextFieldEditor;
import net.minecraft.client.render.font.FontRenderer;
import net.minecraft.core.util.helper.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldElement.class)
public abstract class TextFieldCursor extends Gui {

	@Shadow @Final private FontRenderer fontRenderer;
	@Shadow public int xPosition;
	@Shadow public int yPosition;
	@Shadow @Final public int width;
	@Shadow @Final public int height;
	@Shadow private String text;
	@Shadow private int cursorCounter;
	@Shadow public boolean isFocused;
	@Shadow public boolean isEnabled;
	@Shadow private String placeholder;
	@Shadow public boolean drawBackground;
	@Shadow @Final private TextFieldEditor editor;

	@Inject(method = "drawTextBox", at = @At("HEAD"), cancellable = true)
	private void spike$drawTextBoxFixed(CallbackInfo ci) {
		ci.cancel();

		if (this.drawBackground) {
			this.drawRect(
				this.xPosition - 1,
				this.yPosition - 1,
				this.xPosition + this.width + 1,
				this.yPosition + this.height + 1,
				-6250336
			);
			this.drawRect(
				this.xPosition,
				this.yPosition,
				this.xPosition + this.width,
				this.yPosition + this.height,
				-16777216
			);
		}

		int textX = this.xPosition + 4;
		int textY = this.yPosition + (this.height - 8) / 2;

		if (this.text.isEmpty() && !this.isFocused) {
			this.drawStringShadow(this.fontRenderer, this.placeholder, textX, textY, 6250335);
			return;
		}

		if (!this.isEnabled) {
			this.drawStringShadow(this.fontRenderer, this.text, textX, textY, 7368816);
			return;
		}

		int cursor = this.editor.getCursor();
		int maxDrawWidth = this.width - 8;

		int scrollStart = 0;
		if (cursor > 0) {
			while (scrollStart < cursor) {
				int w = MathHelper.ceil(
					this.fontRenderer.stringWidthDouble(this.text.substring(scrollStart, cursor))
				);
				if (w <= maxDrawWidth) {
					break;
				}
				scrollStart++;
			}
		}

		int scrollEnd = cursor;
		while (scrollEnd < this.text.length()) {
			int w = MathHelper.ceil(
				this.fontRenderer.stringWidthDouble(this.text.substring(scrollStart, scrollEnd + 1))
			);
			if (w > maxDrawWidth) {
				break;
			}
			scrollEnd++;
		}

		while (scrollStart > 0) {
			int w = MathHelper.ceil(
				this.fontRenderer.stringWidthDouble(this.text.substring(scrollStart - 1, scrollEnd))
			);
			if (w > maxDrawWidth) {
				break;
			}
			scrollStart--;
		}

		String visible = this.text.substring(scrollStart, scrollEnd);
		this.drawStringShadow(this.fontRenderer, visible, textX, textY, 14737632);

		boolean showCursor = this.isFocused && (this.cursorCounter / 6) % 2 == 0;
		if (showCursor) {
			int cursorPixelOffset = 0;
			if (cursor > scrollStart) {
				cursorPixelOffset = MathHelper.ceil(
					this.fontRenderer.stringWidthDouble(this.text.substring(scrollStart, cursor))
				);
			}
			if (cursorPixelOffset > maxDrawWidth) {
				cursorPixelOffset = maxDrawWidth;
			}
			this.drawStringShadow(
				this.fontRenderer,
				"_",
				textX + cursorPixelOffset,
				textY + 1,
				14737632
			);
		}
	}
}
