package michael.openplease;

import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

public class DetectDistance {
    public static double getDistanceToEntity(PlayerEntity playerEntity, BlockPos pos) {
        double deltaX = playerEntity.getX() - pos.getX();
        double deltaY = playerEntity.getY() - pos.getY();
        double deltaZ = playerEntity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }


    public static List<BlockPos> findAllDoors(World world) {
        List<BlockPos> doorPositions = new ArrayList<>();

        if (world == null) {
            return doorPositions;
        }

        // Iterate over all players to find loaded chunks around them
        world.getPlayers().forEach(player -> {
            ChunkPos playerChunkPos = new ChunkPos(player.getBlockPos());

            // Scan chunks in a radius around the player (e.g., 2 chunks)
            int radius = 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    ChunkPos chunkPos = new ChunkPos(playerChunkPos.x + dx, playerChunkPos.z + dz);

                    // Get the chunk if it's loaded
                    Chunk chunk = (Chunk) world.getChunkManager().getChunk(chunkPos.x, chunkPos.z);
                    if (chunk != null) {
                        // Scan all blocks in the chunk
                        int startX = chunkPos.getStartX();
                        int endX = chunkPos.getEndX();
                        int startZ = chunkPos.getStartZ();
                        int endZ = chunkPos.getEndZ();

                        for (int x = startX; x <= endX; x++) {
                            for (int y = world.getBottomY(); y < world.getTopYInclusive(); y++) {
                                for (int z = startZ; z <= endZ; z++) {
                                    BlockPos pos = new BlockPos(x, y, z);
                                    if (world.getBlockState(pos).getBlock() instanceof DoorBlock) {
                                        doorPositions.add(pos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        return doorPositions;
    }
}