import java.time.LocalDate;

public class DoctorVisit extends Reservation implements Payable {

    private String insuranceCode;
    private String reservationType; // 1. Added a field to store the type

    public DoctorVisit(BookingID bookingID,
                       String patientID,
                       String patientName,
                       String doctorName,
                       LocalDate appointmentDate,
                       String insuranceCode,
                       String reservationType) { // 2. Added to constructor parameters

        super(bookingID, patientID,
                patientName, doctorName,
                appointmentDate);

        this.insuranceCode = insuranceCode;
        this.reservationType = reservationType; // 3. Assigned the type


    }

    @Override
    public String getConfirmationDetails() {
        // 4. Using the dynamic variable here instead of the hardcoded text
        return reservationType + " Confirmed\n"
                + "Booking ID: " + bookingID
                + "\nPatient: " + patientName
                + "\nPatient ID: " + patientID
                + "\nDoctor: " + doctorName
                + "\nDate: " + appointmentDate
                + "\nInsuranceCode: " + insuranceCode;
    }

    @Override
    public void processPayment() {
        System.out.println("Doctor Visit Payment Processed");
    }
}
