import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

public class HospitalGUI extends JFrame implements ActionListener {
    JTextField txtPatient;
    JTextField txtDoctor;
    JTextField txtInsurance;
    JComboBox<String> comboReservationType;
    JLabel lblSurgeryType;
    JComboBox<String> comboSurgeryType;
    JLabel lblLabTest;
    JComboBox<String> comboLabTest;
    JButton btnBook;
    JTextArea area;
    private LocalDate selectedDate = LocalDate.now();
    private JLabel lblSelectedDate;

    public HospitalGUI() {
        ReservationManager.initializeCounters();
        ReservationManager.loadPatients();

        setTitle("Smart Health System (SHS)");
        setSize(1152, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Appointment Information"));

        panel.add(new JLabel("Patient Name"));
        txtPatient = new JTextField();
        panel.add(txtPatient);

        panel.add(new JLabel("Doctor Name"));
        txtDoctor = new JTextField();
        panel.add(txtDoctor);

        panel.add(new JLabel("Reservation Type"));
        String[] types = {"Doctor Visit", "Lab Test", "Surgery Slot"};
        comboReservationType = new JComboBox<>(types);
        panel.add(comboReservationType);

        lblLabTest = new JLabel("Lab Test Type");
        String[] labTests = {"Blood Test", "Urine Test", "COVID-19 Test", "X-Ray", "MRI Scan"};
        comboLabTest = new JComboBox<>(labTests);
        panel.add(lblLabTest);
        panel.add(comboLabTest);
        lblLabTest.setVisible(false);
        comboLabTest.setVisible(false);

        lblSurgeryType = new JLabel("Surgery Type");
        String[] surgeryTypes = {"Minor Surgery", "Major Surgery", "Emergency Surgery", "Cosmetic Surgery"};
        comboSurgeryType = new JComboBox<>(surgeryTypes);
        lblSurgeryType.setVisible(false);
        comboSurgeryType.setVisible(false);
        panel.add(lblSurgeryType);
        panel.add(comboSurgeryType);

        comboReservationType.addActionListener(e -> {
            String type = (String) comboReservationType.getSelectedItem();
            boolean isLabTest = "Lab Test".equals(type);
            boolean isSurgery = "Surgery Slot".equals(type);
            lblLabTest.setVisible(isLabTest);
            comboLabTest.setVisible(isLabTest);
            lblSurgeryType.setVisible(isSurgery);
            comboSurgeryType.setVisible(isSurgery);
            panel.revalidate();
            panel.repaint();
        });

        panel.add(new JLabel("Insurance Code"));
        txtInsurance = new JTextField();
        panel.add(txtInsurance);

        panel.add(new JLabel("Appointment Date"));
        JPanel datePanel = new JPanel(new BorderLayout());
        lblSelectedDate = new JLabel(selectedDate.toString());
        JButton btnSelectDate = new JButton("Select Date");
        btnSelectDate.addActionListener(e -> showYearCalendar(selectedDate.getYear()));
        datePanel.add(lblSelectedDate, BorderLayout.CENTER);
        datePanel.add(btnSelectDate, BorderLayout.EAST);
        panel.add(datePanel);

        btnBook = new JButton("Book Appointment");
        btnBook.setBackground(new Color(0, 120, 215));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Arial", Font.BOLD, 16));
        btnBook.setPreferredSize(new Dimension(260, 45));
        btnBook.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnBook);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 10));
        centerWrapper.add(panel, BorderLayout.CENTER);
        centerWrapper.add(buttonPanel, BorderLayout.SOUTH);

        area = new JTextArea();
        area.setEditable(false);
        area.setBorder(BorderFactory.createTitledBorder("Appointment Details"));

        JLabel title = new JLabel("SMART HEALTH SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        add(title, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        add(scrollPane, BorderLayout.SOUTH);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem newItem = new JMenuItem("New");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenuItem listAll = new JMenuItem("List All");
        JMenuItem findByPatient = new JMenuItem("Find by Patient ID");
        JMenuItem calenderView = new JMenuItem("Calender View (Year)");
        JMenuItem aboutMenu = new JMenuItem("About");

        newItem.addActionListener(this);
        saveItem.addActionListener(this);
        loadItem.addActionListener(this);
        exitItem.addActionListener(this);

        listAll.addActionListener(this);
        findByPatient.addActionListener(this);
        calenderView.addActionListener(this);
        aboutMenu.addActionListener(this);

        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.add(exitItem);

        viewMenu.add(listAll);
        viewMenu.add(findByPatient);
        viewMenu.add(calenderView);

        helpMenu.add(aboutMenu);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Book Appointment")) {
            String enteredInsuranceCode = txtInsurance.getText().trim();
            String patientName = txtPatient.getText().trim();
            String selectedType = (String) comboReservationType.getSelectedItem();

            if (patientName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Patient name cannot be empty.");
                return;
            }

            // Check if selected date is in the past
            if (selectedDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                        "You cannot book an appointment for a past date (" + selectedDate + "). Please select today or a future date.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String autoBookingID = ReservationManager.generateBookingCounter();
                BookingID id = new BookingID(autoBookingID);
                String autoPatientID = ReservationManager.getOrCreatePatientID(patientName, enteredInsuranceCode);

                Reservation reservation;
                if ("Doctor Visit".equals(selectedType)) {
                    reservation = new DoctorVisit(id, autoPatientID, patientName, txtDoctor.getText(), selectedDate, enteredInsuranceCode, selectedType);
                } else if ("Lab Test".equals(selectedType)) {
                    String selectedLabTest = (String) comboLabTest.getSelectedItem();
                    reservation = new LabTest(id, autoPatientID, patientName, txtDoctor.getText(), selectedDate, enteredInsuranceCode, selectedType, selectedLabTest);
                } else if ("Surgery Slot".equals(selectedType)) {
                    String selectedSurgery = (String) comboSurgeryType.getSelectedItem();
                    reservation = new SurgerySlot(id, autoPatientID, patientName, txtDoctor.getText(), selectedDate, enteredInsuranceCode, selectedSurgery);
                } else {
                    throw new IllegalArgumentException("Invalid reservation type");
                }

                ReservationManager.addReservation(reservation);
                area.append(reservation.getConfirmationDetails() + "\n\n");

                if (reservation instanceof Payable) {
                    ((Payable) reservation).processPayment();
                }

                txtPatient.setText("");
                txtDoctor.setText("");
                txtInsurance.setText("");
                comboReservationType.setSelectedIndex(0);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }

        } else if (command.equals("Find by Patient ID")) {
            String pId = JOptionPane.showInputDialog(this, "Enter Patient ID (e.g. P001):");
            if (pId != null && !pId.trim().isEmpty()) {
                String result = ReservationManager.getReservationsByPatientID(pId.trim());
                area.setText("--- Search Result for Patient ID: " + pId.toUpperCase() + " ---\n\n" + result);
            }

        } else if (command.equals("New")) {
            txtPatient.setText("");
            txtDoctor.setText("");
            txtInsurance.setText("");
            comboReservationType.setSelectedIndex(0);
            area.setText("");

        } else if (command.equals("Load")) {
            String fileHistory = ReservationManager.readReservationFromFile();
            area.setText(fileHistory != null ? fileHistory : "No saved records found.");

        } else if (command.equals("Save")) {
            ReservationManager.saveReservations();
            JOptionPane.showMessageDialog(this, "Reservations saved successfully!");

        } else if (command.equals("Exit")) {
            System.exit(0);

        } else if (command.equals("List All")) {
            String active = ReservationManager.generateReport();
            area.setText(active.isEmpty() ? "No new appointments in this session. Click 'Load' for past records." : "--- Current Session ---\n\n" + active);

        } else if (command.equals("Calender View (Year)")) {
            showYearCalendar(selectedDate.getYear());

        } else if (command.equals("About")) {
            JOptionPane.showMessageDialog(this, "Smart Health System - Hospital Appointment Manager");
        }
    }

    /**
     * Requirement 2 & 3:
     * Full year view (12 months grid) with appointment indicators and click-to-view details.
     */
    private void showYearCalendar(int year) {
        JDialog yearDialog = new JDialog(this, "Yearly Calendar - " + year, true);
        yearDialog.setSize(1000, 750);
        yearDialog.setLayout(new BorderLayout());

        // Header with Year navigation (< 2026 >)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton prevYearBtn = new JButton("< Prev Year");
        JLabel yearLabel = new JLabel("Year " + year, SwingConstants.CENTER);
        yearLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JButton nextYearBtn = new JButton("Next Year >");

        prevYearBtn.addActionListener(e -> {
            yearDialog.dispose();
            showYearCalendar(year - 1);
        });
        nextYearBtn.addActionListener(e -> {
            yearDialog.dispose();
            showYearCalendar(year + 1);
        });

        headerPanel.add(prevYearBtn);
        headerPanel.add(yearLabel);
        headerPanel.add(nextYearBtn);
        yearDialog.add(headerPanel, BorderLayout.NORTH);

        // 12 months panel in a 3 rows x 4 columns grid
        JPanel yearGrid = new JPanel(new GridLayout(3, 4, 10, 10));
        yearGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int month = 1; month <= 12; month++) {
            yearGrid.add(createMonthMiniPanel(yearDialog, YearMonth.of(year, month)));
        }

        JScrollPane scrollPane = new JScrollPane(yearGrid);
        yearDialog.add(scrollPane, BorderLayout.CENTER);
        yearDialog.setLocationRelativeTo(this);
        yearDialog.setVisible(true);
    }

    private JPanel createMonthMiniPanel(JDialog parentDialog, YearMonth ym) {
        JPanel monthPanel = new JPanel(new BorderLayout(2, 2));
        monthPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel title = new JLabel(ym.getMonth().name(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 12));
        title.setOpaque(true);
        title.setBackground(new Color(230, 240, 250));
        monthPanel.add(title, BorderLayout.NORTH);

        JPanel daysGrid = new JPanel(new GridLayout(0, 7, 2, 2));
        String[] daysHeader = {"S", "M", "T", "W", "T", "F", "S"};
        for (String dh : daysHeader) {
            JLabel lbl = new JLabel(dh, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.PLAIN, 10));
            daysGrid.add(lbl);
        }

        LocalDate first = ym.atDay(1);
        int startDay = first.getDayOfWeek().getValue() % 7; // Sun = 0
        for (int i = 0; i < startDay; i++) {
            daysGrid.add(new JLabel(""));
        }

        int totalDays = ym.lengthOfMonth();
        for (int d = 1; d <= totalDays; d++) {
            LocalDate date = ym.atDay(d);
            JButton dayBtn = new JButton(String.valueOf(d));
            dayBtn.setMargin(new Insets(2, 2, 2, 2));
            dayBtn.setFont(new Font("Arial", Font.PLAIN, 10));

            boolean hasAppt = ReservationManager.hasAppointmentOnDate(date);
            boolean isToday = date.equals(LocalDate.now());
            boolean isPast = date.isBefore(LocalDate.now());

            if (isPast) {
                dayBtn.setEnabled(false); // Disables clicking on past dates
                dayBtn.setForeground(Color.LIGHT_GRAY);
            } else if (hasAppt) {
                dayBtn.setBackground(new Color(255, 179, 71)); // Orange for booked
                dayBtn.setToolTipText("Appointment booked!");
            } else if (isToday) {
                dayBtn.setBackground(new Color(175, 238, 238)); // Cyan for today
            }

            dayBtn.addActionListener(e -> {
                // Double safety check
                if (date.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(parentDialog,
                            "Cannot select a past date: " + date,
                            "Invalid Date",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                selectedDate = date;
                lblSelectedDate.setText(selectedDate.toString());

                String info = ReservationManager.getAppointmentsForDate(date);
                int choice = JOptionPane.showConfirmDialog(
                        parentDialog,
                        "Date: " + date + "\n\n" + info + "\n\nDo you want to select this date for the appointment form?",
                        "Date Details",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    parentDialog.dispose();
                }
            });

            daysGrid.add(dayBtn);
        }

        monthPanel.add(daysGrid, BorderLayout.CENTER);
        return monthPanel;
    }
}