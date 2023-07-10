public class Audience {
    private Bag bag;

    public Audience(Bag bag) {
        this.bag = bag;
    }

    //관객이 스스로 가방에 디켓이있는지 확인함
    public Long buy(Ticket ticket) {
        return bag.hold(ticket);
    }
}
