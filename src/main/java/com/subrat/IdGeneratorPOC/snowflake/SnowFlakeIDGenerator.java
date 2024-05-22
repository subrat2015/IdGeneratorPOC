package com.subrat.IdGeneratorPOC.snowflake;

public class SnowFlakeIDGenerator {

    private final Long machineId;
    private long count;

    public SnowFlakeIDGenerator(Long machineId) {
        this.machineId = machineId;
        count = 0;
    }

    public long getId() {
        long ans = 0;
        long epochTimeStamp = getEpochTimeStamp();
        // set first 41 bits
        epochTimeStamp = epochTimeStamp << 23;
        epochTimeStamp = epochTimeStamp & 0x7FFFFFFFFFL; // Ensure sign bit is 0

        // set next 10 bits
        long machineID = machineId << 13;

        long sequenceNumber = getSequenceCount();

        ans = epochTimeStamp | machineID | sequenceNumber;

        return ans;
    }

    /*
        We are considering epoch timestamp since 1 jan 2024
     */
    private long getEpochTimeStamp() {
        long currentTimeMillis = System.currentTimeMillis();
        long millisecondsIn2024 = 1735689600000L; // Number of milliseconds between 1970 and 2024
        long adjustedTimeMillis = currentTimeMillis - millisecondsIn2024;
        return adjustedTimeMillis;
    }

    private synchronized long getSequenceCount() {
        count = count + 1;
        return count;
    }


}
