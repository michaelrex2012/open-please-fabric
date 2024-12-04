package com.example.autodoor;

import net.minecraft.block.DoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.door.DoorBlock;

public class AutoDoorMod {

	public static void autoOpenDoors() {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;

		if (player != null) {
			// Perform raycasting from the player's viewpoint
			BlockHitResult raycastResult = (BlockHitResult) client.crosshairTarget;

			if (raycastResult != null && raycastResult.getType() == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = raycastResult.getBlockPos();
				BlockState blockState = player.getWorld().getBlockState(blockPos);

				// Check if the block is a DoorBlock and if it is closed
				if (blockState.getBlock() instanceof DoorBlock) {
					// If the door is closed, interact to open it
					if (!blockState.get(DoorBlock.OPEN)) {
						player.interactAt(player , raycastResult.getPos(), Hand.MAIN_HAND);
					}
				}
			}
		}
	}
}
