package Timetable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class PerformanceCranes {
    private int performanceLoose;
    private int performanceLiquid;
    private int performanceContainer;

    public int getPerformanceLoose() {
        return performanceLoose;
    }

    public void setPerformanceLoose(int performanceLoose) {
        this.performanceLoose = performanceLoose;
    }

    public int getPerformanceLiquid() {
        return performanceLiquid;
    }

    public void setPerformanceLiquid(int performanceLiquid) {
        this.performanceLiquid = performanceLiquid;
    }

    public int getPerformanceContainer() {
        return performanceContainer;
    }

    public void setPerformanceContainer(int performanceContainer) {
        this.performanceContainer = performanceContainer;
    }

    public PerformanceCranes() {
    }

    public PerformanceCranes(int performanceLoose, int performanceLiquid, int performanceContainer) {
        this.performanceLoose = performanceLoose;
        this.performanceLiquid = performanceLiquid;
        this.performanceContainer = performanceContainer;
    }

    @Override
    public String toString() {
        return "PerformanceCranes{" +
                "performanceLoose=" + performanceLoose +
                ", performanceLiquid=" + performanceLiquid +
                ", performanceContainer=" + performanceContainer +
                '}';
    }
}
