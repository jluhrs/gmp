package edu.gemini.aspen.gmp.status.simulator.simulators;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Simulator of status values following an asymptotic curve plus noise
 */
public class AsymptoticWithNoiseStatusSimulatorSimulator extends BaseStatusSimulator<Double> {
    private final long startTime = System.currentTimeMillis();
    private final double min;
    private final double max;
    private final double period;
    private final double noiseAmplitude;
    private final Random rnd = new SecureRandom();

    public AsymptoticWithNoiseStatusSimulatorSimulator(String name, int updateRate, double min, double max, double period, double noiseAmplitude) {
        super(name, updateRate);
        this.min = min;
        this.max = max;
        this.period = period;
        this.noiseAmplitude = noiseAmplitude;
    }

    @Override
    Double generateValue() {
        double time = (System.currentTimeMillis() - startTime)/period;
        double baseFactor = 1/(Math.exp(-time) + 1)-0.5;
        double scaledValue = min + 2*(max-min)*baseFactor;
        double valueAndNoise = scaledValue + noiseAmplitude *(rnd.nextDouble()-0.5);
        return Math.max(min, valueAndNoise);
    }
}
