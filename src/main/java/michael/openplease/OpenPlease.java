package michael.openplease;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OpenPlease implements ModInitializer {
	public float doorDistance = 4;

	@Override
	public void onInitialize() {
		// Register tick callback
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
	}

	private void onWorldTick(ServerWorld world) {
		world.getPlayers().forEach(player -> {
			BlockPos playerPos = player.getBlockPos();

			// Check surrounding blocks within 2 blocks
			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					for (int z = -2; z <= 2; z++) {
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

		boolean isOpen = world.getBlockState(doorPos).get(DoorBlock.OPEN);
		if (distance <= doorDistance && !isOpen) {
			world.setBlockState(doorPos, world.getBlockState(doorPos).with(DoorBlock.OPEN, true));
		} else if (distance > doorDistance && isOpen) {
			world.setBlockState(doorPos, world.getBlockState(doorPos).with(DoorBlock.OPEN, false));
		}
	}
}
