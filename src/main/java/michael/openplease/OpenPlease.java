package michael.openplease;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

public class OpenPlease implements ModInitializer {
	public float doorDistance = 4;
	public static KeyBinding openToggle;
	public static KeyBinding soundToggle;
	public static KeyBinding getStates;
	public boolean toggleOpen = true;
	public boolean toggleSound = true;

	@Override
	public void onInitialize() {
		openToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.openplease.toggleopen",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"category.openplease"
		));
		soundToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.openplease.togglesound",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_Y,
				"category.openplease"
		));
		getStates = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.openplease.getstates",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_U,
				"category.openplease"
		));

		ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
	}

	private void onWorldTick(ServerWorld world) {
		if (openToggle.wasPressed()){
			toggleOpen = !toggleOpen;

			if (toggleOpen) {
				MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("Auto-Open Enabled!").formatted(Formatting.GREEN), true);
			}
			if (!toggleOpen) {
				MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("Auto-Open Disabled!").formatted(Formatting.RED), true);
			}
		}
		if (soundToggle.wasPressed()){
			toggleSound = !toggleSound;

			if (toggleSound) {
				MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("Sound Enabled!").formatted(Formatting.GREEN), true);
			}
			if (!toggleSound) {
				MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("Sound Disabled!").formatted(Formatting.RED), true);
			}
		}
		if (getStates.wasPressed()){
			Formatting toggleOpenFormat = toggleOpen ? Formatting.GREEN : Formatting.RED;
			Formatting toggleSoundFormat = toggleSound ? Formatting.GREEN : Formatting.RED;
			String toggleOpenText = toggleOpen ? "Enabled" : "Disabled";
			String toggleSoundText = toggleSound ? "Enabled" : "Disabled";

			MinecraftClient.getInstance().inGameHud.setOverlayMessage(
					Text.literal("Open: ").formatted(Formatting.WHITE)
							.append(Text.literal(toggleOpenText).formatted(toggleOpenFormat))
							.append(Text.literal(" Sound: ").formatted(Formatting.WHITE))
							.append(Text.literal(toggleSoundText).formatted(toggleSoundFormat)),
					true
			);
		}

		if (toggleOpen) {
			world.getPlayers().forEach(player -> {
				BlockPos playerPos = player.getBlockPos();

				// Check surrounding blocks within 2 blocks
				for (int x = -4; x <= 4; x++) {
					for (int y = -4; y <= 4; y++) {
						for (int z = -4; z <= 4; z++) {
							BlockPos pos = playerPos.add(x, y, z);

							if (isDoor(world, pos)) {
								boolean oldStateDoor = world.getBlockState(pos).get(DoorBlock.OPEN);
								handleDoor(world, pos, playerPos);
								if (oldStateDoor && !world.getBlockState(pos).get(DoorBlock.OPEN) && toggleSound) {
									world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
								}
								if (!oldStateDoor && world.getBlockState(pos).get(DoorBlock.OPEN) && toggleSound) {
									world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
								}
							}
							if (isTrapdoor(world, pos)) {
								boolean oldStateTrapDoor = world.getBlockState(pos).get(TrapdoorBlock.OPEN);
								handleTrapdoor(world, pos, playerPos);
								if (oldStateTrapDoor && !world.getBlockState(pos).get(TrapdoorBlock.OPEN) && toggleSound) {
									world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
								}
								if (!oldStateTrapDoor && world.getBlockState(pos).get(TrapdoorBlock.OPEN) && toggleSound) {
									world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
								}
							}
							if (isFenceGate(world, pos)) {
								boolean oldStateFenceGate = world.getBlockState(pos).get(FenceGateBlock.OPEN);
								handleFenceGate(world, pos, playerPos);
								if (oldStateFenceGate && !world.getBlockState(pos).get(FenceGateBlock.OPEN) && toggleSound) {
									world.playSound(null, pos, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
								}
								if (!oldStateFenceGate && world.getBlockState(pos).get(FenceGateBlock.OPEN) && toggleSound) {
									world.playSound(null, pos, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
								}
							}
						}
					}
				}
			}
		);
	}}

	private boolean isDoor(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof DoorBlock && block != Blocks.IRON_DOOR;
	}

	private boolean isTrapdoor(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof TrapdoorBlock && block != Blocks.IRON_TRAPDOOR;
	}

	private boolean isFenceGate(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof FenceGateBlock;
	}

	private void handleDoor(World world, BlockPos doorPos, BlockPos playerPos) {
		double distance = playerPos.getSquaredDistance(doorPos.getX(), doorPos.getY(), doorPos.getZ());

		boolean isOpen = world.getBlockState(doorPos).get(DoorBlock.OPEN);
		if (distance <= doorDistance + 2 && !isOpen) {
			world.setBlockState(doorPos, world.getBlockState(doorPos).with(DoorBlock.OPEN, true));
		} else if (distance > doorDistance + 2 && isOpen) {
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
