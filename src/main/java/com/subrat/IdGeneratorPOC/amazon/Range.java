package com.subrat.IdGeneratorPOC.amazon;

public class Range {
    private final Integer startValue;
    private final Integer endValue;

    private final String serviceName;

    public Range(Integer startValue, Integer endValue, String serviceName) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.serviceName = serviceName;
    }

    public Integer getStartValue() {
        return startValue;
    }

    public Integer getEndValue() {
        return endValue;
    }

    public String getServiceName() {
        return serviceName;
    }
}