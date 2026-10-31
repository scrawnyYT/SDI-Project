public interface Payable {

    void processPayment();

    default void applyDiscount() {
        System.out.println("Discount Applied.");
    }
}