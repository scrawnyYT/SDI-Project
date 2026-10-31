import java.time.LocalDate;

public abstract class Reservation {

    protected BookingID bookingID;
    protected String patientID;
    protected String patientName;
    protected String doctorName;
    protected LocalDate appointmentDate;

    // Constructor
    public Reservation(BookingID bookingID, String patientID,
                       String patientName, String doctorName,
                       LocalDate appointmentDate) {

        this.bookingID = bookingID;
        this.patientID = patientID;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
    }

    public BookingID getBookingID() {
        return bookingID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    // Abstract Method
    public abstract String getConfirmationDetails();

    // Static Nested Class
    public static class ClientDetails {
        String name;
        String contact;

        public ClientDetails(String name, String contact) {
            this.name = name;
            this.contact = contact;
        }
    }

    // Inner Class
    public class Reminder {
        public void showReminder() {
            System.out.println("Reminder: Appointment on "
                    + appointmentDate);
        }
    }
}