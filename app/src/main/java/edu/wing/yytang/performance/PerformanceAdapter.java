package edu.wing.yytang.performance;

/**
 * Created by yytang on 3/30/16.
 */
public class PerformanceAdapter {
    private SpanPerformanceData spanPerformanceData;
    private PointPerformanceData pointPerformanceData;

    // setter, used by an object to point to correct performance data objects
//    public void setPerformanceData(PerformanceTimer performance) {
//        this.spanPerformanceData = performance.getSpanPerformanceData();
//        this.pointPerformanceData = performance.getPointPerformanceData();
//    }

    // setter, used by an object to clean up when it's done recording performance data
    public void clearPerformanceData() {
        this.spanPerformanceData = null;
        this.pointPerformanceData = null;
    }

    // used by VideoStreamsView to record frame count
    public void incrementFrameCount() {
        if (spanPerformanceData != null)
            spanPerformanceData.incrementFrameCount();
    }

    // used by TouchHandler to record touch updates
    public void incrementTouchUpdates() {
        if (spanPerformanceData != null)
            spanPerformanceData.incrementTouchUpdates();
    }

    // used by SensorHandler to record sensor updates
    public void incrementSensorUpdates() {
        if (spanPerformanceData != null)
            spanPerformanceData.incrementSensorUpdates();
    }

    // used by MessageHandler to record ping
    public void setPing(long startDate, long endDate) {
        if (pointPerformanceData != null)
            pointPerformanceData.setPing(startDate, endDate);
    }
}
