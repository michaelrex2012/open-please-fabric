package michael.openplease.mixin;

import michael.openplease.DetectDistance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DoorBlock.class)
public class AutoOpenDoorMixin {

	@Inject(method = "neighborUpdate", at = @At("HEAD"))
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		// Only do this on the server side
		if (!world.isClient()) {
			ServerWorld serverWorld = (ServerWorld) world;
			List<ServerPlayerEntity> players = serverWorld.getPlayers(); // Get the server players

			// Loop through players to check distance
			for (ServerPlayerEntity player : players) {
				double distance = DetectDistance.getDistanceToEntity(player, pos);
				System.out.println("Distance to door: " + distance); // Debugging the distance

				if (distance < 1.5) {
					// Toggle the door state
					boolean isOpen = state.get(DoorBlock.OPEN);
					BlockState newState = state.with(DoorBlock.OPEN, !isOpen);

					// Log door state change
					System.out.println("Door state changed: " + (isOpen ? "Closing" : "Opening"));

					// Set the new block state on the server and ensure it updates properly
					serverWorld.setBlockState(pos, newState, 3); // Flag 3 to propagate block updates to clients
				}
			}
		}
	}
}
