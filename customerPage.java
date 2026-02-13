// ========================= customerPage.java =========================
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.Border;

public class customerPage {

    static String selectedTransaction = null;
    static String selectedType = null;

    public static JFrame frame;   //  ADD THIS

    public static void customer() {

        if (frame != null) {       //  ADD THIS (reuse existing)
            frame.setVisible(true);
            return;
        }

        frame = new JFrame("Customer Page");   //  CHANGED: removed local JFrame
        frame.setSize(1400, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);

        JLabel bg = new JLabel();
        bg.setBounds(0, 0, 1400, 800);
        bg.setLayout(null);
        bg.setIcon(new ImageIcon("bg/train.png"));
        frame.add(bg);

        JPanel mPanel = new JPanel();
        mPanel.setBounds(430, 120, 540, 520);
        mPanel.setBackground(new Color(255, 255, 255, 220));
        mPanel.setLayout(null);
        bg.add(mPanel);

        JLabel logoLbl = new JLabel();
        logoLbl.setBounds(130, -20, 300, 180);
        logoLbl.setIcon(new ImageIcon("bg/logo2.png"));
        mPanel.add(logoLbl);

        JLabel lblTrans = new JLabel("TRANSACTION:");
        lblTrans.setBounds(70, 130, 400, 30);
        lblTrans.setFont(new Font("Impact", Font.ITALIC, 26));
        lblTrans.setForeground(new Color(45, 63, 118));
        mPanel.add(lblTrans);

        JButton btnNew = new JButton("NEW APPLICATION");
        btnNew.setBounds(70, 175, 200, 48);
        btnNew.setBackground(new Color(148, 45, 45));
        btnNew.setForeground(Color.WHITE);
        btnNew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNew.setFocusPainted(false);
        mPanel.add(btnNew);

        JButton btnRenew = new JButton("RENEWAL APPLICATION");
        btnRenew.setBounds(285, 175, 200, 48);
        btnRenew.setBackground(new Color(148, 45, 45));
        btnRenew.setForeground(Color.WHITE);
        btnRenew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRenew.setFocusPainted(false);
        mPanel.add(btnRenew);

        JLabel lblType = new JLabel("TYPE:");
        lblType.setBounds(70, 255, 400, 30);
        lblType.setFont(new Font("Impact", Font.ITALIC, 26));
        lblType.setForeground(new Color(45, 63, 118));
        mPanel.add(lblType);

        JButton btnNormal = new JButton("NORMAL");
        btnNormal.setBounds(70, 300, 200, 48);
        btnNormal.setBackground(new Color(148, 45, 45));
        btnNormal.setForeground(Color.WHITE);
        btnNormal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNormal.setFocusPainted(false);
        mPanel.add(btnNormal);

        JButton btnPriority = new JButton("PRIORITY");
        btnPriority.setBounds(285, 300, 200, 48);
        btnPriority.setBackground(new Color(148, 45, 45));
        btnPriority.setForeground(Color.WHITE);
        btnPriority.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPriority.setFocusPainted(false);
        mPanel.add(btnPriority);

        JButton btnGet = new JButton("GET QUEUE NUMBER");
        btnGet.setBounds(170, 410, 200, 50);
        btnGet.setBackground(new Color(45, 63, 118));
        btnGet.setForeground(Color.WHITE);
        btnGet.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGet.setFocusPainted(false);
        mPanel.add(btnGet);

        Border normalBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        Border selectedBorder = BorderFactory.createLineBorder(new Color(45, 63, 118), 3);

        btnNew.setBorder(normalBorder);
        btnRenew.setBorder(normalBorder);
        btnNormal.setBorder(normalBorder);
        btnPriority.setBorder(normalBorder);

        btnNew.addActionListener(e -> {
            selectedTransaction = "NEW";
            btnNew.setBorder(selectedBorder);
            btnRenew.setBorder(normalBorder);
        });

        btnRenew.addActionListener(e -> {
            selectedTransaction = "RENEW";
            btnRenew.setBorder(selectedBorder);
            btnNew.setBorder(normalBorder);
        });

        btnNormal.addActionListener(e -> {
            selectedType = "Normal";
            btnNormal.setBorder(selectedBorder);
            btnPriority.setBorder(normalBorder);
        });

        btnPriority.addActionListener(e -> {
            selectedType = "Priority";
            btnPriority.setBorder(selectedBorder);
            btnNormal.setBorder(normalBorder);
        });

        btnGet.addActionListener(e -> {

            if (selectedTransaction == null || selectedType == null) {
                JOptionPane.showMessageDialog(frame,
                        "Please select a transaction and a queue type before getting a queue number.",
                        "Incomplete Details",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            //test
            String message =
                    "Please review your selected details:\n\n" +
                    "Transaction: " + selectedTransaction + "\n" +
                    "Type: " + selectedType + "\n\n" +
                    "Providing incorrect information may result in cancellation of your transaction.\n" +
                    "Do you want to continue?";

            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    message,
                    "Confirm Queue Request",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(frame,
                        "Request cancelled. Please check your details and try again.",
                        "Cancelled",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String queueNo = queueManager.generateQueueNo(selectedType);

            queueManager.addQueue(queueNo, selectedTransaction, selectedType);

            JOptionPane.showMessageDialog(frame,
                    "Your Queue Number:\n\n" + queueNo,
                    "Queue Created",
                    JOptionPane.INFORMATION_MESSAGE);

            selectedTransaction = null;
            selectedType = null;

            btnNew.setBorder(normalBorder);
            btnRenew.setBorder(normalBorder);
            btnNormal.setBorder(normalBorder);
            btnPriority.setBorder(normalBorder);
        });

        timeDate(bg);
        frame.setVisible(true);
    }

    public static void timeDate(JLabel bg) {

        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setBounds(980, 12, 380, 32);
        dateTimeLabel.setForeground(Color.WHITE);
        dateTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dateTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bg.add(dateTimeLabel);

        Timer timer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            dateTimeLabel.setText(
                    now.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) + " | " +
                    now.format(DateTimeFormatter.ofPattern("hh:mm a"))
            );
        });

        timer.setInitialDelay(0);
        timer.start();
    }

    public static void main(String[] args) {
        customer();
    }
}
