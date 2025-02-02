package rtg.world.biome.realistic.zoesteria;

import net.minecraft.world.biome.Biome;
import rtg.api.world.RTGWorld;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.BumpyHillsEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;


public class RealisticBiomeZOEHighWoodlands extends RealisticBiomeZOEBase {

    public RealisticBiomeZOEHighWoodlands(Biome biome) {
        super(biome);
    }

    @Override
    public void initConfig() {

    }

    @Override
    public TerrainBase initTerrain() {

        return new TerrainBOPHighland();
    }

    public static class TerrainBOPHighland extends TerrainBase {

        private final float baseHeight = 90f;
        private final BumpyHillsEffect onTop = new BumpyHillsEffect();
        private final JitterEffect withJitter;

        public TerrainBOPHighland() {

            onTop.hillHeight = 30;
            onTop.hillWavelength = 60;
            onTop.spikeHeight = 20;
            onTop.spikeWavelength = 10;

            withJitter = new JitterEffect();
            withJitter.amplitude = 2;
            withJitter.wavelength = 5;
            withJitter.jittered = onTop;
        }

        @Override
        public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {

            return riverized(baseHeight + withJitter.added(rtgWorld, x, y) + groundNoise(x, y, 6, rtgWorld), river);
            //return terrainGrasslandMountains(x, y, simplex, cell, river, 4f, 80f, 68f);
        }
    }
}
