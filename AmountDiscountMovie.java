import java.time.Duration;

public class AmountDiscountMovie extends Movie{
    private Money discountAmount;

    public AmountDiscountMovie(String title, Duration runningTime, Money fee, Money discountAmount, IDiscountCondition... IDiscountConditions){
        super(title, runningTime, fee, IDiscountConditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money calculateDiscountAmount() {
        return discountAmount;
    }
}
