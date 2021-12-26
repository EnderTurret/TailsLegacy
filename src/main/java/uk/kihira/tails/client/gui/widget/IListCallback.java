/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui.widget;

import net.minecraft.client.gui.components.ObjectSelectionList;

public interface IListCallback<T extends ObjectSelectionList.Entry<T>> {

	boolean onEntrySelected(ListWidget<T> guiList, int index, T entry);
}
