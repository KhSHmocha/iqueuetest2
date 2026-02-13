import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class IQueue {

    // Optional: keep a reference if you want to reopen login later
    static JFrame loginFrame;

    public static void main(String[] args) {
        login();
    }

    public static void login() {

        // Make sure accounts.txt exists (Admin / Staff defaults)
        AccountManager.initialize();
        CounterManager.initialize();

        // Reuse the same login window (so you can open Admin/Staff/Monitor at the same time)
        if (loginFrame != null && loginFrame.isDisplayable()) {
            loginFrame.setVisible(true);
            loginFrame.toFront();
            loginFrame.requestFocus();
            return;
        }

        JFrame mframe = new JFrame("I_Queue");
        loginFrame = mframe;

        mframe.setSize(1400, 800);
        mframe.setLocationRelativeTo(null);
        mframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mframe.setResizable(false);
        mframe.setLayout(null);

        JPanel pnl1 = new JPanel();
        pnl1.setBackground(Color.WHITE);
        pnl1.setLayout(null);
        pnl1.setBounds(0, 0, 1400, 800);
        mframe.add(pnl1);

        JLabel bglbl = new JLabel();
        bglbl.setBounds(0, 0, 1400, 800);
        bglbl.setIcon(new ImageIcon("bg/train.png"));
        pnl1.add(bglbl);

        JPanel card = new JPanel();
        card.setBounds(500, 150, 400, 500);
        card.setBackground(new Color(255, 255, 255, 230));
        card.setLayout(null);
        bglbl.add(card);

        JLabel title = new JLabel("I_Queue", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setBounds(100, 20, 200, 50);
        card.add(title);

        JLabel user = new JLabel("Username:");
        user.setFont(new Font("Segoe UI", Font.BOLD, 20));
        user.setBounds(30, 100, 150, 50);
        card.add(user);

        JLabel pass = new JLabel("Password:");
        pass.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pass.setBounds(30, 190, 150, 50);
        card.add(pass);

        JTextField txtuser = new JTextField();
        txtuser.setBounds(50, 150, 300, 40);
        txtuser.setFont(new Font("Segoe UI", Font.PLAIN, 21));
        card.add(txtuser);

        JPasswordField txtpass = new JPasswordField();
        txtpass.setBounds(50, 240, 300, 40);
        txtpass.setFont(new Font("Segoe UI", Font.PLAIN, 21));
        card.add(txtpass);

        JButton loginbtn = new JButton("LOGIN");
        loginbtn.setBounds(70, 300, 110, 40);
        loginbtn.setBackground(new Color(129, 199, 132));
        loginbtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginbtn.setForeground(Color.BLACK);
        loginbtn.setFocusPainted(false);
        card.add(loginbtn);

        JButton clearbtn = new JButton("CLEAR");
        clearbtn.setBounds(220, 300, 110, 40);
        clearbtn.setBackground(new Color(239, 154, 154));
        clearbtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        clearbtn.setForeground(Color.BLACK);
        clearbtn.setFocusPainted(false);
        card.add(clearbtn);

        // ENTER key triggers login
        mframe.getRootPane().setDefaultButton(loginbtn);

        loginbtn.addActionListener(e -> {

            String username = txtuser.getText().trim();
            String password = new String(txtpass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        mframe,
                        "USERNAME / PASSWORD REQUIRED",
                        "Invalid",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            String role = AccountManager.authenticateRole(username, password); // Admin / Staff / null

            if (role == null) {
                JOptionPane.showMessageDialog(
                        mframe,
                        "INVALID CREDENTIALS",
                        "Invalid",
                        JOptionPane.INFORMATION_MESSAGE
                );
                txtpass.setText("");
                txtuser.setText("");
                txtuser.requestFocus();
                return;
            }

            JOptionPane.showMessageDialog(
                    mframe,
                    "Welcome " + role,
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

                        if (role.equalsIgnoreCase("Admin")) {

                txtuser.setText("");
                txtpass.setText("");
                txtuser.requestFocus();

                adminPage.admin(username, role);

            } else if (role.equalsIgnoreCase("Staff")) {

                // ===== RULES FOR STAFF =====
                String assignedCounter = AccountManager.getAssignedCounter(username);

                // 1) Staff can't operate if not assigned a counter
                if (assignedCounter == null) assignedCounter = "-";
                assignedCounter = assignedCounter.trim();

                if (assignedCounter.isEmpty() || assignedCounter.equals("-") || assignedCounter.equalsIgnoreCase("NONE")) {
                    JOptionPane.showMessageDialog(
                            mframe,
                            "You have no assigned counter.\nPlease ask the admin to assign you first.",
                            "No Counter Assigned",
                            JOptionPane.WARNING_MESSAGE
                    );
                    txtpass.setText("");
                    txtuser.requestFocus();
                    return;
                }

                // 2) Same counter can't be used by 2 staff at the same time
                if (!SessionManager.startSession(username, assignedCounter)) {
                    String who = SessionManager.whoUsesCounter(assignedCounter);
                    JOptionPane.showMessageDialog(
                            mframe,
                            "Counter " + assignedCounter + " is already in use" + (who != null ? (" by " + who) : "") + ".",
                            "Counter In Use",
                            JOptionPane.WARNING_MESSAGE
                    );
                    txtpass.setText("");
                    txtuser.requestFocus();
                    return;
                }

                txtuser.setText("");
                txtpass.setText("");
                txtuser.requestFocus();

                staffPage.staff(username, role);

            } else {

                JOptionPane.showMessageDialog(
                        mframe,
                        "Unknown role: " + role,
                        "Invalid",
                        JOptionPane.WARNING_MESSAGE
                );
            }
});

        clearbtn.addActionListener(e -> {
        txtuser.setText("");
        txtpass.setText("");
        txtuser.requestFocus();
});


        // ESC closes app
        mframe.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    mframe.dispose();
                }
            }
        });

        mframe.setVisible(true);
    }
}
