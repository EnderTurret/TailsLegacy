/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.dialog;

import net.minecraft.client.gui.widget.button.Button;

public interface IDialogCallback {

	void buttonPressed(Dialog dialog, Button button);
}