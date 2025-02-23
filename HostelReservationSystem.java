import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;


public class HostelReservationSystem {

    private JFrame frame;
    private JTextField nameField;
    private JComboBox<String> roomNumberComboBox;
    private JComboBox<String> genderComboBox;
    private JComboBox<String> yearComboBox;
    private JComboBox<String> courseComboBox;
    private JButton reserveButton;
    private JButton editButton;
    private JButton searchButton;
    private JButton deleteButton;
    private JTextField searchField;
    private JLabel statusLabel;
    private JTextArea reservationArea;
    private Map<String, List<Member>> roomMap;
    private String username;

    private static final String[] USERNAMES = {"admin", "mentor", "student"};
    private static final String[] PASSWORDS = {"admin", "mentor", "student"};

    private static final String[] GENDER_OPTIONS = {"", "Male", "Female"};
    private static final String[] YEAR_OPTIONS = {"", "First", "Second", "Third", "Fourth"};
    private static final String[] COURSE_OPTIONS = {" ", "CSE", "AI", "EEE", "ECE", "ME", "CE"};
    private static final String[] ROOM_OPTIONS = {
            " ", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110",
            "201", "202", "203", "204", "205", "206", "207", "208", "209",
            "301", "302", "303", "304", "305", "306", "307", "308", "309"
    };
    private static final String DATA_FILE = "reservations.txt";

    public HostelReservationSystem(String username) {
        this.username = username;
        initializeUI();
        initializeRooms();
        loadReservations(); // Load reservations on startup
        updateReservationArea(); // Display all reservations on startup
    }

    private void initializeUI() {
        frame = new JFrame("Hostel Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Institution name label
        JLabel institutionLabel = new JLabel("New Horizon College of Engineering Hostel", SwingConstants.CENTER);
        institutionLabel.setFont(new Font("Arial Sans", Font.BOLD, 23));
        institutionLabel.setOpaque(true);
        institutionLabel.setBackground(new Color(70, 130, 180));  // Set background color for label to steel blue
        frame.add(institutionLabel, BorderLayout.PAGE_START);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(173, 216, 230)); // Set background color to light blue
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add form components
        addFormComponents(formPanel, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial Sans", Font.PLAIN, 18));

        reservationArea = new JTextArea(20, 60);
        reservationArea.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        reservationArea.setEditable(false);

        frame.add(formPanel, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.PAGE_END);
        frame.add(new JScrollPane(reservationArea), BorderLayout.SOUTH);

        frame.getContentPane().setBackground(new Color(173, 216, 230));
        frame.pack();
        frame.setVisible(true);
    }

    private void addFormComponents(JPanel formPanel, GridBagConstraints gbc) {
        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        formPanel.add(nameLabel, gbc);
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Room Number
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel roomNumberLabel = new JLabel("Room Number:");
        roomNumberLabel.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        formPanel.add(roomNumberLabel, gbc);
        roomNumberComboBox = new JComboBox<>(ROOM_OPTIONS);
        roomNumberComboBox.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        gbc.gridx = 1;
        formPanel.add(roomNumberComboBox, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        formPanel.add(genderLabel, gbc);
        genderComboBox = new JComboBox<>(GENDER_OPTIONS);
        genderComboBox.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        gbc.gridx = 1;
        formPanel.add(genderComboBox, gbc);

        // Year
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        formPanel.add(yearLabel, gbc);
        yearComboBox = new JComboBox<>(YEAR_OPTIONS);
        yearComboBox.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        gbc.gridx = 1;
        formPanel.add(yearComboBox, gbc);

        // Course
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        formPanel.add(courseLabel, gbc);
        courseComboBox = new JComboBox<>(COURSE_OPTIONS);
        courseComboBox.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        gbc.gridx = 1;
        formPanel.add(courseComboBox, gbc);

        // Buttons
        Dimension buttonSize = new Dimension(100, 30);

        reserveButton = new JButton("Reserve");
        reserveButton.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        reserveButton.addActionListener(new ReserveButtonListener());
        reserveButton.setPreferredSize(buttonSize);
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(reserveButton, gbc);

        editButton = new JButton("Edit");
        editButton.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        editButton.addActionListener(new EditButtonListener());
        editButton.setPreferredSize(buttonSize);
        gbc.gridx = 1;
        formPanel.add(editButton, gbc);

        deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        deleteButton.addActionListener(new DeleteButtonListener());
        deleteButton.setPreferredSize(buttonSize);
        gbc.gridx = 2;
        formPanel.add(deleteButton, gbc);

        searchField = new JTextField(10);
        searchField.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        gbc.gridx = 3;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Search Name:"), gbc);
        gbc.gridx = 4;
        formPanel.add(searchField, gbc);

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial Sans", Font.PLAIN, 18));
        searchButton.addActionListener(new SearchButtonListener());
        searchButton.setPreferredSize(buttonSize);
        gbc.gridx = 5;
        formPanel.add(searchButton, gbc);

        if (username.equals("student")) {
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    private void initializeRooms() {
        roomMap = new HashMap<>();
        for (String roomNumber : ROOM_OPTIONS) {
            roomMap.put(roomNumber, new ArrayList<>());
        }
    }

    private class ReserveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String roomNumber = (String) roomNumberComboBox.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
            String year = (String) yearComboBox.getSelectedItem();
            String course = (String) courseComboBox.getSelectedItem();

            if (name.isEmpty() || roomNumber == null || gender == null || year == null || course == null) {
                showMessageDialog("All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (roomMap.get(roomNumber).size() < 4) {
                if (isGenderAllowed(roomNumber, gender)) {
                    if (!isDuplicateReservation(roomNumber, name)) {
                        Member newMember = new Member(name, gender, year, course);
                        roomMap.get(roomNumber).add(newMember);
                        showMessageDialog("Room reserved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        saveReservations(); // Save reservations after making a new one

                        clearFields(); // Clear input fields
                    } else {
                        showMessageDialog("The same name cannot be reserved in the same room.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    showMessageDialog("Gender not allowed in this room.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showMessageDialog("Room is full.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class EditButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String roomNumber = (String) roomNumberComboBox.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
            String year = (String) yearComboBox.getSelectedItem();
            String course = (String) courseComboBox.getSelectedItem();

            if (name.isEmpty() || roomNumber == null || gender == null || year == null || course == null) {
                showMessageDialog("All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (Member member : roomMap.get(roomNumber)) {
                if (member.getName().equals(name)) {
                    member.setGender(gender);
                    member.setYear(year);
                    member.setCourse(course);
                    showMessageDialog("Reservation updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    saveReservations(); // Save reservations after editing
                    updateReservationArea(); // Update displayed reservations
                    clearFields(); // Clear input fields
                    return;
                }
            }
            showMessageDialog("Member not found in the selected room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String roomNumber = (String) roomNumberComboBox.getSelectedItem();

            if (name.isEmpty() || roomNumber == null) {
                showMessageDialog("Name and Room Number must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Iterator<Member> iterator = roomMap.get(roomNumber).iterator();
            while (iterator.hasNext()) {
                Member member = iterator.next();
                if (member.getName().equals(name)) {
                    iterator.remove();
                    showMessageDialog("Reservation deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    saveReservations(); // Save reservations after deletion
                    updateReservationArea(); // Update displayed reservations
                    clearFields(); // Clear input fields
                    return;
                }
            }
            showMessageDialog("Member not found in the selected room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchName = searchField.getText().trim();
            if (searchName.isEmpty()) {
                statusLabel.setText("Please enter a name to search.");
                return;
            }

            StringBuilder result = new StringBuilder();
            boolean found = false;

            for (String roomNumber : roomMap.keySet()) {
                for (Member member : roomMap.get(roomNumber)) {
                    if (member.getName().equalsIgnoreCase(searchName)) {
                        result.append("Name: ").append(member.getName())
                                .append(", Room Number: ").append(roomNumber)
                                .append(", Gender: ").append(member.getGender())
                                .append(", Year: ").append(member.getYear())
                                .append(", Course: ").append(member.getCourse())
                                .append("\n");
                        found = true;
                    }
                }
            }

            if (found) {
                reservationArea.setText(result.toString());
                statusLabel.setText("Room reservation details found for " + searchName);
            } else {
                statusLabel.setText("No reservations found for " + searchName);
                reservationArea.setText("");
            }
        }
    }

    private void loadReservations() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String name = parts[0].trim();
                    String roomNumber = parts[1].trim();
                    String gender = parts[2].trim();
                    String year = parts[3].trim();
                    String course = parts[4].trim();
                    Member member = new Member(name, gender, year, course);
                    roomMap.get(roomNumber).add(member);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveReservations() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Map.Entry<String, List<Member>> entry : roomMap.entrySet()) {
                for (Member member : entry.getValue()) {
                    writer.write(member.getName() + "," + entry.getKey() + "," + member.getGender() + "," +
                            member.getYear() + "," + member.getCourse());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateReservationArea() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Member>> entry : roomMap.entrySet()) {
            sb.append("Room: ").append(entry.getKey()).append("\n");
            for (Member member : entry.getValue()) {
                sb.append(member).append("\n");
            }
            sb.append("\n");
        }
        reservationArea.setText(sb.toString());
    }

    private void clearFields() {
        nameField.setText("");
        roomNumberComboBox.setSelectedIndex(0);
        genderComboBox.setSelectedIndex(0);
        yearComboBox.setSelectedIndex(0);
        courseComboBox.setSelectedIndex(0);
    }

    private boolean isGenderAllowed(String roomNumber, String gender) {
        // Example logic: Room 101-110 allows Male, Room 201-209 allows Female
        return (roomNumber.startsWith("1") && gender.equals("Male")) || (roomNumber.startsWith("2") && gender.equals("Female"));
    }

    private boolean isDuplicateReservation(String roomNumber, String name) {
        for (Member member : roomMap.get(roomNumber)) {
            if (member.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void showMessageDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(frame, message, title, messageType);
    }

    public static void main(String[] args) {
        // Create a simple login dialog for demonstration purposes
        String username = JOptionPane.showInputDialog("Enter username:");
        if (username != null && Arrays.asList(USERNAMES).contains(username)) {
            String password = JOptionPane.showInputDialog("Enter password:");
            if (password != null && Arrays.asList(PASSWORDS).contains(password)) {
                new HostelReservationSystem(username);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Member class definition
    public static class Member {
        private String name;
        private String gender;
        private String year;
        private String course;

        public Member(String name, String gender, String year, String course) {
            this.name = name;
            this.gender = gender;
            this.year = year;
            this.course = course;
        }

        public String getName() {
            return name;
        }

        public String getGender() {
            return gender;
        }

        public String getYear() {
            return year;
        }

        public String getCourse() {
            return course;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public void setCourse(String course) {
            this.course = course;
        }

        @Override
        public String toString() {
            return "Name: " + name + ", Gender: " + gender + ", Year: " + year + ", Course: " + course;
        }
    }
}
