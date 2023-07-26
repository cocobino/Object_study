public class AmountDiscountPolicy extends DefaultDiscountPolicy{
    private Money discountAmount;

    public AmountDiscountPolicy(Money discountAmount, IDiscountCondition... conditions) {
        super(conditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return discountAmount;
    }
}
