package rtg.api.world.deco;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import rtg.api.util.BlockUtil;
import rtg.api.util.BlockUtil.MatchType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenLog;


/**
 * @author WhichOnesPink
 */
public class DecoFallenTree extends DecoBase {

    private int loops;
    private DecoFallenTree.Distribution distribution; // Parameter object for noise calculations.
    private LogCondition logCondition; // Enum for the various conditions/chances for log gen.
    private float logConditionNoise; // Only applies to a noise-related LogCondition.
    private int logConditionChance; // Only applies to a chance-related LogCondition.
    private int maxY; // Height restriction.
    private IBlockState logBlock;
    private IBlockState leavesBlock;
    private int minSize; // Min log height (only used with certain log presets)
    private int maxSize; // Max log height (only used with certain log presets)
    private IBlockState[] randomLogBlocks;

    public DecoFallenTree() {

        super();

        /**
         * Default values.
         * These can be overridden when configuring the Deco object in the realistic biome.
         */
        this.setLoops(1);
        this.setDistribution(new DecoFallenTree.Distribution(100f, 5f, 0.8f));
        this.setLogCondition(LogCondition.NOISE_GREATER_AND_RANDOM_CHANCE);
        this.setLogConditionNoise(0f);
        this.setLogConditionChance(1);
        this.setMaxY(80);
        this.setLogBlock(Blocks.LOG.getDefaultState());
        this.setLeavesBlock(Blocks.LEAVES.getDefaultState());
        this.setMinSize(2);
        this.setMaxSize(4);
        this.randomLogBlocks = new IBlockState[]{};

        this.addDecoTypes(DecoType.FALLEN_TREE);
    }

    // TODO: [1.12] Remove this unused constructor.
    public DecoFallenTree(DecoFallenTree source) {

        this();
        this.setLoops(source.loops);
        this.setDistribution(source.distribution);
        this.setLogCondition(source.logCondition);
        this.setLogConditionNoise(source.logConditionNoise);
        this.setLogConditionChance(source.logConditionChance);
        this.setMaxY(source.maxY);
        this.setLogBlock(source.logBlock);
        this.setLeavesBlock(source.leavesBlock);
        this.setMinSize(source.minSize);
        this.setMaxSize(source.maxSize);
        this.randomLogBlocks = source.randomLogBlocks;
    }

    @Override
    public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, int worldX, int worldZ, float strength, float river, boolean hasPlacedVillageBlocks) {

        if (this.allowed) {

            float noise = rtgWorld.simplexInstance(0).noise2f(worldX / this.distribution.getNoiseDivisor(), worldZ / this.distribution.getNoiseDivisor());
            noise *= this.distribution.getNoiseFactor() + this.distribution.getNoiseAddend();

            //Do we want to choose a random log?
            if (this.randomLogBlocks.length > 0) {
                this.setLogBlock(this.randomLogBlocks[rand.nextInt(this.randomLogBlocks.length)]);
            }

            WorldGenerator worldGenerator = null;
            int finalSize = 4;

            // Adjust the chance according to biome config.
            this.setLogConditionChance(this.adjustChanceFromMultiplier(this.getLogConditionChance(), biome.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER.get()));

            if (this.maxSize > this.minSize) {
                finalSize = this.minSize + rand.nextInt(this.maxSize - this.minSize);
                worldGenerator = new WorldGenLog(this.logBlock, this.leavesBlock, finalSize);
            }
            else if (this.maxSize == this.minSize) {
                finalSize = this.minSize;
                worldGenerator = new WorldGenLog(this.logBlock, this.leavesBlock, finalSize);
            }
            else {
                worldGenerator = new WorldGenLog(this.logBlock, this.leavesBlock, finalSize);
            }

            final World world = rtgWorld.world();
            MutableBlockPos mpos = new MutableBlockPos();
            for (int i = 0; i < this.loops; i++) {
                if (isValidLogCondition(noise, strength, rand)) {
                    int x = worldX + rand.nextInt(16) + 8;
                    int z = worldZ + rand.nextInt(16) + 8;
                    int y = rtgWorld.world().getHeight(mpos.setPos(x, 0, z)).getY();
                    mpos.setPos(x, y, z);

                    if (y <= this.maxY) {

                        // If we're in a village, check to make sure the log has extra room to grow to avoid corrupting the village.
                        if (hasPlacedVillageBlocks) {
// TODO: [1.12] May need to add a check to prevent fallen logs from generating over village farmland. [LOLOL IT'S A FEATURE NOT A BUG!]
                            if (!BlockUtil.checkAreaBlocks(MatchType.ALL_IGNORE_REPLACEABLE, world, mpos, finalSize)) {
                                return;
                            }
                        }
                        worldGenerator.generate(world, rand, mpos);
                    }
                }
            }
        }
    }

    public boolean isValidLogCondition(float noise, float strength, Random rand) {

        switch (this.logCondition) {
            case ALWAYS_GENERATE:

                return true;

            case RANDOM_CHANCE:

                return (rand.nextInt(this.logConditionChance) == 0);

            case NOISE_GREATER_AND_RANDOM_CHANCE:

                return (noise > this.logConditionNoise && rand.nextInt(this.logConditionChance) == 0);

            case NOISE_LESS_AND_RANDOM_CHANCE:

                return (noise < this.logConditionNoise && rand.nextInt(this.logConditionChance) == 0);

            case X_DIVIDED_BY_STRENGTH:

                return (rand.nextInt((int) (this.logConditionNoise / strength)) == 0);

            default:
                return false;
        }
    }

    public int getLoops() {

        return loops;
    }

    public DecoFallenTree setLoops(int loops) {

        this.loops = loops;
        return this;
    }

    public Distribution getDistribution() {

        return distribution;
    }

    public DecoFallenTree setDistribution(Distribution distribution) {

        this.distribution = distribution;
        return this;
    }

    public LogCondition getLogCondition() {

        return logCondition;
    }

    public DecoFallenTree setLogCondition(LogCondition logCondition) {

        this.logCondition = logCondition;
        return this;
    }

    public float getLogConditionNoise() {

        return logConditionNoise;
    }

    public DecoFallenTree setLogConditionNoise(float logConditionNoise) {

        this.logConditionNoise = logConditionNoise;
        return this;
    }

    public int getLogConditionChance() {

        return logConditionChance;
    }

    public DecoFallenTree setLogConditionChance(int logConditionChance) {

        this.logConditionChance = logConditionChance;
        return this;
    }

    public int getMaxY() {

        return maxY;
    }

    public DecoFallenTree setMaxY(int maxY) {

        this.maxY = maxY;
        return this;
    }

    public IBlockState getLogBlock() {

        return logBlock;
    }

    public DecoFallenTree setLogBlock(IBlockState logBlock) {

        this.logBlock = logBlock;
        return this;
    }

    public IBlockState getLeavesBlock() {

        return leavesBlock;
    }

    public DecoFallenTree setLeavesBlock(IBlockState leavesBlock) {

        this.leavesBlock = leavesBlock;
        return this;
    }

    public int getMinSize() {

        return minSize;
    }

    public DecoFallenTree setMinSize(int minSize) {

        this.minSize = minSize;
        return this;
    }

    public int getMaxSize() {

        return maxSize;
    }

    public DecoFallenTree setMaxSize(int maxSize) {

        this.maxSize = maxSize;
        return this;
    }

    public IBlockState[] getRandomLogBlocks() {

        return randomLogBlocks;
    }

    public DecoFallenTree setRandomLogBlocks(IBlockState[] randomLogBlocks) {

        this.randomLogBlocks = randomLogBlocks;
        return this;
    }

    public enum LogCondition {
        ALWAYS_GENERATE,
        RANDOM_CHANCE,
        NOISE_GREATER_AND_RANDOM_CHANCE,
        NOISE_LESS_AND_RANDOM_CHANCE,
        X_DIVIDED_BY_STRENGTH;
    }

    /**
     * Parameter object for noise calculations.
     * <p>
     * simplex.noise2(chunkX / noiseDivisor, chunkY / noiseDivisor) * noiseFactor + noiseAddend;
     *
     * @author WhichOnesPink
     * @author Zeno410
     */
    public static class Distribution {

        private float noiseDivisor;
        private float noiseFactor;
        private float noiseAddend;

        public Distribution(float noiseDivisor, float noiseFactor, float noiseAddend) {

            this.noiseDivisor = noiseDivisor;
            this.noiseFactor = noiseFactor;
            this.noiseAddend = noiseAddend;
        }

        public float getNoiseDivisor() {

            return noiseDivisor;
        }

        public Distribution setNoiseDivisor(float noiseDivisor) {

            this.noiseDivisor = noiseDivisor;
            return this;
        }

        public float getNoiseFactor() {

            return noiseFactor;
        }

        public Distribution setNoiseFactor(float noiseFactor) {

            this.noiseFactor = noiseFactor;
            return this;
        }

        public float getNoiseAddend() {

            return noiseAddend;
        }

        public Distribution setNoiseAddend(float noiseAddend) {

            this.noiseAddend = noiseAddend;
            return this;
        }
    }

    private int adjustChanceFromMultiplier(int chanceIn, float multiplier) {
        int chanceOut = (multiplier != 0f) ? ((int) Math.floor(chanceIn / multiplier)) : chanceIn;
        return (chanceOut == 0) ? 1 : chanceOut;
    }
}
