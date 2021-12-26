/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client;

import java.util.Collections;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FakeEntity extends LivingEntity {

	public FakeEntity(World world) {
		super(EntityType.BAT, world);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT tagCompound) {}

	@Override
	public void readAdditionalSaveData(CompoundNBT tagCompound) {}

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return Collections.emptyList();
	}

	@Override
	public ItemStack getItemBySlot(EquipmentSlotType slotIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemSlot(EquipmentSlotType slotIn, ItemStack stack) {}

	@Override
	public HandSide getMainArm() {
		return HandSide.RIGHT;
	}
}