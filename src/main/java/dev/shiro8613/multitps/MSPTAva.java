package dev.shiro8613.multitps;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class MSPTAva {

    private final Queue<BigDecimal> samples;
    private final int windowSize;
    private BigDecimal total = BigDecimal.ZERO;

    public MSPTAva(int windowSize) {
        this.windowSize = windowSize;
        this.samples = new ArrayDeque<>(this.windowSize + 1);
    }

    public int getSamples() {
        synchronized (this) {
            return this.samples.size();
        }
    }

    public double max() {
        synchronized (this) {
            BigDecimal max = null;
            for (BigDecimal sample : this.samples) {
                if (max == null || sample.compareTo(max) > 0) {
                    max = sample;
                }
            }
            return max == null ? 0 : max.doubleValue();
        }
    }

    public double min() {
        synchronized (this) {
            BigDecimal min = null;
            for (BigDecimal sample : this.samples) {
                if (min == null || sample.compareTo(min) < 0) {
                    min = sample;
                }
            }
            return min == null ? 0 : min.doubleValue();
        }
    }

    public double percentile(double percentile) {
        if (percentile < 0 || percentile > 1) {
            throw new IllegalArgumentException("Invalid percentile " + percentile);
        }

        BigDecimal[] sortedSamples;
        synchronized (this) {
            if (this.samples.isEmpty()) {
                return 0;
            }
            sortedSamples = this.samples.toArray(new BigDecimal[0]);
        }
        Arrays.sort(sortedSamples);

        int rank = (int) Math.ceil(percentile * (sortedSamples.length - 1));
        return sortedSamples[rank].doubleValue();
    }

}
