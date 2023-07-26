import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public abstract class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<IDiscountCondition> IDiscountConditions;


    public Movie(String title, Duration runningTime, Money fee, IDiscountCondition... IDiscountConditions) {
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
        this.IDiscountConditions = Arrays.asList(IDiscountConditions);
    }

    public boolean isDiscountable(Screening screening) {
        return IDiscountConditions.stream()
            .anyMatch(condition -> condition.isSatisfiedBy(screening));
    }

    public Money getFee() {
        return fee;
    }


    public Money calculateMovieFee(Screening screening) {
        if (isDiscountable(screening)) {
            return fee.minus(calculateDiscountAmount());
        }

        return fee;
    }

    abstract protected Money calculateDiscountAmount();
}
