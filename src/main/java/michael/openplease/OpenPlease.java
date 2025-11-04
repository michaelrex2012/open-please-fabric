package michael.openplease;

import me.shedaniel.clothconfig2.api.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

public class OpenPlease implements ModInitializer {
	public float doorDistance = 4;
	public static KeyBinding openToggle;
	public static KeyBinding soundToggle;
	public static KeyBinding getStates;

	@Override
	public void onInitialize() {
		ModConfig.load();

		ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
	}

	private void onWorldTick(ServerWorld world) {
        world.getPlayers().forEach(player -> {
                    BlockPos playerPos = player.getBlockPos();

                    for (int x = -4; x <= 4; x++) {
                        for (int y = -4; y <= 4; y++) {
                            for (int z = -4; z <= 4; z++) {
                                BlockPos pos = playerPos.add(x, y, z);

                                if (isDoor(world, pos) && ModConfig.DoorAutoOpen) {
                                    boolean oldStateDoor = world.getBlockState(pos).get(DoorBlock.OPEN);
                                    handleDoor(world, pos, playerPos);
                                    if (oldStateDoor && !world.getBlockState(pos).get(DoorBlock.OPEN) && ModConfig.ToggleSound) {
                                        world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                    }
                                    if (!oldStateDoor && world.getBlockState(pos).get(DoorBlock.OPEN) && ModConfig.ToggleSound) {
                                        world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                    }
                                }
                                if (isTrapdoor(world, pos) && ModConfig.TrapdoorAutoOpen) {
                                    boolean oldStateTrapDoor = world.getBlockState(pos).get(TrapdoorBlock.OPEN);
                                    handleTrapdoor(world, pos, playerPos);
                                    if (oldStateTrapDoor && !world.getBlockState(pos).get(TrapdoorBlock.OPEN) && ModConfig.ToggleSound) {
                                        world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                    }
                                    if (!oldStateTrapDoor && world.getBlockState(pos).get(TrapdoorBlock.OPEN) && ModConfig.ToggleSound) {
                                        world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                    }
                                }
                                if (isFenceGate(world, pos) && ModConfig.GateAutoOpen) {
                                    boolean oldStateFenceGate = world.getBlockState(pos).get(FenceGateBlock.OPEN);
                                    handleFenceGate(world, pos, playerPos);
                                    if (oldStateFenceGate && !world.getBlockState(pos).get(FenceGateBlock.OPEN) && ModConfig.ToggleSound) {
                                        world.playSound(null, pos, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                    }
                                    if (!oldStateFenceGate && world.getBlockState(pos).get(FenceGateBlock.OPEN) && ModConfig.ToggleSound) {
                                        world.playSound(null, pos, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

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
		double distance = Math.sqrt((playerPos.getX() - doorPos.getX()) + (playerPos.getY() - doorPos.getY()) + (playerPos.getZ() - doorPos.getZ()));

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
	public static Screen openConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.translatable("title.openplease.config"));

		ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.openplease.general"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		general.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("option.openplease.door_toggle"), ModConfig.DoorAutoOpen)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("Defines if doors auto-open"))
				.setSaveConsumer(newValue -> ModConfig.DoorAutoOpen = newValue)
				.build()
		);

		general.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("option.openplease.trapdoor_toggle"), ModConfig.TrapdoorAutoOpen)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("Defines if trapdoors auto-open"))
				.setSaveConsumer(newValue -> ModConfig.TrapdoorAutoOpen = newValue)
				.build()
		);

		general.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("option.openplease.gate_toggle"), ModConfig.GateAutoOpen)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("Defines if gates auto-open"))
				.setSaveConsumer(newValue -> ModConfig.GateAutoOpen = newValue)
				.build()
		);

		general.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("option.openplease.toggle_sound"), ModConfig.ToggleSound)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("Defines auto-open sound plays"))
				.setSaveConsumer(newValue -> ModConfig.ToggleSound = newValue)
				.build()
		);

		general.addEntry(entryBuilder
				.startTextDescription(Text.translatable("option.openplease.info").formatted(Formatting.ITALIC))
				.setColor(Color.GRAY.getRGB())
				.build()
		);

		builder.setSavingRunnable(ModConfig::save);

		MinecraftClient.getInstance().setScreen(builder.build());
		return builder.build();
	}
}
