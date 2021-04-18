package Timetable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;

@JsonAutoDetect
public class DayAndTime implements Comparable<DayAndTime> {
    private int day;
    private int hour;
    private int minute;

    public static final Integer MINUTE_IN_SEVEN_DAYS = 10080;
    public static final Integer MAX_HOUR = 24;
    public static final Integer MAX_MINUTE = 60;
    public static final Integer MAX_DAYS = 30;
    public static final Integer MINUTE_IN_THIRTY_DAYS = 43200;

    public DayAndTime() {
        day = 0;
        hour = 0;
        minute = 0;
    }

    public DayAndTime(int minute) {
        convertDataAndTime(0, 0, minute);
    }

    public DayAndTime(DayAndTime dayAndTime) {
        day = dayAndTime.day;
        hour = dayAndTime.hour;
        minute = dayAndTime.minute;
    }

    public DayAndTime(Integer day, Integer hour, Integer minute) {
        convertDataAndTime(day, hour, minute);
    }

    private void convertDataAndTime(int day, int hour, int minute) {
        int copyDay = day;
        int copyHour = hour;
        int copyMinute = minute;

        int minuteToHour = copyMinute / MAX_MINUTE;
        if (minuteToHour > 0) {
            copyHour += minuteToHour;
            copyMinute %= MAX_MINUTE;
        }

        int hourToDay = copyHour / MAX_HOUR;
        if (hourToDay > 0) {
            copyDay += hourToDay;
            copyHour %= MAX_HOUR;
        }

        minuteToHour = copyMinute / MAX_MINUTE;

        if (minuteToHour < 0) {
            copyHour += minuteToHour;
            copyMinute += MAX_MINUTE * Math.abs(minuteToHour);
            if (copyMinute % MAX_MINUTE != 0) {
                copyHour--;
                copyMinute += MAX_MINUTE;
            }
        }

        hourToDay = copyHour / MAX_HOUR;
        if (hourToDay < 0) {
            copyDay += hourToDay;
            copyHour += MAX_HOUR * Math.abs(hourToDay);
            if (copyHour % MAX_HOUR != 0) {
                copyDay--;
                copyHour += MAX_HOUR;
            }

        }

        if (copyDay < 0 || copyDay > 30)  {
            throw new IllegalArgumentException("There can be no such date and time!");
        }

        this.day = copyDay;
        this.hour = copyHour;
        this.minute = copyMinute;
    }

    public static DayAndTime generateRandomDateAndTime(Integer minDay, Integer maxDay) {
        return new DayAndTime((int) (Math.floor(Math.random() * (maxDay - minDay)) + minDay),
                (int) (Math.floor(Math.random() * MAX_HOUR)),
                (int) (Math.floor(Math.random() * MAX_MINUTE)));
    }

    public static int generateRandomMinute(Integer minMinute, Integer maxMinute) {
        return (int) Math.floor(Math.random() * (maxMinute - minMinute));
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        convertDataAndTime(day, hour, minute);
    }

    public Integer getHour() {
        return hour;
    }

    public DayAndTime addTime(Integer minute) {
        convertDataAndTime(day, hour, this.minute + minute);
        return this;
    }

    public DayAndTime addTime(Integer hour, Integer minute) {
        convertDataAndTime(day, this.hour + hour, this.minute + minute);
        return this;
    }

    public DayAndTime addTime(Integer day, Integer hour, Integer minute) {
        convertDataAndTime(this.day + day, this.hour + hour, this.minute + minute);
        return this;
    }

    public void setHour(Integer hour) {
        convertDataAndTime(day, hour, minute);
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        convertDataAndTime(day, hour, minute);
    }

    public Integer receiveTimeInMinute() {
        return minute + hour * MAX_MINUTE + day * MAX_HOUR * MAX_MINUTE;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        DayAndTime dayAndTime = (DayAndTime) obj;
        return (day == dayAndTime.getDay()) && (hour == dayAndTime.getHour()) && (minute == dayAndTime.getMinute());
    }

    @Override
    public String toString() {
        return String.format("%02d", day) +
                ":" + String.format("%02d", hour) +
                ":" + String.format("%02d", minute);
    }

    @Override
    public int compareTo(@NotNull DayAndTime dayAndTime) {
        Integer copyDay = day;
        Integer copyHour = hour;
        Integer copyMinute = minute;

        int compare;
        if ((compare = copyDay.compareTo(dayAndTime.getDay())) == 0) {
            if ((compare = copyHour.compareTo(dayAndTime.getHour())) == 0) {
                compare = copyMinute.compareTo(dayAndTime.getMinute());
            }
        }

        return compare;
    }
}
