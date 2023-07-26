import java.time.DayOfWeek;
import java.time.LocalTime;

public class PeriodConditionI implements IDiscountCondition {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public PeriodConditionI(DayOfWeek dayOfweek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfweek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isSatisfiedBy(Screening screening) {
        return screening.getStartTime().getDayOfWeek().equals(dayOfWeek) &&
            startTime.compareTo(screening.getStartTime().toLocalTime()) <= 0 &&
            endTime.compareTo(screening.getStartTime().toLocalTime()) >= 0;
    }

}
