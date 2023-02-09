package rtg.world.biome.realistic.biomesoplenty;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

import java.util.Random;


public class RealisticBiomeBOPWhiteBeach extends RealisticBiomeBase {

    public RealisticBiomeBOPWhiteBeach(final Biome biome) {
        super(biome);
    }

    @Override
    public void initDecos() {
    }

    @Override
    public void initConfig() {

    }

    @Override
    public TerrainBase initTerrain() {

        return new TerrainBOPWhiteBeach();
    }

    @Override
    public SurfaceBase initSurface() {
        return new SurfaceBOPWhiteBeach(getConfig(), baseBiome().topBlock, baseBiome().fillerBlock, baseBiome().topBlock, baseBiome().fillerBlock, 1);
    }

    public static class TerrainBOPWhiteBeach extends TerrainBase {

        public TerrainBOPWhiteBeach() {

        }

        @Override
        public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {

            return terrainBeach(x, y, rtgWorld, river, 60f);
        }
    }

    public static class SurfaceBOPWhiteBeach extends SurfaceBase {

        private IBlockState cliffBlock1;
        private IBlockState cliffBlock2;
        private int cliffType;

        public SurfaceBOPWhiteBeach(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState cliff1, IBlockState cliff2, int cliff) {

            super(config, top, filler);

            cliffBlock1 = cliff1;
            cliffBlock2 = cliff2;
            cliffType = cliff;
        }

        @Override
        public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {

            Random rand = rtgWorld.rand();
            SimplexNoise simplex = rtgWorld.simplexInstance(0);
            float c = TerrainBase.calcCliff(x, z, noise);
            boolean cliff = c > 1.3f;
            boolean dirt = false;

            for (int k = 255; k > -1; k--) {
                Block b = primer.getBlockState(x, k, z).getBlock();
                if (b == Blocks.AIR) {
                    depth = -1;
                } else if (b == Blocks.STONE) {
                    depth++;

                    if (cliff) {
                        if (cliffType == 1) {
                            if (depth < 6) {
                                primer.setBlockState(x, k, z, cliffBlock1.getBlock().getDefaultState());
                            }
                        } else {
                            if (depth > -1 && depth < 2) {
                                primer.setBlockState(x, k, z, rand.nextInt(3) == 0 ? cliffBlock2 : cliffBlock1);
                            } else if (depth < 10) {
                                primer.setBlockState(x, k, z, cliffBlock1);
                            }
                        }
                    } else if (depth < 6) {
                        if (depth == 0 && k > 61) {
                            if (simplex.noise2f(i / 12f, j / 12f) > -0.3f + ((k - 61f) / 15f)) {
                                dirt = true;
                                primer.setBlockState(x, k, z, topBlock);
                            } else {
                                primer.setBlockState(x, k, z, Blocks.SAND.getDefaultState());
                            }
                        } else if (depth < 4) {
                            if (dirt) {
                                primer.setBlockState(x, k, z, fillerBlock);
                            } else {
                                primer.setBlockState(x, k, z, Blocks.SAND.getDefaultState());
                            }
                        } else if (!dirt) {
                            primer.setBlockState(x, k, z, Blocks.SANDSTONE.getDefaultState());
                        }
                    }
                }
            }
        }
    }
}
