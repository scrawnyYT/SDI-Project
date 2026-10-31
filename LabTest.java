import java.time.LocalDate;

public class LabTest extends Reservation
        implements Payable {

    private String testType;
    private String insuranceCode;
    private String reservationType;

    public LabTest(BookingID bookingID,
                       String patientID,
                       String patientName,
                       String doctorName,
                       LocalDate appointmentDate,
                       String insuranceCode,
                       String reservationType,
                       String testType) { 

        super(bookingID, patientID,
                patientName, doctorName,
                appointmentDate);
                
        this.testType = testType;
        this.insuranceCode = insuranceCode;
        this.reservationType = reservationType;
    }

    @Override
    public String getConfirmationDetails() {

        return "Lab Test Booked\n"
                + "Booking ID: " + bookingID
                + "\nPatient: " + patientName
                + "\nPatient ID: " + patientID
                + "\nDoctor: " + doctorName
                + "\nDate: " + appointmentDate
                + "\nInsuranceCode: " + insuranceCode
                + "\nTest Type: " + testType;
    }

    @Override
    public void processPayment() {
        System.out.println("Lab Test Payment Processed");
    }
}