import java.time.Duration;
import java.time.LocalDateTime;

public class Event {
    private String subject;
    private LocalDateTime from;
    private Duration duration;

    public Event(String subject, LocalDateTime from, Duration duration) {
        this.subject = subject;
        this.from = from;
        this.duration = duration;
    }

    private long dayDistance(LocalDateTime from, LocalDateTime to) {
        return Duration.between(from.toLocalDate().atStartOfDay(), to.toLocalDate().atStartOfDay()).toDays();
    }
}
