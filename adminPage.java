import java.awt.*;
import java.time.*;
import java.time.format.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class adminPage {

    public static void admin() {
        admin("User", "Role");
    }

    public static void admin(String username, String role) {

        JFrame frame = new JFrame("Admin Page");
        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(49, 55, 114, 255));
        leftPanel.setBounds(0, 0, 300, 800);
        leftPanel.setLayout(null);
        leftPanel.setBorder(new MatteBorder(0, 0, 0, 6, new Color(0, 0, 0, 90)));
        frame.add(leftPanel);

        // logo
        ImageIcon originalIcon = new ImageIcon();
        JLabel logo = new JLabel(originalIcon);
        logo.setIcon(new ImageIcon("bg/logo1.png"));
        frame.setIconImage(originalIcon.getImage());
        logo.setBounds(30, 30, 240, 140);
        leftPanel.add(logo);

        JPanel logoBorder = new JPanel();
        logoBorder.setBackground(new Color(255, 255, 255, 255));
        logoBorder.setBounds(30, 40, 240, 120);
        logoBorder.setLayout(null);
        logoBorder.setBorder(new MatteBorder(3, 3, 3, 3, new Color(0, 0, 0, 80)));
        leftPanel.add(logoBorder);

        JButton btn1 = new JButton("Dashboard");
        btn1.setBounds(50, 200, 200, 50);
        btn1.setFocusPainted(false);
        btn1.setOpaque(true);
        btn1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn1.setForeground(new Color(49, 55, 114, 255));
        leftPanel.add(btn1);

        JButton btn2 = new JButton("Queue Management");
        btn2.setBounds(50, 300, 200, 50);
        btn2.setFocusPainted(false);
        btn2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn2.setForeground(new Color(49, 55, 114, 255));
        leftPanel.add(btn2);

        JButton btn3 = new JButton("Staff Management");
        btn3.setBounds(50, 400, 200, 50);
        btn3.setFocusPainted(false);
        btn3.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn3.setForeground(new Color(49, 55, 114, 255));
        leftPanel.add(btn3);

        JButton btn4 = new JButton("Reports");
        btn4.setBounds(50, 500, 200, 50);
        btn4.setFocusPainted(false);
        btn4.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn4.setForeground(new Color(49, 55, 114, 255));
        leftPanel.add(btn4);

        JButton btn5 = new JButton("Sign Out");
        btn5.setBounds(70, 650, 150, 50);
        btn5.setFocusPainted(false);
        btn5.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn5.setForeground(new Color(49, 55, 114, 255));
        leftPanel.add(btn5);

        // TOP PANEL
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(151, 44, 52, 255));
        topPanel.setBounds(300, 0, 1100, 60);
        topPanel.setLayout(null);
        topPanel.setBorder(new MatteBorder(0, 0, 6, 0, new Color(0, 0, 0, 90)));
        frame.add(topPanel);

        JLabel userRole = new JLabel(username + " | " + role);
        userRole.setBounds(20, 20, 300, 24);
        userRole.setForeground(Color.WHITE);
        userRole.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userRole.setHorizontalAlignment(SwingConstants.LEFT);
        topPanel.add(userRole);

        JLabel pagetitle = new JLabel("");
        pagetitle.setBounds(450, 20, 250, 24);
        pagetitle.setForeground(Color.WHITE);
        pagetitle.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 18));
        pagetitle.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(pagetitle);

        JLabel dateLabel = new JLabel();
        dateLabel.setBounds(800, 5, 280, 18);
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(dateLabel);

        JLabel timeLabel = new JLabel();
        timeLabel.setBounds(800, 25, 280, 24);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(timeLabel);

        Timer clockTimer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
            dateLabel.setText(now.format(dateFormat));
            timeLabel.setText(now.format(timeFormat));
        });
        clockTimer.start();

        CardLayout card = new CardLayout();
        JPanel mainPanel = new JPanel(card);
        mainPanel.setBounds(300, 60, 1100, 703);
        mainPanel.setBorder(new MatteBorder(0, 0, 6, 0, new Color(0, 0, 0, 90)));
        frame.add(mainPanel);

        JPanel dashboard = new JPanel();
        dashboard.setLayout(null);
        dashboard.setBackground(new Color(255, 255, 255, 200));
        openDashboardPage(dashboard, frame);

        JPanel queue = new JPanel();
        queue.setLayout(null);
        queue.setBackground(new Color(255, 255, 255, 200));
        openQueuePage(queue, frame, username);

        JPanel staff = new JPanel();
        staff.setLayout(null);
        staff.setBackground(new Color(255, 255, 255, 200));
        openStaffPage(staff, frame);

        JPanel report = new JPanel();
        report.setLayout(null);
        report.setBackground(new Color(255, 255, 255, 200));
        openReportPage(report, frame, username);

        mainPanel.add(dashboard, "DASHBOARD");
        mainPanel.add(queue, "QUEUE");
        mainPanel.add(staff, "STAFF");
        mainPanel.add(report, "REPORT");

        card.show(mainPanel, "DASHBOARD");
        pagetitle.setText("DASHBOARD");

        btn1.addActionListener(e -> {
            card.show(mainPanel, "DASHBOARD");
            pagetitle.setText("DASHBOARD");
        });

        btn2.addActionListener(e -> {
            card.show(mainPanel, "QUEUE");
            pagetitle.setText("QUEUE MANAGEMENT");
        });

        btn3.addActionListener(e -> {
            card.show(mainPanel, "STAFF");
            pagetitle.setText("STAFF MANAGEMENT");
        });

        btn4.addActionListener(e -> {
            card.show(mainPanel, "REPORT");
            pagetitle.setText("REPORTS");
        });

        btn5.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to sign out?", "Sign Out", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                frame.setVisible(false);
                IQueue.login();
            }
        });

        frame.setVisible(true);
    }

    // =========================== QUEUE PAGE ===========================
    static void openQueuePage(JPanel queue, JFrame frame, String username) {

        Color navy = new Color(49, 55, 114, 255);
        Color borderCol = new Color(0, 0, 0, 80);

        Color queuebg = new Color(238, 240, 255);
        queue.setBackground(queuebg);

        JPanel filterBar = new JPanel();
        filterBar.setBounds(50, 15, 1000, 45);
        filterBar.setLayout(null);
        filterBar.setBackground(new Color(255, 255, 255));
        filterBar.setOpaque(true);
        filterBar.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        queue.add(filterBar);

        JLabel filterLbl = new JLabel("FILTER:");
        filterLbl.setBounds(20, 10, 90, 25);
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
        filterLbl.setForeground(Color.BLACK);
        filterBar.add(filterLbl);

        String[] filterOptions = { "All", "Normal", "Priority" };
        JComboBox<String> filterBox = new JComboBox<>(filterOptions);
        filterBox.setBounds(110, 10, 200, 25);
        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterBar.add(filterBox);

        JPanel tableBg = new JPanel();
        tableBg.setBounds(50, 70, 1000, 400);
        tableBg.setLayout(null);
        tableBg.setBackground(new Color(125, 132, 208, 255));
        tableBg.setOpaque(true);
        tableBg.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        queue.add(tableBg);

        DefaultTableModel queueModel = new DefaultTableModel();
        queueModel.addColumn("Queue No.");
        queueModel.addColumn("Transaction");
        queueModel.addColumn("Type");
        queueModel.addColumn("Status");

        JTable queueTable = new JTable(queueModel);
        queueTable.setRowHeight(32);
        queueTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        queueTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        queueTable.setRowSelectionAllowed(true);
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane queueScroll = new JScrollPane(queueTable);
        queueScroll.setBounds(0, 0, 1000, 400);
        queueScroll.setBorder(null);
        tableBg.add(queueScroll);

        JPanel btnBar = new JPanel();
        btnBar.setBounds(50, 485, 1000, 55);
        btnBar.setLayout(null);
        btnBar.setBackground(new Color(255, 255, 255));
        btnBar.setOpaque(true);
        btnBar.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        queue.add(btnBar);

        JButton btnDone = new JButton("Done");
        btnDone.setBounds(120, 10, 120, 35);
        btnDone.setFocusPainted(false);
        btnDone.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDone.setBackground(navy);
        btnDone.setForeground(Color.WHITE);
        btnBar.add(btnDone);

        JButton btnCallNext = new JButton("Call Next");
        btnCallNext.setBounds(250, 10, 120, 35);
        btnCallNext.setFocusPainted(false);
        btnCallNext.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCallNext.setBackground(navy);
        btnCallNext.setForeground(Color.WHITE);
        btnBar.add(btnCallNext);

        JButton btnSkip = new JButton("Skip");
        btnSkip.setBounds(380, 10, 120, 35);
        btnSkip.setFocusPainted(false);
        btnSkip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSkip.setBackground(navy);
        btnSkip.setForeground(Color.WHITE);
        btnBar.add(btnSkip);

        JButton btnCallAgain = new JButton("Call Again");
        btnCallAgain.setBounds(510, 10, 120, 35);
        btnCallAgain.setFocusPainted(false);
        btnCallAgain.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCallAgain.setBackground(navy);
        btnCallAgain.setForeground(Color.WHITE);
        btnBar.add(btnCallAgain);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(640, 10, 120, 35);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(navy);
        btnCancel.setForeground(Color.WHITE);
        btnBar.add(btnCancel);

        JPanel statusStrip = new JPanel();
        statusStrip.setLayout(null);
        statusStrip.setBounds(50, 555, 1000, 70);
        statusStrip.setBackground(new Color(255, 255, 255));
        statusStrip.setOpaque(true);
        statusStrip.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        queue.add(statusStrip);

        JLabel statusText = new JLabel("QUEUE STATUS:");
        statusText.setBounds(20, 22, 160, 25);
        statusText.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        statusStrip.add(statusText);

        JLabel statusValue = new JLabel("CLOSE");
        statusValue.setBounds(170, 22, 120, 25);
        statusValue.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        statusValue.setForeground(new Color(220, 60, 60));
        statusValue.setHorizontalAlignment(SwingConstants.LEFT);
        statusStrip.add(statusValue);

        JButton btnOpenQueue = new JButton("OPEN QUEUE");
        btnOpenQueue.setBounds(700, 18, 140, 35);
        btnOpenQueue.setFocusPainted(false);
        btnOpenQueue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOpenQueue.setBackground(new Color(90, 200, 90));
        btnOpenQueue.setForeground(Color.WHITE);
        statusStrip.add(btnOpenQueue);

        JButton btnCloseQueue = new JButton("CLOSE QUEUE");
        btnCloseQueue.setBounds(850, 18, 140, 35);
        btnCloseQueue.setFocusPainted(false);
        btnCloseQueue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCloseQueue.setBackground(new Color(220, 60, 60));
        btnCloseQueue.setForeground(Color.WHITE);
        statusStrip.add(btnCloseQueue);
        JButton btnOpenMonitor = new JButton("OPEN QUEUE MONITOR");
        btnOpenMonitor.setBounds(515, 18, 175, 35);
        btnOpenMonitor.setFocusPainted(false);
        btnOpenMonitor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOpenMonitor.setBackground(navy);
        btnOpenMonitor.setForeground(Color.WHITE);
        statusStrip.add(btnOpenMonitor);

        btnOpenMonitor.addActionListener(e -> {
            queueDisplayMonitor.monitor();
        });


        Runnable loadQueue = () -> {

            String selectedQueueNo = null;
            int selectedRow = queueTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedQueueNo = queueModel.getValueAt(selectedRow, 0).toString();
            }

            queueModel.setRowCount(0);

            String selectedFilter = filterBox.getSelectedItem().toString();
            for (String[] r : queueManager.getRowsForAdminTable4(selectedFilter)) {
                queueModel.addRow(r);
            }

            if (selectedQueueNo != null) {
                for (int i = 0; i < queueModel.getRowCount(); i++) {
                    if (queueModel.getValueAt(i, 0).toString().equals(selectedQueueNo)) {
                        queueTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        };

        loadQueue.run();

        new Timer(1000, e -> loadQueue.run()).start();
        filterBox.addActionListener(e -> loadQueue.run());

        // keep statusValue synced
        new Timer(500, e -> {
            if (queueManager.isQueueOpen()) {
                statusValue.setText("OPEN");
                statusValue.setForeground(new Color(90, 200, 90));
            } else {
                statusValue.setText("CLOSE");
                statusValue.setForeground(new Color(220, 60, 60));
            }
        }).start();

        btnOpenQueue.addActionListener(e -> {

            queueManager.setQueueOpen(true);

            if (customerPage.frame == null || !customerPage.frame.isDisplayable()) {
                customerPage.customer(); // must set customerPage.frame inside
            } else {
                customerPage.frame.setVisible(true);
                customerPage.frame.toFront();
                customerPage.frame.requestFocus();
            }

            // queue display monitor removed

            loadQueue.run();
        });

        btnCloseQueue.addActionListener(e -> {

            queueManager.setQueueOpen(false);

            if (customerPage.frame != null) {
                customerPage.frame.dispose();
                customerPage.frame = null;
            }
            loadQueue.run();
        });

        btnCallNext.addActionListener(e -> {

            if (queueManager.hasServing("Serving-C1")) {

                String[] current = queueManager.getServingRow("Serving-C1");
                String currentNo = (current == null) ? "" : current[queueManager.COL_QUEUE_NO];

                JOptionPane.showMessageDialog(frame,
                        "Counter 1 is still serving:\n" + currentNo + "\n\nFinish transaction before calling next.");
                return;
            }

            String[] served = queueManager.serveNextForCounter("C1", username);

            if (served == null) {
                JOptionPane.showMessageDialog(frame, "No customers available.");
            }

            loadQueue.run();
        });

        btnSkip.addActionListener(e -> {
            int row = queueTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a queue first.");
                return;
            }
            String queueNo = queueModel.getValueAt(row, 0).toString();

            if (!queueManager.skipQueue(queueNo)) {
                JOptionPane.showMessageDialog(frame, "Only 'Waiting' queues can be skipped.");
            }
            loadQueue.run();
        });

        btnCallAgain.addActionListener(e -> {
            int row = queueTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a queue first.");
                return;
            }
            String queueNo = queueModel.getValueAt(row, 0).toString();

            if (!queueManager.recallSkipped(queueNo)) {
                JOptionPane.showMessageDialog(frame, "Only 'Skipped' queues can be recalled.");
            }
            loadQueue.run();
        });

        btnCancel.addActionListener(e -> {
            int row = queueTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a queue first.");
                return;
            }
            String queueNo = queueModel.getValueAt(row, 0).toString();

            int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel this queue?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                if (!queueManager.cancelQueue(queueNo)) {
                    JOptionPane.showMessageDialog(frame, "Cannot cancel this queue.");
                }
                loadQueue.run();
            }
        });

        btnDone.addActionListener(e -> {
            int row = queueTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a queue first.");
                return;
            }

            String queueNo = queueModel.getValueAt(row, 0).toString();

            int option = JOptionPane.showConfirmDialog(frame, "Mark this queue as DONE?\n" + queueNo, "Confirm Done",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                if (!queueManager.markDone(queueNo)) {
                    JOptionPane.showMessageDialog(frame, "Only 'Serving' queues can be marked as Done.");
                }
                loadQueue.run();
            }
        });
    }

    // =========================== REPORT PAGE ===========================
    static void openReportPage(JPanel report, JFrame frame, String username) {

        Color navy = new Color(49, 55, 114, 255);
        Color borderCol = new Color(0, 0, 0, 80);
        Color cardBg = new Color(205, 208, 245);
        Color reportbg = new Color(238, 240, 255);
        report.setBackground(reportbg);

        JPanel card1 = new JPanel();
        card1.setBounds(60, 30, 315, 85);
        card1.setLayout(null);
        card1.setBackground(cardBg);
        card1.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        report.add(card1);

        JLabel lbl1 = new JLabel("RENEWAL PROCESSED");
        lbl1.setBounds(18, 12, 250, 18);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card1.add(lbl1);

        JLabel val1 = new JLabel("0");
        val1.setBounds(18, 32, 250, 45);
        val1.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        val1.setForeground(navy);
        card1.add(val1);

        JPanel card2 = new JPanel();
        card2.setBounds(395, 30, 315, 85);
        card2.setLayout(null);
        card2.setBackground(cardBg);
        card2.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        report.add(card2);

        JLabel lbl2 = new JLabel("NEW CARDS ISSUED");
        lbl2.setBounds(18, 12, 250, 18);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card2.add(lbl2);

        JLabel val2 = new JLabel("0");
        val2.setBounds(18, 32, 250, 45);
        val2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        val2.setForeground(navy);
        card2.add(val2);

        JPanel card3 = new JPanel();
        card3.setBounds(730, 30, 315, 85);
        card3.setLayout(null);
        card3.setBackground(cardBg);
        card3.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        report.add(card3);

        JLabel lbl3 = new JLabel("CANCELLED");
        lbl3.setBounds(18, 12, 250, 18);
        lbl3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card3.add(lbl3);

        JLabel val3 = new JLabel("0");
        val3.setBounds(18, 32, 250, 45);
        val3.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        val3.setForeground(navy);
        card3.add(val3);

        JPanel card4 = new JPanel();
        card4.setBounds(60, 130, 315, 85);
        card4.setLayout(null);
        card4.setBackground(cardBg);
        card4.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        report.add(card4);

        JLabel lbl4 = new JLabel("PRIORITY SERVED");
        lbl4.setBounds(18, 12, 250, 18);
        lbl4.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card4.add(lbl4);

        JLabel valPriServed = new JLabel("0");
        valPriServed.setBounds(18, 32, 250, 45);
        valPriServed.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        valPriServed.setForeground(navy);
        card4.add(valPriServed);

        JPanel card5 = new JPanel();
        card5.setBounds(395, 130, 315, 85);
        card5.setLayout(null);
        card5.setBackground(cardBg);
        card5.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        report.add(card5);

        JLabel lbl5 = new JLabel("NORMAL SERVED");
        lbl5.setBounds(18, 12, 250, 18);
        lbl5.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card5.add(lbl5);

        JLabel valNormServed = new JLabel("0");
        valNormServed.setBounds(18, 32, 250, 45);
        valNormServed.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        valNormServed.setForeground(navy);
        card5.add(valNormServed);

        JPanel card6 = new JPanel();
        card6.setBounds(730, 130, 315, 85);
        card6.setLayout(null);
        card6.setBackground(cardBg);
        card6.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        report.add(card6);

        JLabel lbl6 = new JLabel("TOTAL TRANSACTIONS");
        lbl6.setBounds(18, 12, 250, 18);
        lbl6.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card6.add(lbl6);

        JLabel valTotal = new JLabel("0");
        valTotal.setBounds(18, 32, 250, 45);
        valTotal.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        valTotal.setForeground(navy);
        card6.add(valTotal);

        JLabel reportDate = new JLabel("REPORT DATE: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        reportDate.setBounds(60, 230, 520, 25);
        reportDate.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
        report.add(reportDate);

        JLabel filterLbl = new JLabel("FILTER:");
        filterLbl.setBounds(740, 230, 80, 25);
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
        filterLbl.setForeground(Color.BLACK);
        report.add(filterLbl);

        String[] reportFilterOptions = { "All", "Done", "Cancelled", "Priority", "Normal" };
        JComboBox<String> reportFilterBox = new JComboBox<>(reportFilterOptions);
        reportFilterBox.setBounds(835, 230, 210, 25);
        reportFilterBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        report.add(reportFilterBox);

        // ===== SEARCH BAR =====
        JLabel searchLbl = new JLabel("SEARCH:");
        searchLbl.setBounds(330, 230, 90, 25);
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
        searchLbl.setForeground(Color.BLACK);
        report.add(searchLbl);

        JTextField searchField = new JTextField();
        searchField.setBounds(400, 230, 260, 25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        report.add(searchField);

        JButton btnClearSearch = new JButton("Clear");
        btnClearSearch.setBounds(650, 230, 80, 25);
        btnClearSearch.setFocusPainted(false);
        btnClearSearch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnClearSearch.setBackground(navy);
        btnClearSearch.setForeground(Color.WHITE);
        report.add(btnClearSearch);

        JPanel tableBg = new JPanel();
        tableBg.setBounds(60, 270, 985, 300);
        tableBg.setLayout(null);
        tableBg.setBackground(new Color(186, 190, 240));
        tableBg.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        report.add(tableBg);

        DefaultTableModel reportModel = new DefaultTableModel();
        reportModel.addColumn("QUEUE NO");
        reportModel.addColumn("TRANSACTION");
        reportModel.addColumn("TYPE");
        reportModel.addColumn("SERVED BY");
        reportModel.addColumn("STATUS");
        reportModel.addColumn("START");
        reportModel.addColumn("FINISH");

        JTable reportTable = new JTable(reportModel);
        reportTable.setRowHeight(28);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<DefaultTableModel> reportSorter = new TableRowSorter<>(reportModel);
        reportTable.setRowSorter(reportSorter);

        Runnable applyReportFilters = () -> {
            String filterValue = reportFilterBox.getSelectedItem().toString();
            String searchText = searchField.getText().trim();

            RowFilter<DefaultTableModel, Object> statusFilter = new RowFilter<>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {

                    String type = entry.getStringValue(2);      // TYPE
                    String status = entry.getStringValue(4);    // STATUS (because Served By is col 3 now)

                    boolean isDone = status.equalsIgnoreCase("Done");
                    boolean isCancelled = status.equalsIgnoreCase("Cancelled");

                    // Filter dropdown logic
                    if (filterValue.equals("All")) return (isDone || isCancelled);
                    if (filterValue.equalsIgnoreCase("Done")) return isDone;
                    if (filterValue.equalsIgnoreCase("Cancelled")) return isCancelled;
                    if (filterValue.equalsIgnoreCase("Priority")) return type.equalsIgnoreCase("Priority");
                    if (filterValue.equalsIgnoreCase("Normal")) return type.equalsIgnoreCase("Normal");

                    return true;
                }
            };

            RowFilter<DefaultTableModel, Object> searchFilter = null;
            if (!searchText.isEmpty()) {
                searchFilter = RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText));
            }

            if (searchFilter == null) {
                reportSorter.setRowFilter(statusFilter);
            } else {
                reportSorter.setRowFilter(RowFilter.andFilter(java.util.List.of(statusFilter, searchFilter)));
            }
        };

        JScrollPane sp = new JScrollPane(reportTable);
        sp.setBounds(0, 0, 985, 300);
        sp.setBorder(null);
        tableBg.add(sp);

        JButton btnExport = new JButton("EXPORT REPORT");
        btnExport.setBounds(440, 585, 220, 45);
        btnExport.setFocusPainted(false);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setBackground(navy);
        btnExport.setForeground(Color.WHITE);
        report.add(btnExport);

        Runnable loadReport = () -> {

            String selectedQueueNo = null;
            int selectedRow = reportTable.getSelectedRow();
            if (selectedRow != -1) {
                selectedQueueNo = reportModel.getValueAt(selectedRow, 0).toString();
            }

            String selectedFilter = reportFilterBox.getSelectedItem().toString();
            reportModel.setRowCount(0);

            int renewal = 0;
            int newCards = 0;
            int cancelled = 0;
            int priorityServed = 0;
            int normalServed = 0;
            int total = 0;

            for (String[] row : queueManager.queueList) {

                String qNo = row[queueManager.COL_QUEUE_NO];
                String trans = row[queueManager.COL_TRANS];
                String type = row[queueManager.COL_TYPE];
                String status = row[queueManager.COL_STATUS];
                String start = row[queueManager.COL_START_DT];
                String finish = row[queueManager.COL_FINISH_DT];
                String servedBy = "";
                if (row != null && row.length > queueManager.COL_SERVED_BY) {
                    servedBy = row[queueManager.COL_SERVED_BY];
                }
                if (servedBy == null || servedBy.trim().equals("-")) servedBy = "";

                boolean isDone = status.equalsIgnoreCase("Done");
                boolean isCancelled = status.equalsIgnoreCase("Cancelled");

                if (!isDone && !isCancelled)
                    continue;

                total++;

                if (isCancelled) {
                    cancelled++;
                }

                if (isDone) {

                    if (type.equalsIgnoreCase("Priority"))
                        priorityServed++;
                    if (type.equalsIgnoreCase("Normal"))
                        normalServed++;

                    String transUpper = (trans == null) ? "" : trans.trim().toUpperCase();

                    if (transUpper.equals("RENEW") || transUpper.equals("RENEWAL")) {
                        renewal++;
                    } else if (transUpper.equals("NEW") || transUpper.equals("NEW CARD") || transUpper.equals("NEW CARDS")
                            || transUpper.equals("ISSUE") || transUpper.equals("ISSUANCE")) {
                        newCards++;
                    }
                }

                boolean show = false;

                if (selectedFilter.equals("All"))
                    show = true;
                else if (selectedFilter.equalsIgnoreCase("Done") && isDone)
                    show = true;
                else if (selectedFilter.equalsIgnoreCase("Cancelled") && isCancelled)
                    show = true;
                else if (selectedFilter.equalsIgnoreCase("Priority") && type.equalsIgnoreCase("Priority"))
                    show = true;
                else if (selectedFilter.equalsIgnoreCase("Normal") && type.equalsIgnoreCase("Normal"))
                    show = true;

                if (!show)
                    continue;

                reportModel.addRow(new String[] { qNo, trans, type, servedBy, status, start, finish });
            }

            val1.setText(String.valueOf(renewal));
            val2.setText(String.valueOf(newCards));
            val3.setText(String.valueOf(cancelled));
            valPriServed.setText(String.valueOf(priorityServed));
            valNormServed.setText(String.valueOf(normalServed));
            valTotal.setText(String.valueOf(total));

            if (selectedQueueNo != null) {
                for (int i = 0; i < reportModel.getRowCount(); i++) {
                    if (reportModel.getValueAt(i, 0).toString().equals(selectedQueueNo)) {
                        reportTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
            applyReportFilters.run();
        };

        loadReport.run();
        applyReportFilters.run();
        reportFilterBox.addActionListener(e -> applyReportFilters.run());
        new Timer(1000, e -> loadReport.run()).start();

        btnExport.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Export feature will be added using Java I/O streams.");
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyReportFilters.run(); }
            public void removeUpdate(DocumentEvent e) { applyReportFilters.run(); }
            public void changedUpdate(DocumentEvent e) { applyReportFilters.run(); }
        });
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            applyReportFilters.run();
        });
    }

    // =========================== DASHBOARD PAGE ===========================
    
    // =========================== DASHBOARD PAGE ===========================
    static void openDashboardPage(JPanel dashboard, JFrame frame) {

        Color navy = new Color(49, 55, 114, 255);
        Color red = new Color(151, 44, 52, 255);
        Color borderCol = new Color(0, 0, 0, 80);

        Color cardBg = new Color(255, 255, 255, 235);
        Color softBg = new Color(238, 240, 255);
        dashboard.setBackground(softBg);

        // ===== Status strip =====
        JPanel statusStrip = new JPanel();
        statusStrip.setBounds(60, 55, 985, 55);
        statusStrip.setLayout(null);
        statusStrip.setBackground(cardBg);
        statusStrip.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        dashboard.add(statusStrip);

        JLabel lblQueueStatusTitle = new JLabel("QUEUE STATUS:");
        lblQueueStatusTitle.setBounds(18, 16, 140, 20);
        lblQueueStatusTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQueueStatusTitle.setForeground(new Color(0, 0, 0, 170));
        statusStrip.add(lblQueueStatusTitle);

        JLabel lblQueueStatusVal = new JLabel("CLOSE");
        lblQueueStatusVal.setBounds(150, 10, 120, 32);
        lblQueueStatusVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblQueueStatusVal.setForeground(red);
        statusStrip.add(lblQueueStatusVal);

        JLabel lblActiveCounters = new JLabel("Active Counters: 0/6");
        lblActiveCounters.setBounds(720, 14, 240, 24);
        lblActiveCounters.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblActiveCounters.setForeground(new Color(0, 0, 0, 170));
        lblActiveCounters.setHorizontalAlignment(SwingConstants.RIGHT);
        statusStrip.add(lblActiveCounters);

        // ===== Metric cards =====
        JPanel card1 = createMiniCard(dashboard, 60, 125, 315, 90, cardBg, borderCol, "WAITING (TOTAL)");
        JLabel valWaiting = getMiniCardValue(card1, navy);

        JPanel card2 = createMiniCard(dashboard, 395, 125, 315, 90, cardBg, borderCol, "SKIPPED (TOTAL)");
        JLabel valSkipped = getMiniCardValue(card2, navy);

        JPanel card3 = createMiniCard(dashboard, 730, 125, 315, 90, cardBg, borderCol, "SERVED TODAY");
        JLabel valServedToday = getMiniCardValue(card3, navy);

        // ===== Now Serving panels (fixed 6 slots to match monitor) =====
        String[] slots = new String[] { "C1","C2","C3","C4","C5","C6" };

        JPanel[] counterPanels = new JPanel[6];
        JLabel[] nowLbl = new JLabel[6];
        JLabel[] typeLbl = new JLabel[6];
        JLabel[] transLbl = new JLabel[6];

        // layout: 3 columns x 2 rows
        int startX = 60;
        int startY = 235;
        int w = 315;
        int h = 200;
        int gapX = 20;
        int gapY = 20;

        for (int i = 0; i < 6; i++) {
            int col = i % 3;
            int row = i / 3;

            int x = startX + col * (w + gapX);
            int y = startY + row * (h + gapY);

            counterPanels[i] = createServingPanel(dashboard, x, y, w, h, cardBg, borderCol, navy, slots[i]);
            nowLbl[i] = (JLabel) counterPanels[i].getClientProperty("now");
            typeLbl[i] = (JLabel) counterPanels[i].getClientProperty("type");
            transLbl[i] = (JLabel) counterPanels[i].getClientProperty("trans");
        }

        Runnable refresh = () -> {

            boolean openNow = queueManager.isQueueOpen();
            lblQueueStatusVal.setText(openNow ? "OPEN" : "CLOSE");
            lblQueueStatusVal.setForeground(openNow ? new Color(90, 200, 90) : red);

            // Active counters based on staff sessions
            int active = 0;
            for (int i = 0; i < 6; i++) {
                if (SessionManager.isCounterInUse(slots[i])) active++;
            }
            lblActiveCounters.setText("Active Counters: " + active + "/6");

            // Totals across all counters
            int waitingTotal = queueManager.CountTotalWaitingAll();
            int skippedTotal = queueManager.CountTotalSkippedAll();

            int servedTotal = 0;
            for (int i = 0; i < 6; i++) {
                servedTotal += queueManager.countServedToday(slots[i]);
            }

            valWaiting.setText(String.valueOf(waitingTotal));
            valSkipped.setText(String.valueOf(skippedTotal));
            valServedToday.setText(String.valueOf(servedTotal));

            // Now serving per counter slot
            for (int i = 0; i < 6; i++) {
                setServingLabels(slots[i], nowLbl[i], typeLbl[i], transLbl[i], navy);
            }
        };

        refresh.run();
        new Timer(1000, e -> refresh.run()).start();
    }

    // Small helpers for dashboard (keeps layout clean)
    static JPanel createMiniCard(JPanel parent, int x, int y, int w, int h, Color bg, Color border, String title) {

        JPanel card = new JPanel();
        card.setBounds(x, y, w, h);
        card.setLayout(null);
        card.setBackground(bg);
        card.setBorder(new MatteBorder(2, 2, 2, 2, border));
        parent.add(card);

        JLabel lbl = new JLabel(title);
        lbl.setBounds(18, 12, w - 36, 18);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(0, 0, 0, 170));
        card.add(lbl);

        return card;
    }

    static JLabel getMiniCardValue(JPanel card, Color navy) {
        JLabel val = new JLabel("0");
        val.setBounds(18, 28, card.getWidth() - 36, 48);
        val.setFont(new Font("Segoe UI", Font.BOLD, 34));
        val.setForeground(navy);
        card.add(val);
        return val;
    }

    static JPanel createServingPanel(JPanel parent, int x, int y, int w, int h,
                                    Color bg, Color borderCol, Color navy, String title) {

        JPanel panel = new JPanel();
        panel.setBounds(x, y, w, h);
        panel.setLayout(null);
        panel.setBackground(bg);
        panel.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        parent.add(panel);

        int headerH = 50;

        JPanel header = new JPanel();
        header.setBounds(0, 0, w, headerH);
        header.setLayout(null);
        header.setBackground(navy);
        panel.add(header);

        JLabel t = new JLabel(title);
        t.setBounds(16, 14, w - 32, 22);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.add(t);

        JLabel now = new JLabel("---");
        int nowY = headerH + 10;
        int nowH = Math.max(60, h - headerH - 60);
        now.setBounds(0, nowY, w, nowH);
        now.setForeground(navy);
        now.setFont(new Font("Segoe UI", Font.BOLD, 45));
        now.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(now);

        int typeY = h - 45;
        int transY = h - 25;

        JLabel type = new JLabel("Type: ---");
        type.setBounds(0, typeY, w, 18);
        type.setForeground(new Color(0, 0, 0, 170));
        type.setFont(new Font("Segoe UI", Font.BOLD, 13));
        type.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(type);

        JLabel trans = new JLabel("Transaction: ---");
        trans.setBounds(0, transY, w, 18);
        trans.setForeground(new Color(0, 0, 0, 170));
        trans.setFont(new Font("Segoe UI", Font.BOLD, 13));
        trans.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(trans);

        panel.putClientProperty("now", now);
        panel.putClientProperty("type", type);
        panel.putClientProperty("trans", trans);

        return panel;
    }

    static void setServingLabels(String counterCode, JLabel lblNow, JLabel lblType, JLabel lblTrans, Color navy) {

        String[] row = queueManager.getServingRowForCounter(counterCode);
        if (row == null) {
            lblNow.setText("---");
            lblType.setText("Type: ---");
            lblTrans.setText("Transaction: ---");
            lblNow.setForeground(navy);
            return;
        }

        String qno = row[queueManager.COL_QUEUE_NO];
        String type = row[queueManager.COL_TYPE];
        String trans = row[queueManager.COL_TRANS];

        lblNow.setText(qno == null ? "---" : qno);
        lblType.setText("Type: " + (type == null ? "---" : type.toUpperCase()));
        lblTrans.setText("Transaction: " + (trans == null ? "---" : trans.toUpperCase()));
        lblNow.setForeground(navy);
    }

    // =========================== STAFF PAGE ===========================
    static void openStaffPage(JPanel staff, JFrame frame) {
        
        Color navy = new Color(49, 55, 114, 255);
        Color borderCol = new Color(0, 0, 0, 80);
        
        Color staffbg = new Color(238, 240, 255);
        staff.setBackground(staffbg);

        JLabel title = new JLabel("STAFF MANAGEMENT");
        title.setBounds(60, 20, 400, 28);
        title.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 18));
        title.setForeground(Color.BLACK);
        staff.add(title);

        JPanel searchBar = new JPanel();
        searchBar.setBounds(50, 60, 1000, 45);
        searchBar.setLayout(null);
        searchBar.setBackground(new Color(255, 255, 255, 220));
        searchBar.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        staff.add(searchBar);

        JLabel searchLbl = new JLabel("SEARCH:");
        searchLbl.setBounds(20, 10, 80, 25);
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        searchBar.add(searchLbl);

        JTextField searchField = new JTextField();
        searchField.setBounds(95, 10, 260, 25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchBar.add(searchField);

        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(365, 10, 90, 25);
        btnClear.setFocusPainted(false);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClear.setBackground(navy);
        btnClear.setForeground(Color.WHITE);
        searchBar.add(btnClear);

        JPanel tableBg = new JPanel();
        tableBg.setBounds(50, 120, 1000, 380);
        tableBg.setLayout(null);
        tableBg.setBackground(new Color(125, 132, 208, 255));
        tableBg.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        staff.add(tableBg);

        DefaultTableModel staffModel = new DefaultTableModel();
        staffModel.addColumn("Role");
        staffModel.addColumn("Username");
        staffModel.addColumn("Password");
        staffModel.addColumn("Counter");

        JTable staffTable = new JTable(staffModel);
        staffTable.setRowHeight(30);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        staffTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(staffTable);
        sp.setBounds(0, 0, 1000, 380);
        sp.setBorder(null);
        tableBg.add(sp);

        Runnable loadStaff = () -> {

            String selectedUser = null;
            int vr = staffTable.getSelectedRow();
            if (vr != -1) {
                int mr = staffTable.convertRowIndexToModel(vr);
                selectedUser = staffModel.getValueAt(mr, 1).toString();
            }

            staffModel.setRowCount(0);

            for (String[] r : AccountManager.getStaffRows()) {
                staffModel.addRow(r);
            }

            if (selectedUser != null) {
                for (int i = 0; i < staffModel.getRowCount(); i++) {
                    if (staffModel.getValueAt(i, 1).toString().equalsIgnoreCase(selectedUser)) {
                        staffTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        };

        loadStaff.run();
        new Timer(1000, e -> loadStaff.run()).start();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(staffModel);
        staffTable.setRowSorter(sorter);

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = searchField.getText().trim();
                if (text.isEmpty())
                    sorter.setRowFilter(null);
                else
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
        });

        btnClear.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        JPanel btnBar = new JPanel();
        btnBar.setBounds(50, 515, 1000, 55);
        btnBar.setLayout(null);
        btnBar.setBackground(new Color(255, 255, 255, 220));
        btnBar.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        staff.add(btnBar);

        JButton btnAdd = new JButton("Add Staff");
        btnAdd.setBounds(190, 10, 130, 35);
        btnAdd.setFocusPainted(false);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(navy);
        btnAdd.setForeground(Color.WHITE);
        btnBar.add(btnAdd);

        JButton btnEdit = new JButton("Edit Staff");
        btnEdit.setBounds(330, 10, 130, 35);
        btnEdit.setFocusPainted(false);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEdit.setBackground(navy);
        btnEdit.setForeground(Color.WHITE);
        btnBar.add(btnEdit);

        JButton btnAssign = new JButton("Assign Counter");
        btnAssign.setBounds(470, 10, 150, 35);
        btnAssign.setFocusPainted(false);
        btnAssign.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAssign.setBackground(navy);
        btnAssign.setForeground(Color.WHITE);
        btnBar.add(btnAssign);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(630, 10, 130, 35);
        btnDelete.setFocusPainted(false);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(navy);
        btnDelete.setForeground(Color.WHITE);
        btnBar.add(btnDelete);

        JButton btnCounters = new JButton("Manage Counters");
        btnCounters.setBounds(770, 10, 200, 35);
        btnCounters.setFocusPainted(false);
        btnCounters.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCounters.setBackground(navy);
        btnCounters.setForeground(Color.WHITE);
        btnBar.add(btnCounters);

        btnCounters.addActionListener(e -> openCounterManager(frame));

        btnAdd.addActionListener(e -> {
            JTextField u = new JTextField();

            JPanel panel = new JPanel(new java.awt.GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("Username:"));
            panel.add(u);

            int opt = JOptionPane.showConfirmDialog(frame, panel, "Add Staff", JOptionPane.OK_CANCEL_OPTION);
            if (opt != JOptionPane.OK_OPTION) return;

            String gen = AccountManager.addStaffAuto(u.getText());
            if (gen == null) {
                JOptionPane.showMessageDialog(frame, "Add failed (username exists or invalid).");
                return;
            }

            loadStaff.run();
            JOptionPane.showMessageDialog(frame,
                    "Staff added!\n\nTemporary Password: " + gen + "\n\n(Staff can change it later in Profile.)");
        });

        btnEdit.addActionListener(e -> {
            int vr = staffTable.getSelectedRow();
            if (vr == -1) {
                JOptionPane.showMessageDialog(frame, "Select a staff row first.");
                return;
            }
            int mr = staffTable.convertRowIndexToModel(vr);

            String oldUser = staffModel.getValueAt(mr, 1).toString();
            String oldPass = staffModel.getValueAt(mr, 2).toString();

            JTextField newU = new JTextField(oldUser);
            JTextField newP = new JTextField(oldPass);

            JPanel panel = new JPanel(new java.awt.GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("New Username:"));
            panel.add(newU);
            panel.add(new JLabel("New Password:"));
            panel.add(newP);

            int opt = JOptionPane.showConfirmDialog(frame, panel, "Edit Staff", JOptionPane.OK_CANCEL_OPTION);
            if (opt != JOptionPane.OK_OPTION)
                return;

            if (!AccountManager.updateStaff(oldUser, newU.getText(), newP.getText())) {
                JOptionPane.showMessageDialog(frame, "Edit failed (duplicate username or invalid).");
                return;
            }
            loadStaff.run();
            JOptionPane.showMessageDialog(frame, "Staff updated!");
        });

        btnAssign.addActionListener(e -> {
            int vr = staffTable.getSelectedRow();
            if (vr == -1) {
                JOptionPane.showMessageDialog(frame, "Select a staff row first.");
                return;
            }
            int mr = staffTable.convertRowIndexToModel(vr);
            String user = staffModel.getValueAt(mr, 1).toString();

            CounterManager.initialize();
            java.util.List<String> counterList = CounterManager.getCounters();
            if (counterList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No counters found.\nUse 'Manage Counters' first.");
                return;
            }

            String[] counters = counterList.toArray(new String[0]);
            String selected = (String) JOptionPane.showInputDialog(frame, "Assign counter for: " + user, "Assign Counter",
                    JOptionPane.PLAIN_MESSAGE, null, counters, counters[0]);

            if (selected == null)
                return;

            if (!AccountManager.assignCounter(user, selected)) {
                JOptionPane.showMessageDialog(frame, "Assign failed.");
                return;
            }
            loadStaff.run();
            JOptionPane.showMessageDialog(frame, "Assigned to " + selected);
        });

        btnDelete.addActionListener(e -> {
            int vr = staffTable.getSelectedRow();
            if (vr == -1) {
                JOptionPane.showMessageDialog(frame, "Select a staff row first.");
                return;
            }
            int mr = staffTable.convertRowIndexToModel(vr);
            String user = staffModel.getValueAt(mr, 1).toString();

            int opt = JOptionPane.showConfirmDialog(frame, "Delete staff: " + user + " ?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (opt != JOptionPane.YES_OPTION)
                return;

            if (!AccountManager.deleteStaff(user)) {
                JOptionPane.showMessageDialog(frame, "Delete failed.");
                return;
            }
            loadStaff.run();
            JOptionPane.showMessageDialog(frame, "Staff deleted!");
        });
    }

    // ======================= COUNTER MANAGEMENT (MAX 6) =======================
    // Minimalist dialog flow (keeps your UI style and avoids redesigning panels)
    static void openCounterManager(JFrame frame) {

        CounterManager.initialize();

        String[] actions = { "Add Counter", "Delete Counter", "Set Lane", "Close" };

        while (true) {

            java.util.Map<String, String> map = CounterManager.loadAll();
            StringBuilder sb = new StringBuilder();
            sb.append("ACTIVE COUNTERS (max ").append(CounterManager.MAX_COUNTERS).append(")\n\n");

            if (map.isEmpty()) {
                sb.append("---\n");
            } else {
                for (java.util.Map.Entry<String, String> e : map.entrySet()) {
                    sb.append(e.getKey()).append("   -   ").append(e.getValue()).append("\n");
                }
            }

            int choice = JOptionPane.showOptionDialog(
                    frame,
                    sb.toString(),
                    "Manage Counters",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    actions,
                    actions[0]
            );

            if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) {
                break;
            }

            if (choice == 0) {
                // Add Counter
                String[] lanes = { "NORMAL", "PRIORITY", "HYBRID" };
                JComboBox<String> laneBox = new JComboBox<>(lanes);

                JPanel p = new JPanel(new java.awt.GridLayout(0, 1, 5, 5));
                p.add(new JLabel("Lane for new counter:"));
                p.add(laneBox);

                int ok = JOptionPane.showConfirmDialog(frame, p, "Add Counter", JOptionPane.OK_CANCEL_OPTION);
                if (ok != JOptionPane.OK_OPTION) continue;

                String created = CounterManager.addCounter(laneBox.getSelectedItem().toString());
                if (created == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Maximum of " + CounterManager.MAX_COUNTERS + " counters reached.\n" +
                            "(Monitor screen is limited to 6 counters.)");
                } else {
                    JOptionPane.showMessageDialog(frame, "Added: " + created);
                }
            }

            if (choice == 1) {
                // Delete Counter
                java.util.List<String> counters = CounterManager.getCounters();
                if (counters.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No counters to delete.");
                    continue;
                }

                JComboBox<String> counterBox = new JComboBox<>(counters.toArray(new String[0]));
                JPanel p = new JPanel(new java.awt.GridLayout(0, 1, 5, 5));
                p.add(new JLabel("Select counter to delete:"));
                p.add(counterBox);

                int ok = JOptionPane.showConfirmDialog(frame, p, "Delete Counter", JOptionPane.OK_CANCEL_OPTION);
                if (ok != JOptionPane.OK_OPTION) continue;

                String c = counterBox.getSelectedItem().toString();
                int conf = JOptionPane.showConfirmDialog(frame,
                        "Delete " + c + "?\n\nNote: Staff assigned to this counter should be reassigned.",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (conf != JOptionPane.YES_OPTION) continue;

                if (!CounterManager.deleteCounter(c)) {
                    JOptionPane.showMessageDialog(frame, "Delete failed.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Deleted: " + c);
                }
            }

            if (choice == 2) {
                // Set Lane
                java.util.List<String> counters = CounterManager.getCounters();
                if (counters.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No counters available.");
                    continue;
                }

                JComboBox<String> counterBox = new JComboBox<>(counters.toArray(new String[0]));
                String[] lanes = { "NORMAL", "PRIORITY", "HYBRID" };
                JComboBox<String> laneBox = new JComboBox<>(lanes);

                // preselect current lane
                String current = CounterManager.getLane(counterBox.getSelectedItem().toString());
                laneBox.setSelectedItem(current);
                counterBox.addActionListener(ev -> {
                    String c = counterBox.getSelectedItem().toString();
                    laneBox.setSelectedItem(CounterManager.getLane(c));
                });

                JPanel p = new JPanel(new java.awt.GridLayout(0, 1, 5, 5));
                p.add(new JLabel("Select counter:"));
                p.add(counterBox);
                p.add(new JLabel("Lane:"));
                p.add(laneBox);

                int ok = JOptionPane.showConfirmDialog(frame, p, "Set Lane", JOptionPane.OK_CANCEL_OPTION);
                if (ok != JOptionPane.OK_OPTION) continue;

                String c = counterBox.getSelectedItem().toString();
                String lane = laneBox.getSelectedItem().toString();

                if (!CounterManager.setLane(c, lane)) {
                    JOptionPane.showMessageDialog(frame, "Update failed.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Updated " + c + " lane to " + lane + ".");
                }
            }
        }
    }

    public static void main(String[] args) {
        AccountManager.initialize();
        CounterManager.initialize();
        admin();
    }
}
