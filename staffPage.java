import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class staffPage {

    private static String loggedInUser;

    // assignment (Admin can change while staff window is open)
    static String assignedCounter = "-";          // e.g. "C1".."C6" or "-"
    static String laneText = "NO COUNTER ASSIGNED";

    // dashboard labels
    static JLabel lblCounterStatus;
    static JLabel lblQueueStatus;
    static JLabel lblNowServingNo;
    static JLabel lblNowServingTrans;
    static JLabel lblNowServingType;

    static JLabel lblWaitingCount;
    static JLabel lblSkippedCount;
    static JLabel lblServedTodayCount;

    // show assignment + lane inside dashboard card (auto-updates)
    static JLabel lblAssignedCounterVal;
    static JLabel lblLaneVal;

    // counter-page refresh hooks (so we can use ONE timer only)
    static Runnable rLoadCounterTables;
    static Runnable rRefreshCounterLabels;

    // ----------------- Small helpers -----------------
    private static void setTextIfChanged(JLabel lbl, String newText) {
        if (lbl == null) return;
        String old = lbl.getText();
        if (old == null || !old.equals(newText)) {
            lbl.setText(newText);
        }
    }

    // Normalize whatever is stored in accounts.txt to "C#"
    private static String normalizeCounterCode(String counter) {
        if (counter == null) return null;
        String c = counter.trim().toUpperCase();

        if (c.isEmpty() || c.equals("-") || c.equals("NONE")) return null;

        // "1" -> "C1"
        if (c.matches("^\\d+$")) return "C" + c;

        // already "C1"
        if (c.matches("^C\\d+$")) return c;

        // fallback: extract digits
        String digits = c.replaceAll("\\D+", "");
        if (!digits.isEmpty()) return "C" + digits;

        return null;
    }

    private static String buildLaneText(String assignedCounter) {
        String code = normalizeCounterCode(assignedCounter);
        if (code == null) return "NO COUNTER ASSIGNED";

        // If admin deleted the counter, treat as unassigned
        if (!CounterManager.hasCounter(code)) return "NO COUNTER ASSIGNED";

        String lane = CounterManager.getLane(code);
        if (CounterManager.LANE_NORMAL.equals(lane)) return "NORMAL";
        if (CounterManager.LANE_PRIORITY.equals(lane)) return "PRIORITY";
        if (CounterManager.LANE_HYBRID.equals(lane)) return "HYBRID";
        return "HYBRID";
    }

    private static String buildHeaderText(String assignedCounter) {
        String code = normalizeCounterCode(assignedCounter);
        if (code == null || !CounterManager.hasCounter(code)) {
            return "NO COUNTER ASSIGNED";
        }
        return code + " | " + buildLaneText(assignedCounter) + " LANE";
    }

    private static String getCounterCode() {
        String code = normalizeCounterCode(assignedCounter);
        if (code == null) return null;
        if (!CounterManager.hasCounter(code)) return null;
        return code;
    }

    // =========================================================
    // MAIN STAFF WINDOW
    // =========================================================
    public static void staff(String username, String role) {

        loggedInUser = username;

        // Make sure counters exist
        CounterManager.initialize();

        // Load assigned counter from accounts.txt (Admin assigns this)
        assignedCounter = AccountManager.getAssignedCounter(username);
        if (assignedCounter == null || assignedCounter.trim().isEmpty()) assignedCounter = "-";
        assignedCounter = (normalizeCounterCode(assignedCounter) == null) ? "-" : normalizeCounterCode(assignedCounter);

        laneText = buildLaneText(assignedCounter);

        JFrame frame = new JFrame("Staff Page");
        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);

        // Release the counter reservation if this window is closed
        final String _user = username;
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                SessionManager.endSession(_user, getCounterCode());
            }
        });

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

        JButton btn2 = new JButton("My Counter");
        btn2.setBounds(50, 300, 200, 50);
        btn2.setFocusPainted(false);
        btn2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn2.setForeground(new Color(49, 55, 114, 255));
        leftPanel.add(btn2);

        
        JButton btn3 = new JButton("Profile");
        btn3.setBounds(50, 580, 200, 50);
        btn3.setFocusPainted(false);
        btn3.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn3.setForeground(new Color(49, 55, 114, 255));
        leftPanel.add(btn3);

JButton btn4 = new JButton("Sign Out");
        btn4.setBounds(70, 650, 150, 50);
        btn4.setFocusPainted(false);
        btn4.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn4.setForeground(new Color(49, 55, 114, 255));
        leftPanel.add(btn4);

        // ===== TOP RED PANEL =====
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

        // must be final to allow update inside lambda
        final JLabel pagetitle = new JLabel("");
        pagetitle.setBounds(350, 20, 400, 24);
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
        clockTimer.setCoalesce(true);
        clockTimer.start();

        // ===== MAIN PANEL (CardLayout) =====
        CardLayout card = new CardLayout();
        JPanel mainPanel = new JPanel(card);
        mainPanel.setBounds(300, 60, 1100, 703);
        mainPanel.setBorder(new MatteBorder(0, 0, 6, 0, new Color(0, 0, 0, 90)));
        frame.add(mainPanel);

        JPanel dashboard = new JPanel();
        dashboard.setLayout(null);
        dashboard.setBackground(new Color(255, 255, 255, 200));
        openStaffDashboard(dashboard);

        JPanel counter = new JPanel();
        counter.setLayout(null);
        counter.setBackground(new Color(255, 255, 255, 200));
        openCounterPage(counter, frame);

        JPanel profile = new JPanel();
profile.setLayout(null);
profile.setBackground(new Color(255, 255, 255, 200));
openProfilePage(profile, frame);   // <-- NEW

        mainPanel.add(dashboard, "DASHBOARD");
        mainPanel.add(counter, "COUNTER");
        mainPanel.add(profile, "PROFILE"); // <-- NEW

        // Track active page to prevent table reload flicker while on dashboard
        final String[] activePage = { "DASHBOARD" };

        // ===== DEFAULT PAGE =====
        card.show(mainPanel, "DASHBOARD");
        pagetitle.setText(buildHeaderText(assignedCounter));

        btn1.addActionListener(e -> {
            activePage[0] = "DASHBOARD";
            card.show(mainPanel, "DASHBOARD");
            pagetitle.setText(buildHeaderText(assignedCounter));
        });

        btn2.addActionListener(e -> {
            activePage[0] = "COUNTER";
            card.show(mainPanel, "COUNTER");
            pagetitle.setText(buildHeaderText(assignedCounter));
        });
        btn3.addActionListener(e -> {
    activePage[0] = "PROFILE";
    card.show(mainPanel, "PROFILE");
    pagetitle.setText("PROFILE");
});

        btn4.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to sign out?",
                    "Sign Out",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                SessionManager.endSession(loggedInUser, getCounterCode());
                frame.dispose();
                IQueue.login();
            }
        });

        

// =========================================================
        // DASHBOARD REFRESH (labels only)  ✅ no table rebuilding here
        // =========================================================
        Runnable refreshDashboard = () -> {

            // 1) Auto-update assignment if Admin changes it while window is open
            String latest = AccountManager.getAssignedCounter(loggedInUser);
            latest = (normalizeCounterCode(latest) == null) ? "-" : normalizeCounterCode(latest);

            if (!latest.equals(assignedCounter)) {
                // release old session reservation if counter changed
                SessionManager.endSession(loggedInUser, getCounterCode());

                assignedCounter = latest;
                laneText = buildLaneText(assignedCounter);

                pagetitle.setText(buildHeaderText(assignedCounter));

                if (lblAssignedCounterVal != null) setTextIfChanged(lblAssignedCounterVal, (getCounterCode() == null) ? "Counter -" : getCounterCode());
                if (lblLaneVal != null) setTextIfChanged(lblLaneVal, laneText);
            } else {
                // lane might change even if counter stays
                String updatedLane = buildLaneText(assignedCounter);
                if (!updatedLane.equals(laneText)) {
                    laneText = updatedLane;
                    pagetitle.setText(buildHeaderText(assignedCounter));
                    if (lblLaneVal != null) setTextIfChanged(lblLaneVal, laneText);
                }
            }

            // 2) Queue open/close (always live)
            boolean openNow = queueManager.isQueueOpen();
            setTextIfChanged(lblQueueStatus, openNow ? "OPEN" : "CLOSE");
            if (lblQueueStatus != null) {
                lblQueueStatus.setForeground(openNow ? new Color(90, 200, 90) : new Color(220, 60, 60));
            }

            // 3) Counter-specific stats
            String counterCode = getCounterCode();

            if (counterCode != null) {

                setTextIfChanged(lblWaitingCount, String.valueOf(queueManager.countWaitingEligible(counterCode)));
                setTextIfChanged(lblSkippedCount, String.valueOf(queueManager.countSkippedEligible(counterCode)));
                setTextIfChanged(lblServedTodayCount, String.valueOf(queueManager.countServedToday(counterCode)));

                String[] serving = queueManager.getServingRowForCounter(counterCode);
                if (serving == null) {
                    setTextIfChanged(lblNowServingNo, "---");
                    setTextIfChanged(lblNowServingTrans, "---");
                    setTextIfChanged(lblNowServingType, "---");
                } else {
                    setTextIfChanged(lblNowServingNo, serving[queueManager.COL_QUEUE_NO]);
                    setTextIfChanged(lblNowServingTrans, serving[queueManager.COL_TRANS]);
                    setTextIfChanged(lblNowServingType, serving[queueManager.COL_TYPE]);
                }

            } else {
                setTextIfChanged(lblWaitingCount, "0");
                setTextIfChanged(lblSkippedCount, "0");
                setTextIfChanged(lblServedTodayCount, "0");
                setTextIfChanged(lblNowServingNo, "---");
                setTextIfChanged(lblNowServingTrans, "---");
                setTextIfChanged(lblNowServingType, "---");
            }

            // Counter status (ACTIVE/IDLE)
            boolean isIdle = (lblNowServingNo == null) || lblNowServingNo.getText().trim().equals("---");
            setTextIfChanged(lblCounterStatus, isIdle ? "IDLE" : "ACTIVE");
            if (lblCounterStatus != null) {
                lblCounterStatus.setForeground(isIdle ? new Color(220, 60, 60) : new Color(90, 200, 90));
            }
        };

        // =========================================================
        // ONE TIMER ONLY ✅ (fixes flicker)
        // =========================================================
        Timer uiTimer = new Timer(500, e -> {
            if ("DASHBOARD".equals(activePage[0])) {
                refreshDashboard.run();
            } else {
                if (rLoadCounterTables != null) rLoadCounterTables.run();
                if (rRefreshCounterLabels != null) rRefreshCounterLabels.run();
            }
        });
        uiTimer.setInitialDelay(0);
        uiTimer.setCoalesce(true);
        uiTimer.start();

        refreshDashboard.run();

        frame.setVisible(true);

        if ("-".equals(assignedCounter) || getCounterCode() == null) {
            JOptionPane.showMessageDialog(frame,
                    "Your account has no counter assigned yet.\nAsk admin to assign you a counter (C1 to C6).",
                    "No Counter Assigned",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // =========================================================
    // STAFF DASHBOARD UI
    // =========================================================
    static void openStaffDashboard(JPanel dashboard) {

        Color navy = new Color(49, 55, 114, 255);
        Color borderCol = new Color(0, 0, 0, 80);
        Color cardBg = new Color(205, 208, 245);

        // ===== 1) COUNTER STATUS CARD =====
        JPanel statusCard = new JPanel();
        statusCard.setBounds(60, 30, 985, 110);
        statusCard.setLayout(null);
        statusCard.setBackground(cardBg);
        statusCard.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        dashboard.add(statusCard);

        JLabel title = new JLabel("COUNTER STATUS");
        title.setBounds(18, 12, 250, 18);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusCard.add(title);

        JLabel cLbl = new JLabel("Counter:");
        cLbl.setBounds(18, 45, 80, 20);
        cLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusCard.add(cLbl);

        lblAssignedCounterVal = new JLabel((getCounterCode() == null) ? "Counter -" : getCounterCode());
        lblAssignedCounterVal.setBounds(80, 45, 160, 20);
        lblAssignedCounterVal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusCard.add(lblAssignedCounterVal);

        JLabel laneLbl = new JLabel("Lane:");
        laneLbl.setBounds(260, 45, 60, 20);
        laneLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusCard.add(laneLbl);

        lblLaneVal = new JLabel(laneText);
        lblLaneVal.setBounds(310, 45, 220, 20);
        lblLaneVal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusCard.add(lblLaneVal);

        JLabel statLbl = new JLabel("Status:");
        statLbl.setBounds(540, 45, 70, 20);
        statLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusCard.add(statLbl);

        lblCounterStatus = new JLabel("IDLE");
        lblCounterStatus.setBounds(595, 45, 120, 20);
        lblCounterStatus.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 12));
        lblCounterStatus.setForeground(new Color(220, 60, 60));
        statusCard.add(lblCounterStatus);

        JLabel qStatLbl = new JLabel("Queue Status:");
        qStatLbl.setBounds(720, 45, 100, 20);
        qStatLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusCard.add(qStatLbl);

        boolean openNow = queueManager.isQueueOpen();
        lblQueueStatus = new JLabel(openNow ? "OPEN" : "CLOSE");
        lblQueueStatus.setBounds(810, 45, 120, 20);
        lblQueueStatus.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 12));
        lblQueueStatus.setForeground(openNow ? new Color(90, 200, 90) : new Color(220, 60, 60));
        statusCard.add(lblQueueStatus);

        // ===== 2) NOW SERVING SUMMARY =====
        JPanel nowServingCard = new JPanel();
        nowServingCard.setBounds(60, 160, 985, 150);
        nowServingCard.setLayout(null);
        nowServingCard.setBackground(new Color(255, 255, 255, 255));
        nowServingCard.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        dashboard.add(nowServingCard);

        JLabel nsTitle = new JLabel("NOW SERVING");
        nsTitle.setBounds(18, 12, 250, 20);
        nsTitle.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        nsTitle.setForeground(navy);
        nowServingCard.add(nsTitle);

        lblNowServingNo = new JLabel("---");
        lblNowServingNo.setBounds(18, 35, 650, 55);
        lblNowServingNo.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 44));
        lblNowServingNo.setForeground(new Color(151, 44, 52, 255));
        nowServingCard.add(lblNowServingNo);

        JLabel tLbl = new JLabel("Transaction:");
        tLbl.setBounds(18, 98, 90, 20);
        tLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nowServingCard.add(tLbl);

        lblNowServingTrans = new JLabel("---");
        lblNowServingTrans.setBounds(105, 98, 250, 20);
        lblNowServingTrans.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nowServingCard.add(lblNowServingTrans);

        JLabel tyLbl = new JLabel("Type:");
        tyLbl.setBounds(400, 98, 45, 20);
        tyLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nowServingCard.add(tyLbl);

        lblNowServingType = new JLabel("---");
        lblNowServingType.setBounds(440, 98, 200, 20);
        lblNowServingType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nowServingCard.add(lblNowServingType);

        // ===== 3) COUNTS FOR THIS COUNTER =====
        JPanel card1 = new JPanel();
        card1.setBounds(60, 340, 315, 85);
        card1.setLayout(null);
        card1.setBackground(cardBg);
        card1.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        dashboard.add(card1);

        JLabel lbl1 = new JLabel("WAITING IN LINE");
        lbl1.setBounds(18, 12, 250, 18);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card1.add(lbl1);

        lblWaitingCount = new JLabel("0");
        lblWaitingCount.setBounds(18, 32, 250, 45);
        lblWaitingCount.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        lblWaitingCount.setForeground(navy);
        card1.add(lblWaitingCount);

        JPanel card2 = new JPanel();
        card2.setBounds(395, 340, 315, 85);
        card2.setLayout(null);
        card2.setBackground(cardBg);
        card2.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        dashboard.add(card2);

        JLabel lbl2 = new JLabel("SKIPPED (NOT RECALLED)");
        lbl2.setBounds(18, 12, 250, 18);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card2.add(lbl2);

        lblSkippedCount = new JLabel("0");
        lblSkippedCount.setBounds(18, 32, 250, 45);
        lblSkippedCount.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        lblSkippedCount.setForeground(navy);
        card2.add(lblSkippedCount);

        JPanel card3 = new JPanel();
        card3.setBounds(730, 340, 315, 85);
        card3.setLayout(null);
        card3.setBackground(cardBg);
        card3.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        dashboard.add(card3);

        JLabel lbl3 = new JLabel("SERVED TODAY");
        lbl3.setBounds(18, 12, 250, 18);
        lbl3.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card3.add(lbl3);

        lblServedTodayCount = new JLabel("0");
        lblServedTodayCount.setBounds(18, 32, 250, 45);
        lblServedTodayCount.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 30));
        lblServedTodayCount.setForeground(navy);
        card3.add(lblServedTodayCount);
    }

    // =========================================================
    // MY COUNTER PAGE
    // =========================================================
    static void openCounterPage(JPanel counter, JFrame frame) {

        Color navy = new Color(49, 55, 114, 255);
        Color borderCol = new Color(0, 0, 0, 80);

        JLabel header = new JLabel("MY COUNTER - QUEUE LIST (READ-ONLY)");
        header.setBounds(60, 20, 500, 25);
        header.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
        counter.add(header);

        JPanel tableBg = new JPanel();
        tableBg.setBounds(50, 60, 650, 440);
        tableBg.setLayout(null);
        tableBg.setBackground(new Color(125, 132, 208, 255));
        tableBg.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        counter.add(tableBg);

        DefaultTableModel queueModel = new DefaultTableModel(
                new Object[] { "Queue No.", "Transaction", "Type", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable queueTable = new JTable(queueModel);
        queueTable.setRowHeight(28);
        queueTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        queueTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane queueScroll = new JScrollPane(queueTable);
        queueScroll.setBounds(0, 0, 650, 440);
        queueScroll.setBorder(null);
        tableBg.add(queueScroll);

        JPanel skippedBg = new JPanel();
        skippedBg.setBounds(720, 60, 330, 440);
        skippedBg.setLayout(null);
        skippedBg.setBackground(new Color(186, 190, 240));
        skippedBg.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        counter.add(skippedBg);

        JLabel skippedLbl = new JLabel("SKIPPED / HOLD LIST");
        skippedLbl.setBounds(10, 8, 250, 18);
        skippedLbl.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 12));
        skippedBg.add(skippedLbl);

        DefaultTableModel skippedModel = new DefaultTableModel(
                new Object[] { "Queue No.", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable skippedTable = new JTable(skippedModel);
        skippedTable.setRowHeight(26);
        skippedTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        skippedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        skippedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane skippedScroll = new JScrollPane(skippedTable);
        skippedScroll.setBounds(0, 30, 330, 410);
        skippedScroll.setBorder(null);
        skippedBg.add(skippedScroll);

        JButton btnDone = new JButton("Done");
        btnDone.setBounds(200, 530, 120, 35);
        btnDone.setFocusPainted(false);
        btnDone.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDone.setBackground(navy);
        btnDone.setForeground(Color.WHITE);
        counter.add(btnDone);

        JButton btnCallNext = new JButton("Call Next");
        btnCallNext.setBounds(330, 530, 120, 35);
        btnCallNext.setFocusPainted(false);
        btnCallNext.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCallNext.setBackground(navy);
        btnCallNext.setForeground(Color.WHITE);
        counter.add(btnCallNext);

        JButton btnSkip = new JButton("Skip");
        btnSkip.setBounds(460, 530, 120, 35);
        btnSkip.setFocusPainted(false);
        btnSkip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSkip.setBackground(navy);
        btnSkip.setForeground(Color.WHITE);
        counter.add(btnSkip);

        JButton btnCallAgain = new JButton("Call Again");
        btnCallAgain.setBounds(590, 530, 120, 35);
        btnCallAgain.setFocusPainted(false);
        btnCallAgain.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCallAgain.setBackground(navy);
        btnCallAgain.setForeground(Color.WHITE);
        counter.add(btnCallAgain);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(720, 530, 120, 35);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(navy);
        btnCancel.setForeground(Color.WHITE);
        counter.add(btnCancel);

        // =========================================================
        // Refresh functions (NO Timer here!)
        // =========================================================
        rLoadCounterTables = () -> {

            String counterCode = getCounterCode();

            String selectedQueueNo = null;
            int selectedRow = queueTable.getSelectedRow();
            if (selectedRow != -1 && queueModel.getRowCount() > 0) {
                selectedQueueNo = queueModel.getValueAt(selectedRow, 0).toString();
            }

            String selectedSkippedNo = null;
            int skippedRow = skippedTable.getSelectedRow();
            if (skippedRow != -1 && skippedModel.getRowCount() > 0) {
                selectedSkippedNo = skippedModel.getValueAt(skippedRow, 0).toString();
            }

            if (counterCode == null) {
                if (queueModel.getRowCount() != 0) queueModel.setRowCount(0);
                if (skippedModel.getRowCount() != 0) skippedModel.setRowCount(0);
                return;
            }

            queueModel.setRowCount(0);
            for (String[] r : queueManager.getRowsForCounterQueueTable(counterCode)) {
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

            skippedModel.setRowCount(0);
            for (String[] r : queueManager.getRowsForCounterSkippedTable(counterCode)) {
                skippedModel.addRow(r);
            }

            if (selectedSkippedNo != null) {
                for (int i = 0; i < skippedModel.getRowCount(); i++) {
                    if (skippedModel.getValueAt(i, 0).toString().equals(selectedSkippedNo)) {
                        skippedTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        };

        rRefreshCounterLabels = () -> { /* no extra labels here */ };

        // =========================================================
        // Buttons
        // =========================================================
        btnCallNext.addActionListener(e -> {

            if (!queueManager.isQueueOpen()) {
                JOptionPane.showMessageDialog(frame, "Queue is CLOSED. Ask admin to open queue.");
                return;
            }

            String counterCode = getCounterCode();
            if (counterCode == null) {
                JOptionPane.showMessageDialog(frame, "No counter assigned. Ask admin to assign you.");
                return;
            }

            // Enforce "one serving at a time" per counter
            if (queueManager.getServingRowForCounter(counterCode) != null) {
                JOptionPane.showMessageDialog(frame, "You are still serving. Mark DONE first.");
                return;
            }

            // Save who served (logged-in staff username)
            String[] served = queueManager.serveNextForCounter(counterCode, staffPage.loggedInUser);
            if (served == null) JOptionPane.showMessageDialog(frame, "No customers available for your lane.");

            if (rLoadCounterTables != null) rLoadCounterTables.run();
        });

        btnDone.addActionListener(e -> {

            int row = queueTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a queue first.");
                return;
            }

            String queueNo = queueModel.getValueAt(row, 0).toString();

            int opt = JOptionPane.showConfirmDialog(frame, "Mark as DONE?\n" + queueNo, "Confirm Done", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;

            if (!queueManager.markDone(queueNo)) {
                JOptionPane.showMessageDialog(frame, "Only 'Serving' queues can be marked as Done.");
            }

            if (rLoadCounterTables != null) rLoadCounterTables.run();
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

            if (rLoadCounterTables != null) rLoadCounterTables.run();
        });

        btnCallAgain.addActionListener(e -> {

            int row = skippedTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a skipped queue first.");
                return;
            }

            String queueNo = skippedModel.getValueAt(row, 0).toString();

            if (!queueManager.recallSkipped(queueNo)) {
                JOptionPane.showMessageDialog(frame, "Recall failed.");
            }

            if (rLoadCounterTables != null) rLoadCounterTables.run();
        });

        btnCancel.addActionListener(e -> {

            int row = queueTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a queue first.");
                return;
            }

            String queueNo = queueModel.getValueAt(row, 0).toString();

            int opt = JOptionPane.showConfirmDialog(frame, "Cancel this queue?\n" + queueNo, "Confirm Cancel", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;

            if (!queueManager.cancelQueue(queueNo)) {
                JOptionPane.showMessageDialog(frame, "Cannot cancel this queue.");
            }

            if (rLoadCounterTables != null) rLoadCounterTables.run();
        });
    }

static void openProfilePage(JPanel profile, JFrame frame) {

    Color navy = new Color(49, 55, 114, 255);
    Color borderCol = new Color(0, 0, 0, 80);

    JLabel title = new JLabel("PROFILE");
    title.setBounds(60, 25, 300, 28);
    title.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 20));
    profile.add(title);

    // Main minimalist card
    JPanel card = new JPanel();
    card.setBounds(60, 80, 985, 420);
    card.setLayout(null);
    card.setBackground(new Color(255, 255, 255, 235));
    card.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
    profile.add(card);

    JLabel section = new JLabel("CHANGE PASSWORD");
    section.setBounds(30, 25, 300, 20);
    section.setFont(new Font("Segoe UI", Font.BOLD, 14));
    section.setForeground(navy);
    card.add(section);

    JLabel lblUser = new JLabel("Logged in as: " + (loggedInUser == null ? "" : loggedInUser));
    lblUser.setBounds(30, 55, 400, 18);
    lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblUser.setForeground(new Color(0, 0, 0, 170));
    card.add(lblUser);

    JLabel oldLbl = new JLabel("Old Password");
    oldLbl.setBounds(30, 105, 200, 18);
    oldLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
    card.add(oldLbl);

    JPasswordField oldPass = new JPasswordField();
    oldPass.setBounds(30, 128, 380, 35);
    oldPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    card.add(oldPass);

    JLabel newLbl = new JLabel("New Password");
    newLbl.setBounds(30, 180, 200, 18);
    newLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
    card.add(newLbl);

    JPasswordField newPass = new JPasswordField();
    newPass.setBounds(30, 203, 380, 35);
    newPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    card.add(newPass);

    JLabel confLbl = new JLabel("Confirm New Password");
    confLbl.setBounds(30, 255, 200, 18);
    confLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
    card.add(confLbl);

    JPasswordField confirm = new JPasswordField();
    confirm.setBounds(30, 278, 380, 35);
    confirm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    card.add(confirm);

    JButton btnSave = new JButton("SAVE CHANGES");
    btnSave.setBounds(30, 335, 180, 40);
    btnSave.setFocusPainted(false);
    btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btnSave.setBackground(navy);
    btnSave.setForeground(Color.WHITE);
    card.add(btnSave);

    JButton btnClear = new JButton("CLEAR");
    btnClear.setBounds(230, 335, 180, 40);
    btnClear.setFocusPainted(false);
    btnClear.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btnClear.setBackground(new Color(151, 44, 52, 255));
    btnClear.setForeground(Color.WHITE);
    card.add(btnClear);

    btnClear.addActionListener(e -> {
        oldPass.setText("");
        newPass.setText("");
        confirm.setText("");
        oldPass.requestFocus();
    });

    btnSave.addActionListener(e -> {
        String o = new String(oldPass.getPassword()).trim();
        String n = new String(newPass.getPassword()).trim();
        String c = new String(confirm.getPassword()).trim();

        if (o.isEmpty() || n.isEmpty() || c.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            return;
        }
        if (n.length() < 4) {
            JOptionPane.showMessageDialog(frame, "New password must be at least 4 characters.");
            return;
        }
        if (!n.equals(c)) {
            JOptionPane.showMessageDialog(frame, "New password and confirm password do not match.");
            return;
        }

        boolean ok = AccountManager.changePassword(loggedInUser, o, n);
        if (ok) {
            JOptionPane.showMessageDialog(frame, "Password updated successfully.");
            oldPass.setText("");
            newPass.setText("");
            confirm.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Password change failed.\nCheck your old password.");
        }
    });
}
}