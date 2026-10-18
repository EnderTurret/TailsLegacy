/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.fabric.mixin.client.duck;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.enderturret.tailslegacy.common.client.duck.TailsPoseStack;

@Mixin(PoseStack.Pose.class)
public class MixinPoseStackPose implements TailsPoseStack.Entry {

}