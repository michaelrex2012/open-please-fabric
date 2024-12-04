package michael.openplease;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OpenPlease implements ModInitializer {
	// Define the distance variable
	private static final double DOOR_TRIGGER_DISTANCE = 1; // Distance in blocks

	@Override
	public void onInitialize() {
		// Register tick callback
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
	}

	private void onWorldTick(ServerWorld world) {
		world.getPlayers().forEach(player -> {
			BlockPos playerPos = player.getBlockPos();

			// Check surrounding blocks within the defined distance
			for (int x = -(int) DOOR_TRIGGER_DISTANCE; x <= (int) DOOR_TRIGGER_DISTANCE; x++) {
				for (int y = -1; y <= 2; y++) { // Include vertical range for doors
					for (int z = -(int) DOOR_TRIGGER_DISTANCE; z <= (int) DOOR_TRIGGER_DISTANCE; z++) {
						BlockPos pos = playerPos.add(x, y, z);
						if (isDoor(world, pos)) {
							handleDoor(world, pos, playerPos);
						}
					}
				}
			}
		});
	}

	private boolean isDoor(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof DoorBlock;
	}

	private void handleDoor(World world, BlockPos doorPos, BlockPos playerPos) {
		DoorBlock door = (DoorBlock) world.getBlockState(doorPos).getBlock();
		double distance = playerPos.getSquaredDistance(doorPos.getX(), doorPos.getY(), doorPos.getZ());

		boolean isOpen = world.getBlockState(doorPos).get(Properties.OPEN);
		double triggerDistanceSquared = DOOR_TRIGGER_DISTANCE * DOOR_TRIGGER_DISTANCE;

		if (distance <= triggerDistanceSquared && !isOpen) {
			// Open the door if within range
			world.setBlockState(doorPos, world.getBlockState(doorPos).with(Properties.OPEN, true));
			playDoorSound(world, doorPos, true);
		} else if (distance > triggerDistanceSquared && isOpen) {
			// Close the door if out of range
			world.setBlockState(doorPos, world.getBlockState(doorPos).with(Properties.OPEN, false));
			playDoorSound(world, doorPos, false);
		}
	}

	private void playDoorSound(World world, BlockPos pos, boolean open) {
		if (open) {
			world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
		} else {
			world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
		}
	}
}
