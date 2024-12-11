package michael.openplease;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

public class OpenPlease implements ModInitializer {
	public float doorDistance = 4;
	public static KeyBinding doorToggle;
	public boolean toggleState = true;

	@Override
	public void onInitialize() {
		doorToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.openplease.toggle",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"category.openplease"
		));

		net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
	}

	private void onWorldTick(ServerWorld world) {
		if (doorToggle.wasPressed()){
			toggleState = !toggleState;

			if (toggleState) {
				MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("Auto-Open Enabled!").formatted(Formatting.GREEN), true);
			}
			if (!toggleState) {
				MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("Auto-Open Disabled!").formatted(Formatting.RED), true);
			}
		}
		if (toggleState) {
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
			}
		);
	}}

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
