import java.time.LocalDate;

public class SurgerySlot extends Reservation
        implements Payable {

    private String urgent;
    private String insuranceCode;
    private String reservationType; 

    public SurgerySlot(BookingID bookingID,
                       String patientID,
                       String patientName,
                       String doctorName,
                       LocalDate appointmentDate,
                       String insuranceCode,
                       String urgent) {

        super(bookingID, patientID,
                patientName, doctorName,
                appointmentDate);

        this.urgent = urgent;
        this.insuranceCode = insuranceCode;
        this.reservationType = reservationType;
    }

    @Override
    public String getConfirmationDetails() {

        return "Surgery Reserved\n"
                + "Booking ID: " + bookingID
                + "\nPatient: " + patientName
                + "\nPatient ID: " + patientID
                + "\nDoctor: " + doctorName
                + "\nDate: " + appointmentDate
                + "\nInsuranceCode: " + insuranceCode
                + "\nUrgent: " + urgent;
    }

    @Override
    public void processPayment() {
        System.out.println("Surgery Payment Processed");
    }
}