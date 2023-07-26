public class SequenceConditionI implements IDiscountCondition {
    private int sequence;

    public SequenceConditionI(int sequence) {
        this.sequence = sequence;
    }

    public boolean isSatisfiedBy(Screening screening) {
        return screening.inSequence(sequence);
    }
}
