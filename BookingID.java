public final class BookingID {

    private final String bookingCode;

    public BookingID(String bookingCode) {

        if (!bookingCode.matches("HSP-\\d{4}-\\d{3}")) {
            throw new IllegalArgumentException(
                    "Invalid Booking ID Format");
        }

        this.bookingCode = bookingCode;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    @Override
    public String toString() {
        return bookingCode;
    }
}