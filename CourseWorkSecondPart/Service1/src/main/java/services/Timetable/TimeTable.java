package services.Timetable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.TreeSet;

@JsonAutoDetect
public class TimeTable {
    @JsonDeserialize(as = TreeSet.class)
    public TreeSet<NodeTimeTable> timeTable;

    public TimeTable() {
        timeTable = new TreeSet<NodeTimeTable>();
    }

    public TimeTable(TreeSet<NodeTimeTable> timeTable) {
        this.timeTable = timeTable;
    }

    public TreeSet<NodeTimeTable> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(TreeSet<NodeTimeTable> timeTable) {
        this.timeTable = timeTable;
    }

    @Override
    public String toString() {
        return "TimeTable{" +
                "timeTable=" + timeTable +
                '}';
    }
}
