package net.wiselywisp.thebeneath.world.gen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;


public final class NaturalBeneathBiomeSource extends BiomeSource {
    public static final MapCodec<NaturalBeneathBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.CODEC.fieldOf("lost").forGetter(source -> source.lost),
                    Biome.CODEC.fieldOf("hills").forGetter(source -> source.hills)
            ).apply(instance, NaturalBeneathBiomeSource::new));

    private final Holder<Biome> lost;
    private final Holder<Biome> hills;

    public NaturalBeneathBiomeSource(Holder<Biome> lost, Holder<Biome> hills) {
        this.lost = lost;
        this.hills = hills;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(lost, hills);
    }

    private static double hash(int x, int z) {
        long n = ((long) x * 341873128712L + (long) z * 132897987541L + 0x6A09E667F3BCC909L);
        n ^= n >>> 30;
        n *= 0xBF58476D1CE4E5B9L;
        n ^= n >>> 27;
        n *= 0x94D049BB133111EBL;
        n ^= n >>> 31;
        return ((n >>> 11) * 0x1.0p-53) * 2.0 - 1.0;
    }

    private static double fade(double x) {
        return x * x * (3.0 - 2.0 * x);
    }

    private static double valueNoise(double x, double z) {
        int x0 = (int) Math.floor(x), z0 = (int) Math.floor(z);
        double fx = fade(x - x0), fz = fade(z - z0);
        double a = hash(x0, z0) + fx * (hash(x0 + 1, z0) - hash(x0, z0));
        double b = hash(x0, z0 + 1) + fx * (hash(x0 + 1, z0 + 1) - hash(x0, z0 + 1));
        return a + fz * (b - a);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {

        double x = quartX / 192.0;
        double z = quartZ / 192.0;
        double value = valueNoise(x, z) * 0.75
                + valueNoise(x * 2.25 + 37.1, z * 2.25 - 18.4) * 0.25;
        return value >= 0.0 ? hills : lost;
    }
}
