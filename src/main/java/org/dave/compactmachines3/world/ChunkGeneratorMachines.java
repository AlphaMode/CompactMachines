package org.dave.compactmachines3.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ChunkGeneratorMachines implements IChunkGenerator {
    private final World world;
    private final byte[] voidBiomeArray;

    public ChunkGeneratorMachines(World worldIn) {
        this.world = worldIn;
        voidBiomeArray = new byte[256];
        Arrays.fill(voidBiomeArray, (byte) Biome.getIdForBiome(Biomes.VOID));
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        ChunkPrimer cp = new ChunkPrimer();
        Chunk chunk = new NoPopulateChunk(this.world, cp, x, z);
        chunk.generateSkylightMap();
        chunk.setBiomeArray(voidBiomeArray);
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return world.getBiome(pos).getSpawnableList(creatureType);
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
