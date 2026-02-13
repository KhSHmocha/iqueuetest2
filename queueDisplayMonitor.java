import java.awt.*;
import java.time.*;
import java.time.format.*;
import javax.swing.*;
import javax.swing.border.*;

public class queueDisplayMonitor {

    // Up to 6 counters on the monitor
    static JLabel[] nowLbl = new JLabel[6];
    static JLabel[] typeLbl = new JLabel[6];
    static JLabel[] transLbl = new JLabel[6];

    static JLabel skippedStrip;
    static JLabel announcementText;
    static JLabel nextInlineText;

    public static void monitor() {

        JFrame frame = new JFrame("I_QUEUE  -  QUEUE DISPLAY MONITOR");
        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);

        // ===== THEME =====
        Color navy = new Color(49, 55, 114, 255);
        Color red  = new Color(151, 44, 52, 255);
        Color borderCol = new Color(0, 0, 0, 80);

        Color softBg   = new Color(238, 240, 255);
        Color cardWhite = new Color(255, 255, 255, 245);
        Color mutedText = new Color(0, 0, 0, 140);

        // ===== ROOT BG =====
        JPanel bg = new JPanel();
        bg.setBounds(0, 0, 1400, 800);
        bg.setLayout(null);
        bg.setBackground(softBg);
        frame.add(bg);

        // ===== TOP BAR =====
        int TOP_H = 60;

        JPanel topPanel = new JPanel();
        topPanel.setBackground(red);
        topPanel.setBounds(0, 0, 1400, TOP_H);
        topPanel.setLayout(null);
        topPanel.setBorder(new MatteBorder(0, 0, 6, 0, new Color(0, 0, 0, 90)));
        bg.add(topPanel);

        JLabel logo = new JLabel();
        logo.setBounds(20, 6, 120, 48);
        try {
            ImageIcon icon = new ImageIcon("bg/logo1.png");
            Image img = icon.getImage().getScaledInstance(120, 48, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(img));
        } catch (Exception e) { /* ignore */ }
        topPanel.add(logo);

        JLabel title = new JLabel("QUEUE DISPLAY MONITOR");
        title.setBounds(0, 24, 1400, 20);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(title);

        JLabel dateLabel = new JLabel();
        dateLabel.setBounds(1080, 5, 300, 18);
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(dateLabel);

        JLabel timeLabel = new JLabel();
        timeLabel.setBounds(1080, 25, 300, 24);
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

        // =========================================================
        // PROFESSIONAL + MINIMALIST FILL LAYOUT (no cut-off)
        // =========================================================
        int W = 1400;
        int H = 800;

        int M = 50;          // outer margin
        int GAP = 12;        // vertical gap between blocks
        int innerW = W - (M * 2);
        int availableY = TOP_H + 12; // start content under top bar

        // Bottom blocks (fixed heights so it always fits cleanly)
        int SKIP_H = 38;
        int NEXT_H = 100;
        int ANN_H  = 120;

        int bottomTotal = ANN_H + GAP + NEXT_H + GAP + SKIP_H;

        // Remaining height for counters grid
        int countersAreaY = availableY;
        int countersAreaH = (H - M) - countersAreaY - GAP - bottomTotal; // ensures bottom strip visible

        // 3 columns x 2 rows
        int cols = 3;
        int rows = 2;

        int gapX = 18;
        int gapY = 14;

        int cardW = (innerW - (gapX * (cols - 1))) / cols;
        int cardH = (countersAreaH - (gapY * (rows - 1))) / rows;

        // If your screen is tight, keep a minimum
        cardH = Math.max(cardH, 150);

        // Font sizes scale a bit with cardH
        int fsNow = Math.max(34, Math.min(48, (int) (cardH * 0.30)));
        int fsTitle = 15;
        int fsMeta = 12;

        // ====== COUNTERS GRID ======
        for (int i = 0; i < 6; i++) {
            int col = i % cols;
            int row = i / cols;

            int x = M + col * (cardW + gapX);
            int y = countersAreaY + row * (cardH + gapY);

            JPanel cardP = createCard(bg, x, y, cardW, cardH, cardWhite, borderCol);

            int headH = 42;
            JPanel head = createStrip(cardP, 0, 0, cardW, headH, navy, borderCol);

            JLabel t = createStripTitle("COUNTER " + (i + 1));
            t.setBounds(0, 0, cardW, headH);
            t.setFont(new Font("Segoe UI", Font.BOLD, fsTitle));
            head.add(t);

            JLabel ns = new JLabel("NOW SERVING");
            ns.setBounds(0, headH + 6, cardW, 14);
            ns.setForeground(mutedText);
            ns.setFont(new Font("Segoe UI", Font.BOLD, 11));
            ns.setHorizontalAlignment(SwingConstants.CENTER);
            cardP.add(ns);

            nowLbl[i] = new JLabel("---");
            nowLbl[i].setBounds(0, headH + 22, cardW, (int)(cardH * 0.42));
            nowLbl[i].setForeground(navy);
            nowLbl[i].setFont(new Font("Segoe UI", Font.BOLD, fsNow));
            nowLbl[i].setHorizontalAlignment(SwingConstants.CENTER);
            cardP.add(nowLbl[i]);

            // meta rows anchored near bottom of card
            int metaY1 = cardH - 46;
            int metaY2 = cardH - 26;

            typeLbl[i] = new JLabel("Type: ---");
            typeLbl[i].setBounds(0, metaY1, cardW, 18);
            typeLbl[i].setForeground(new Color(0, 0, 0, 170));
            typeLbl[i].setFont(new Font("Segoe UI", Font.BOLD, fsMeta));
            typeLbl[i].setHorizontalAlignment(SwingConstants.CENTER);
            cardP.add(typeLbl[i]);

            transLbl[i] = new JLabel("Transaction: ---");
            transLbl[i].setBounds(0, metaY2, cardW, 18);
            transLbl[i].setForeground(new Color(0, 0, 0, 170));
            transLbl[i].setFont(new Font("Segoe UI", Font.BOLD, fsMeta));
            transLbl[i].setHorizontalAlignment(SwingConstants.CENTER);
            cardP.add(transLbl[i]);
        }

        // Where counters end (actual bottom y based on computed cardH)
        int countersBottom = countersAreaY + (rows * cardH) + ((rows - 1) * gapY);

        // ====== ANNOUNCEMENT ======
        int annY = countersBottom + GAP;

        JPanel annWrap = createCard(bg, M, annY, innerW, ANN_H, cardWhite, borderCol);

        JLabel annLbl = new JLabel("ANNOUNCEMENT:");
        annLbl.setBounds(18, 10, 220, 18);
        annLbl.setForeground(new Color(0, 0, 0, 200));
        annLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        annWrap.add(annLbl);

        JPanel annBar = new JPanel();
        annBar.setBounds(18, 34, innerW - 36, ANN_H - 46);
        annBar.setLayout(null);
        annBar.setBackground(navy);
        annBar.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        annWrap.add(annBar);

        announcementText = new JLabel("PLEASE WAIT FOR YOUR NUMBER");
        announcementText.setBounds(0, 0, annBar.getWidth(), annBar.getHeight());
        announcementText.setForeground(Color.WHITE);
        announcementText.setFont(new Font("Segoe UI", Font.BOLD, 32));
        announcementText.setHorizontalAlignment(SwingConstants.CENTER);
        annBar.add(announcementText);

        // ====== NEXT IN LINE ======
        int nextY = annY + ANN_H + GAP;

        JPanel nextWrap = createCard(bg, M, nextY, innerW, NEXT_H, cardWhite, borderCol);

        JLabel nextLbl = new JLabel("NEXT IN LINE:");
        nextLbl.setBounds(18, 10, 220, 18);
        nextLbl.setForeground(new Color(0, 0, 0, 200));
        nextLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nextWrap.add(nextLbl);

        JPanel nextBar = new JPanel();
        nextBar.setBounds(18, 34, innerW - 36, NEXT_H - 46);
        nextBar.setLayout(null);
        nextBar.setBackground(new Color(205, 208, 245));
        nextBar.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        nextWrap.add(nextBar);

        nextInlineText = new JLabel("NEXT IN LINE: ---");
        nextInlineText.setBounds(0, 0, nextBar.getWidth(), nextBar.getHeight());
        nextInlineText.setForeground(navy);
        nextInlineText.setFont(new Font("Segoe UI", Font.BOLD, 26));
        nextInlineText.setHorizontalAlignment(SwingConstants.CENTER);
        nextBar.add(nextInlineText);

        // ====== SKIPPED STRIP ======
        int skippedY = nextY + NEXT_H + GAP;

        JPanel skippedWrap = createCard(bg, M, skippedY, innerW, SKIP_H, cardWhite, borderCol);

        JLabel skLbl = new JLabel("SKIPPED / HOLD:");
        skLbl.setBounds(15, 8, 140, 20);
        skLbl.setForeground(new Color(0, 0, 0, 200));
        skLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        skippedWrap.add(skLbl);

        skippedStrip = new JLabel("---");
        skippedStrip.setBounds(150, 8, innerW - 165, 20);
        skippedStrip.setForeground(navy);
        skippedStrip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        skippedWrap.add(skippedStrip);

        // ===== Live refresh (reads from queueManager every second) =====
        Runnable refreshUI = () -> {

            boolean openNow = queueManager.isQueueOpen();

            // Serving (C1..C6)
            for (int i = 0; i < 6; i++) {
                String counterCode = "C" + (i + 1);
                setCounterServing(counterCode, nowLbl[i], typeLbl[i], transLbl[i]);
            }

            // Skipped strip (top 10)
            java.util.List<String> skipped = new java.util.ArrayList<>();
            for (String[] row : queueManager.queueList) {
                if (row == null) continue;
                String st = row[queueManager.COL_STATUS];
                String s = (st == null) ? "" : st.trim();
                if (s.equalsIgnoreCase("Skipped")) {
                    String qno = row[queueManager.COL_QUEUE_NO];
                    if (qno != null && !qno.trim().isEmpty()) skipped.add(qno.trim());
                }
            }
            int max = Math.min(10, skipped.size());
            skippedStrip.setText(max == 0 ? "---" : String.join("  |  ", skipped.subList(0, max)));

            // NEXT-IN-LINE: ONLY "Waiting"
            java.util.List<String> next = new java.util.ArrayList<>();
            for (String[] row : queueManager.queueList) {
                if (row == null) continue;

                String st = row[queueManager.COL_STATUS];
                String s = (st == null) ? "" : st.trim();

                boolean isWaiting = s.equalsIgnoreCase("Waiting");

                String lower = s.toLowerCase();
                boolean isServing = lower.equals("serving") || lower.startsWith("serving-");
                boolean isBlocked = isServing
                        || lower.equals("skipped")
                        || lower.equals("done")
                        || lower.equals("cancelled");

                if (isWaiting && !isBlocked) {
                    String qno = row[queueManager.COL_QUEUE_NO];
                    if (qno != null && !qno.trim().isEmpty()) next.add(qno.trim());
                }
                if (next.size() >= 6) break;
            }

            if (!openNow) {
                announcementText.setText("QUEUE IS CLOSED  -  PLEASE WAIT FOR THE ADMIN");
                nextInlineText.setText("NEXT IN LINE: ---");
            } else {
                announcementText.setText(buildAnnouncementFromServing());
                nextInlineText.setText("NEXT IN LINE: " + (next.isEmpty() ? "---" : String.join("  |  ", next)));
            }
        };

        refreshUI.run();
        Timer t = new Timer(1000, e -> refreshUI.run());
        t.setCoalesce(true);
        t.start();

        frame.setVisible(true);
    }

    static void setCounterServing(String counterCode, JLabel now, JLabel type, JLabel trans) {
        String[] row = queueManager.getServingRowForCounter(counterCode);
        if (row == null) {
            now.setText("---");
            type.setText("Type: ---");
            trans.setText("Transaction: ---");
            return;
        }

        String qno = row[queueManager.COL_QUEUE_NO];
        String ty  = row[queueManager.COL_TYPE];
        String tr  = row[queueManager.COL_TRANS];

        now.setText(qno == null ? "---" : qno);
        type.setText("Type: " + (ty == null ? "---" : ty.toUpperCase()));
        trans.setText("Transaction: " + (tr == null ? "---" : tr.toUpperCase()));
    }

    static String buildAnnouncementFromServing() {
        for (int i = 1; i <= 6; i++) {
            String counterCode = "C" + i;
            String[] r = queueManager.getServingRowForCounter(counterCode);
            if (r != null) {
                String q = r[queueManager.COL_QUEUE_NO];
                if (q != null && !q.trim().isEmpty()) {
                    return q.trim() + " PLEASE PROCEED TO COUNTER " + i;
                }
            }
        }
        return "PLEASE WAIT FOR YOUR NUMBER";
    }

    static JPanel createCard(JPanel parent, int x, int y, int w, int h, Color bg, Color borderCol) {
        JPanel p = new JPanel();
        p.setBounds(x, y, w, h);
        p.setLayout(null);
        p.setBackground(bg);
        p.setBorder(new MatteBorder(2, 2, 2, 2, borderCol));
        parent.add(p);
        return p;
    }

    static JPanel createStrip(JPanel parent, int x, int y, int w, int h, Color bg, Color borderCol) {
        JPanel p = new JPanel();
        p.setBounds(x, y, w, h);
        p.setLayout(null);
        p.setBackground(bg);
        p.setBorder(new MatteBorder(0, 0, 2, 0, borderCol));
        parent.add(p);
        return p;
    }

    static JLabel createStripTitle(String text) {
        JLabel t = new JLabel(text);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setHorizontalAlignment(SwingConstants.CENTER);
        return t;
    }

    public static void main(String[] args) {
        monitor();
    }
}
