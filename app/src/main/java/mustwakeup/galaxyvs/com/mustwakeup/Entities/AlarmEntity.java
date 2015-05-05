package mustwakeup.galaxyvs.com.mustwakeup.Entities;

import java.io.Serializable;

/**
 * Created by viral.thakkar on 04-05-2015.
 */
public class AlarmEntity implements Serializable{
    private int id;
    private String name;
    private int activityType;
    private String tone;
    private long time;
    private int isActive;
    private String weekDays;

    public AlarmEntity() {
    }

    public AlarmEntity(String name, int isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }
}
