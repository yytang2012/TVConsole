package edu.wing.yytang.performance;

/**
 * Created by yytang on 3/29/16.
 */
public class SpanPerformanceData {
    private int frameCount;
    private int sensorUpdates;
    private int touchUpdates;

    public SpanPerformanceData() {}

    public synchronized SpanPerformanceData reset() {
        // create a copy of the measurements taken
        SpanPerformanceData copy = new SpanPerformanceData();
        copy.frameCount = frameCount;
        copy.sensorUpdates = sensorUpdates;
        copy.touchUpdates = touchUpdates;

        // reset measurements
        frameCount = 0;
        sensorUpdates = 0;
        touchUpdates = 0;

        return copy;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getSensorUpdates() {
        return sensorUpdates;
    }

    public int getTouchUpdates() {
        return touchUpdates;
    }

    public synchronized void incrementFrameCount() {
        this.frameCount++;
    }
    public synchronized void incrementSensorUpdates() {
        this.sensorUpdates++;
    }
    public synchronized void incrementTouchUpdates() {
        this.touchUpdates++;
    }

    public String toString() {
        return String.format("frameCount '%d', sensorUpdates '%d', touchUpdates '%d'",
                frameCount, sensorUpdates, touchUpdates);
    }
}
