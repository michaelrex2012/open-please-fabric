package michael.openplease;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.TrapdoorBlock;
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
			for (int x = -4; x <= 4; x++) {
				for (int y = -4; y <= 4; y++) {
					for (int z = -4; z <= 4; z++) {
						BlockPos pos = playerPos.add(x, y, z);
						if (isDoor(world, pos)) {
							handleDoor(world, pos, playerPos);
						}
						if (isTrapdoor(world, pos)) {
							handleTrapdoor(world, pos, playerPos);
						}
						if (isFenceGate(world, pos)) {
							handleFenceGate(world, pos, playerPos);
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

	private boolean isTrapdoor(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof TrapdoorBlock;
	}

	private boolean isFenceGate(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof FenceGateBlock;
	}

	private void handleDoor(World world, BlockPos doorPos, BlockPos playerPos) {
		double distance = playerPos.getSquaredDistance(doorPos.getX(), doorPos.getY(), doorPos.getZ());

		boolean isOpen = world.getBlockState(doorPos).get(DoorBlock.OPEN);
		if (distance <= doorDistance && !isOpen) {
			world.setBlockState(doorPos, world.getBlockState(doorPos).with(DoorBlock.OPEN, true));
		} else if (distance > doorDistance && isOpen) {
			world.setBlockState(doorPos, world.getBlockState(doorPos).with(DoorBlock.OPEN, false));
		}
	}

	private void handleTrapdoor(World world, BlockPos trapdoorPos, BlockPos playerPos) {
		double distance = playerPos.getSquaredDistance(trapdoorPos.getX(), trapdoorPos.getY(), trapdoorPos.getZ());

		boolean isOpen = world.getBlockState(trapdoorPos).get(TrapdoorBlock.OPEN);
		if (distance <= doorDistance && !isOpen) {
			world.setBlockState(trapdoorPos, world.getBlockState(trapdoorPos).with(TrapdoorBlock.OPEN, true));
		} else if (distance > doorDistance && isOpen) {
			world.setBlockState(trapdoorPos, world.getBlockState(trapdoorPos).with(TrapdoorBlock.OPEN, false));
		}
	}

	private void handleFenceGate(World world, BlockPos fenceGatePos, BlockPos playerPos) {
		double distance = playerPos.getSquaredDistance(fenceGatePos.getX(), fenceGatePos.getY(), fenceGatePos.getZ());

		boolean isOpen = world.getBlockState(fenceGatePos).get(FenceGateBlock.OPEN);
		if (distance <= doorDistance && !isOpen) {
			world.setBlockState(fenceGatePos, world.getBlockState(fenceGatePos).with(FenceGateBlock.OPEN, true));
		} else if (distance > doorDistance && isOpen) {
			world.setBlockState(fenceGatePos, world.getBlockState(fenceGatePos).with(FenceGateBlock.OPEN, false));
		}
	}
}
