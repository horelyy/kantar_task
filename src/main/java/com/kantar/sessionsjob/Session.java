package com.kantar.sessionsjob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Session {
    public final static int SECOND_MS = 1000;

    private final int homeNr;
    private final int channel;
    private Date startTime;
    private final String activity;
    private Date endTime;
    private long duration;

    public Session(String[] inputSession) throws Exception {
        if (inputSession.length != 4) {
            throw new Exception("Invalid count of input params");
        } else {
            this.homeNr = Integer.parseInt(inputSession[0]);
            this.channel = Integer.parseInt(inputSession[1]);
            parseDate(inputSession[2]);
            this.activity = inputSession[3];
            this.endTime = new Date();
        }
    }

    @Override
    public String toString() {
        return homeNr + "|" + channel + "|" + new SimpleDateFormat("yyyyMMddHHmmss").format(startTime) + "|" +
                activity + "|" + new SimpleDateFormat("yyyyMMddHHmmss").format(endTime) + "|" + duration;
    }

    /**
     * Converts string date representation in Date instance.
     * @param date String date
     */
    private void parseDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            startTime = df.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date");
        }
    }

    /**
     * Calculate end time of session based on start time of next session.
     * @param nextSession next session instance
     */
    public void calculateEndTime(Session nextSession) {
        // end time setting
        Date nextDate = nextSession.getStartTime();
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(nextDate.getTime());
        // Subtract 1 second from start date of next session
        endTime.add(Calendar.SECOND, -1);
        setEndTime(endTime.getTime());
    }

    /**
     * Calculates session duration in seconds.
     * @return duration in seconds
     */
    public void calculateDuration() {
        long duration = Math.abs(endTime.getTime() - startTime.getTime()) + SECOND_MS;
        setDuration(duration / SECOND_MS);
    }

    /**
     * Set session end time to 23:59:59.
     */
    public void setEndTimeToMax() {
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(startTime.getTime());
        // set time to the end of day
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        setEndTime(endTime.getTime());
    }

    public int getHomeNr() {
        return homeNr;
    }

    public int getChannel() {
        return channel;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getActivity() {
        return activity;
    }

    public Date getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
