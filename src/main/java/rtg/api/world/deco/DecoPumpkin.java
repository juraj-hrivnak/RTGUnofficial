package rtg.api.world.deco;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.TerrainGen;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;

import java.util.Random;


/**
 * @author WhichOnesPink
 */
public class DecoPumpkin extends DecoBase {

    private float strengthFactor;
    private int maxY;
    private float randomFloat;
    @Deprecated
    private RandomType randomType;
    private int chance;
    private int loops;

    public DecoPumpkin() {

        super();

        /**
         * Default values.
         * These can be overridden when configuring the Deco object in the realistic biome.
         */
        this.setMaxY(255); // No height limit by default.
        this.setStrengthFactor(0f); // The higher the value, the more there will be. Disabled by default.
        this.setRandomType(RandomType.USE_CHANCE_VALUE);
        this.setRandomFloat(1f);
        this.setChance(1);
        this.setLoops(1);

        this.addDecoTypes(DecoType.PUMPKIN);
    }

    @Override
    public void generate(final IRealisticBiome biome, final RTGWorld rtgWorld, final Random rand, final ChunkPos chunkPos, final float river, final boolean hasVillage) {

        if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, Decorate.EventType.PUMPKIN)) {

            // Let's figure out what the rand.nextInt() argument should be.
            switch (this.randomType) {
                case ALWAYS_GENERATE:
                    this.setChance(1);
                    break;

                case USE_CHANCE_VALUE:
                    break;

                default:
                    break;
            }

            final int loopCount = (this.strengthFactor > 0f) ? (int) this.strengthFactor : this.loops;
            for (int i = 0; i < loopCount; i++) {
                if (rand.nextInt(this.chance) == 0) {
                    final BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), rand.nextInt(this.maxY), rand.nextInt(16));
                    new WorldGenPumpkin()
                            .generate(rtgWorld.world(), rand, pos);
                }
            }
        }
    }

    public float getStrengthFactor() {

        return strengthFactor;
    }

    public DecoPumpkin setStrengthFactor(float strengthFactor) {

        this.strengthFactor = strengthFactor;
        return this;
    }

    public int getMaxY() {

        return maxY;
    }

    public DecoPumpkin setMaxY(int maxY) {

        this.maxY = maxY;
        return this;
    }

    public float getRandomFloat() {

        return randomFloat;
    }

    public DecoPumpkin setRandomFloat(float randomFloat) {

        this.randomFloat = randomFloat;
        return this;
    }

    public RandomType getRandomType() {

        return randomType;
    }

    public DecoPumpkin setRandomType(RandomType randomType) {

        this.randomType = randomType;
        return this;
    }

    public int getChance() {

        return chance;
    }

    public DecoPumpkin setChance(int chance) {

        this.chance = chance;
        return this;
    }

    public int getLoops() {

        return loops;
    }

    public DecoPumpkin setLoops(int loops) {

        this.loops = loops;
        return this;
    }

    // TODO: [1.12] Overcomplication for a simple surface feature.
    @Deprecated
    public enum RandomType {
        ALWAYS_GENERATE,
        USE_CHANCE_VALUE
    }
}
