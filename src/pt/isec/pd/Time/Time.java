package pt.isec.pd.Time;

import java.io.Serial;
import java.io.Serializable;

public class Time implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected int hours;
    protected int minutes;
    protected int seconds;

    public Time(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
