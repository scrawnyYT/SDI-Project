import java.io.*;
import java.util.ArrayList;

public class ReservationManager {

    private static ArrayList<Reservation> reservations = new ArrayList<>();
    private static ArrayList<String[]> patientDatabase = new ArrayList<>();

    private static int patientCount = 1;
    private static int bookingCount = 1;

    public static void addReservation(Reservation reservation) {

        reservations.add(reservation);
    }

    public static int getTotalReservations() {
        return reservations.size();
    }

    public static String generateReport() {

        StringBuilder report = new StringBuilder();

        for (Reservation r : reservations) {
            report.append(r.getConfirmationDetails());
            report.append("\n\n");
        }

        return report.toString();

    }

    public static void saveReservations() {

        FileWriter fw = null;

        try {

            fw = new FileWriter(
                    "reservations.txt", true);

            for (Reservation r : reservations) {

                fw.write(r.getConfirmationDetails() + "\n\n");
            }

            System.out.println("Saved Successfully");

        } catch (IOException e) {

            System.out.println(
                    "Error Saving File");

        } finally {

            try {

                if (fw != null)
                    fw.close();

            } catch (IOException e) {

                System.out.println(
                        "Error Closing File");
            }

        }
    }

    public static String readReservationFromFile() {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            return fileContent.toString();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        }
    }


    public static String generatePatientCounter() {
        String newPatientID = String.format("P%03d", patientCount);
        patientCount++;
        return newPatientID;
    }

    public static String generateBookingCounter() {
        String newBookingID = String.format("HSP-2026-%03d", bookingCount);
        bookingCount++;
        return newBookingID;
    }

    public static void registerPatient(String id, String name, String policy) {
        String[] details = {id, name, policy};

        patientDatabase.add(details);
    }

    public static String[] getPatient(String patientID) {
        for (String[] patient : patientDatabase) {

            if (patient[0].equals(patientID)) {
                return patient;
            }
        }

        return null;
    }

    public static void savePatients() {
        try (FileWriter fw = new FileWriter("patients.txt", false)) {

            for (String[] details : patientDatabase) {
                fw.write(details[0] + "," + details[1] + "," + details[2] + "\n");
            }

        } catch (IOException e) {
            System.out.println("Error Saving Patient File");
        }
    }

    public static void loadPatients() {
        patientDatabase.clear();

        try (BufferedReader br = new BufferedReader(new FileReader("patients.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length == 3) {
                    registerPatient(data[0], data[1], data[2]);
                }
            }
        } catch (Exception e) {
            System.out.println("No previous patient file found. Starting fresh!");
        }
    }

    public static void initializeCounters() {
        try (BufferedReader br = new BufferedReader(new FileReader("reservations.txt"))) {
            String line;
            int highestPatientID = 0;
            int highestBookingID = 0;

            while ((line = br.readLine()) != null) {

                if (line.startsWith("Patient ID: P")) {
                    try {
                        String numberString = line.substring(13).trim();

                        int currentId = Integer.parseInt(numberString);

                        if (currentId > highestPatientID) {
                            highestPatientID = currentId;
                        }
                    } catch (NumberFormatException e) {

                    }
                }

                if (line.startsWith("Booking ID: HSP-2026-")) {
                    try {
                        String numberString = line.substring(21).trim();
                        int currentId = Integer.parseInt(numberString);
                        if (currentId > highestBookingID) {
                            highestBookingID = currentId;
                        }
                    } catch (NumberFormatException e) {

                    }
                }
            }


            // Set both counters!
            patientCount = highestPatientID + 1;
            bookingCount = highestBookingID + 1;
            System.out.println("Counters initialized.");
            System.out.println("Next Patient ID will be: P" + String.format("%03d", patientCount));
            System.out.println("Next Booking ID will be: HSP-2026-" + String.format("%03d", bookingCount));

        } catch (Exception e) {
            System.out.println("No previous reservations found. Starting counters at 1.");
            patientCount = 1;
            bookingCount = 1;
        }
    }
    public static boolean isInsuranceCodeUsed(String newCode) {
        for (Reservation r : reservations) {
            if (r.getConfirmationDetails().contains("InsuranceCode: " + newCode)) {
                return true;
            }
        }

        String savedFileText = readReservationFromFile();
        if (savedFileText != null && savedFileText.contains("InsuranceCode: " + newCode)) {
            return true;
        }

        return false;
    }

    public static String getOrCreatePatientID(String patientName, String insuranceCode) {

        String cleanName = patientName.trim();

        System.out.println("");
        System.out.println("Looking for patient: [" + cleanName + "]");
        System.out.println("Current patients in memory: " + patientDatabase.size());
        System.out.println("");

        for (String[] patient : patientDatabase) {
            String savedName = patient[1].trim();
            System.out.println("Comparing with saved patient: [" + savedName + "]");

            if (savedName.equalsIgnoreCase(cleanName)) {
                System.out.println("MATCH FOUND! Reusing old ID: " + patient[0]);
                return patient[0];
            }
        }

        System.out.println("No match found. Generating a brand new ID.");
        String newPatientId = generatePatientCounter();

        registerPatient(newPatientId, cleanName, insuranceCode);
        savePatients();

        return newPatientId;
    }

    public static boolean doesPatientOwnCode(String patientName, String insuranceCode) {
        for (String[] patient : patientDatabase) {
            if (patient[1].equalsIgnoreCase(patientName) && patient[2].equals(insuranceCode)) {
                return true;
            }
        }
        return false;
    }
    // 1. Get all reservations belonging to a specific Patient ID
    public static String getReservationsByPatientID(String pId) {
        StringBuilder sb = new StringBuilder();
        String target = pId.trim().toUpperCase();

        // Check active in-memory reservations
        for (Reservation r : reservations) {
            if (r.getPatientID().equalsIgnoreCase(target)) {
                sb.append(r.getConfirmationDetails()).append("\n\n");
            }
        }

        // Also parse historical records from reservations.txt
        String savedText = readReservationFromFile();
        if (savedText != null && !savedText.isEmpty()) {
            String[] blocks = savedText.split("\n\n+");
            for (String block : blocks) {
                if (block.toUpperCase().contains("PATIENT ID: " + target)) {
                    // Avoid duplicating if already present
                    if (!sb.toString().contains(block.trim())) {
                        sb.append(block.trim()).append("\n\n");
                    }
                }
            }
        }

        if (sb.length() == 0) {
            return "No reservations found for Patient ID: " + pId;
        }
        return sb.toString();
    }

    // 2. Check if a date has any appointment
    public static boolean hasAppointmentOnDate(java.time.LocalDate date) {
        String dateStr = date.toString();
        for (Reservation r : reservations) {
            if (r.getAppointmentDate().equals(date)) {
                return true;
            }
        }
        String saved = readReservationFromFile();
        return (saved != null && saved.contains("Date: " + dateStr));
    }

    // 3. Get appointment details for a specific date
    public static String getAppointmentsForDate(java.time.LocalDate date) {
        StringBuilder sb = new StringBuilder();
        String dateStr = "Date: " + date.toString();

        for (Reservation r : reservations) {
            if (r.getAppointmentDate().equals(date)) {
                sb.append(r.getConfirmationDetails()).append("\n\n");
            }
        }

        String saved = readReservationFromFile();
        if (saved != null) {
            String[] blocks = saved.split("\n\n+");
            for (String block : blocks) {
                if (block.contains(dateStr)) {
                    if (!sb.toString().contains(block.trim())) {
                        sb.append(block.trim()).append("\n\n");
                    }
                }
            }
        }

        if (sb.length() == 0) {
            return "No appointments scheduled on " + date;
        }
        return sb.toString();
    }
}