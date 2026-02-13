import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class queueManager {

    // ================== COLUMNS ==================
    public static final int COL_QUEUE_NO   = 0;
    public static final int COL_TRANS      = 1;
    public static final int COL_TYPE       = 2;
    public static final int COL_STATUS     = 3;
    public static final int COL_SERVED_BY  = 4;  // e.g. staff username (optional)
    public static final int COL_COUNTER    = 5;  // e.g. "C1" / "C2"
    public static final int COL_START_DT   = 6;
    public static final int COL_FINISH_DT  = 7;

    // ================== DATA ==================
    public static final List<String[]> queueList = new ArrayList<>();
    private static boolean queueOpen = false;

    private static final DateTimeFormatter DT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Queue number format: P/N0126-001 (P=Priority, N=Regular/Normal, 0126=MMYY)
    private static final DateTimeFormatter MMYY =
            DateTimeFormatter.ofPattern("MMyy");

    // ================== QUEUE STATE ==================
    public static synchronized boolean isQueueOpen() {
        return queueOpen;
    }

    public static synchronized void setQueueOpen(boolean open) {
        queueOpen = open;
    }

    // ================== NORMALIZE HELPERS ==================
    private static String norm(String s) {
        return (s == null) ? "" : s.trim();
    }

    // OK normalize counter to canonical "C1"/"C2"
    private static String normCounter(String counter) {
        String c = norm(counter).toUpperCase();
        if (c.isEmpty()) return "";

        // numeric -> C#
        if (c.matches("^\\d+$")) return "C" + c;

        // already canonical
        if (c.matches("^C\\d+$")) return c;

        // fallback: extract digits
        String digits = c.replaceAll("\\D+", "");
        if (!digits.isEmpty()) return "C" + digits;
        return c;
    }

    // Canonical lane types:
    // - "Regular"  (covers: Normal, Regular)
    // - "Priority" (covers: Priority)
    private static String normType(String type) {
        String t = norm(type).toLowerCase();
        if (t.isEmpty()) return "";

        if (t.equals("normal") || t.equals("regular") || t.equals("reg")) return "Regular";
        if (t.equals("priority") || t.equals("prio")) return "Priority";

        return norm(type);
    }

    // ================== QUEUE NUMBER ==================
    public static synchronized String generateQueueNo(String type) {

        String canon = normType(type);
        String prefix = "N"; // N = Regular/Normal
        if ("Priority".equalsIgnoreCase(canon)) {
            prefix = "P";
        }

        String mmyy = LocalDate.now().format(MMYY);

        int max = 0;
        for (String[] r : queueList) {
            if (r == null || r.length <= COL_QUEUE_NO) continue;

            String q = r[COL_QUEUE_NO];
            if (q == null) continue;

            String qNormalized = q.replace("/", "").trim();

            if (!qNormalized.startsWith(prefix + mmyy + "-")) continue;

            int dash = qNormalized.lastIndexOf('-');
            if (dash < 0 || dash + 1 >= qNormalized.length()) continue;

            try {
                int n = Integer.parseInt(qNormalized.substring(dash + 1));
                if (n > max) max = n;
            } catch (NumberFormatException ignored) {}
        }

        int next = max + 1;
        return prefix + "/" + mmyy + "-" + String.format("%03d", next);
    }

    // ================== ADD QUEUE (Customer Page) ==================
    public static synchronized void addQueue(String queueNo, String transaction, String type) {

        queueList.add(new String[]{
                norm(queueNo),                    // 0
                norm(transaction),                // 1
                normType(type),                   // 2  <-- store canonical type
                "Waiting",                        // 3
                "-",                              // 4
                "-",                              // 5
                LocalDateTime.now().format(DT),   // 6
                ""                                // 7
        });
    }

    // ================== DISPLAY MONITOR HELPERS ==================
    public static synchronized List<String> getNextInLine(int max) {
        List<String> next = new ArrayList<>();
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;

            if (isWaitingStatus(r[COL_STATUS])) {
                String qno = r[COL_QUEUE_NO];
                if (qno != null && !qno.trim().isEmpty()) next.add(qno.trim());
            }

            if (next.size() >= max) break;
        }
        return next;
    }

    // ================== STATUS HELPERS ==================
    private static boolean isFinishedStatus(String status) {
        String s = norm(status);
        return s.equalsIgnoreCase("Done") || s.equalsIgnoreCase("Cancelled");
    }

    private static boolean isWaitingStatus(String status) {
        return norm(status).equalsIgnoreCase("Waiting");
    }

    private static boolean isSkippedStatus(String status) {
        return norm(status).equalsIgnoreCase("Skipped");
    }

    private static boolean isServingStatus(String status) {
        String s = norm(status).toLowerCase();
        return s.equals("serving") || s.startsWith("serving-");
    }

    // ================== ADMIN TABLE (Queue Management) ==================
    public static synchronized List<String[]> getRowsForAdminTable4(String filter) {
        List<String[]> out = new ArrayList<>();

        String f = (filter == null) ? "All" : filter;

        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;

            String type = normType(r[COL_TYPE]);
            if (!"All".equalsIgnoreCase(f) && !normType(f).equalsIgnoreCase(type)) continue;

            out.add(new String[]{
                    r[COL_QUEUE_NO],
                    r[COL_TRANS],
                    type,
                    r[COL_STATUS]
            });
        }
        return out;
    }

    // ================== STAFF TABLES ==================
    public static synchronized List<String[]> getRowsForCounterQueueTable(String counter) {
        List<String[]> out = new ArrayList<>();
        String c = normCounter(counter);
        String servingKey = "Serving-" + c;

        String preferred = preferredTypeForCounter(c);
        String backup    = backupTypeForCounter(c);

        boolean preferredWaiting = hasWaitingType(preferred);

        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;

            if (norm(r[COL_STATUS]).equalsIgnoreCase(servingKey)) {
                out.add(new String[]{ r[COL_QUEUE_NO], r[COL_TRANS], normType(r[COL_TYPE]), r[COL_STATUS] });
                continue;
            }

            if (isWaitingStatus(r[COL_STATUS])) {
                String rowType = normType(r[COL_TYPE]);

                if (preferredWaiting) {
                    if (!preferred.equalsIgnoreCase(rowType)) continue;
                } else {
                    if (!backup.equalsIgnoreCase(rowType)) continue;
                }

                out.add(new String[]{ r[COL_QUEUE_NO], r[COL_TRANS], rowType, r[COL_STATUS] });
            }
        }

        return out;
    }

    public static synchronized List<String[]> getRowsForCounterSkippedTable(String counter) {
        List<String[]> out = new ArrayList<>();
        String c = normCounter(counter);

        String preferred = preferredTypeForCounter(c);
        String backup    = backupTypeForCounter(c);

        boolean preferredSkipped = hasSkippedType(preferred);

        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;

            if (isSkippedStatus(r[COL_STATUS])) {
                String rowType = normType(r[COL_TYPE]);

                if (preferredSkipped) {
                    if (!preferred.equalsIgnoreCase(rowType)) continue;
                } else {
                    if (!backup.equalsIgnoreCase(rowType)) continue;
                }

                out.add(new String[]{ r[COL_QUEUE_NO], r[COL_TRANS], rowType, r[COL_STATUS] });
            }
        }

        return out;
    }

    public static synchronized int countWaitingEligible(String counter) {
        String c = normCounter(counter);

        String preferred = preferredTypeForCounter(c);
        String backup    = backupTypeForCounter(c);

        boolean preferredWaiting = hasWaitingType(preferred);
        String typeToCount = preferredWaiting ? preferred : backup;

        int cnt = 0;
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (!isWaitingStatus(r[COL_STATUS])) continue;

            if (typeToCount.equalsIgnoreCase(normType(r[COL_TYPE]))) cnt++;
        }
        return cnt;
    }

    public static synchronized int countSkippedEligible(String counter) {
        String c = normCounter(counter);

        String preferred = preferredTypeForCounter(c);
        String backup    = backupTypeForCounter(c);

        boolean preferredSkipped = hasSkippedType(preferred);
        String typeToCount = preferredSkipped ? preferred : backup;

        int cnt = 0;
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (!isSkippedStatus(r[COL_STATUS])) continue;

            if (typeToCount.equalsIgnoreCase(normType(r[COL_TYPE]))) cnt++;
        }
        return cnt;
    }

    public static synchronized int countServedToday(String counter) {
        String cCounter = normCounter(counter);
        int cnt = 0;
        String today = LocalDate.now().toString();

        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;

            if (!norm(r[COL_STATUS]).equalsIgnoreCase("Done")) continue;

            String rowCounter = normCounter(r[COL_COUNTER]);
            if (!cCounter.isEmpty() && !cCounter.equalsIgnoreCase(rowCounter)) continue;

            String finish = r[COL_FINISH_DT];
            if (finish != null && finish.startsWith(today)) cnt++;
        }
        return cnt;
    }

    public static synchronized String[] getServingRowForCounter(String counter) {
        String c = normCounter(counter);
        String servingKey = "Serving-" + c;
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (norm(r[COL_STATUS]).equalsIgnoreCase(servingKey)) return r;
        }
        return null;
    }

    // ================== SERVING ==================
    public static synchronized boolean hasServing(String servingKey) {
        String key = norm(servingKey);
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (norm(r[COL_STATUS]).equalsIgnoreCase(key)) return true;
        }
        return false;
    }

    public static synchronized String[] getServingRow(String servingKey) {
        String key = norm(servingKey);
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (norm(r[COL_STATUS]).equalsIgnoreCase(key)) return r;
        }
        return null;
    }

    // ===== Lane rules (from CounterManager) =====
    // NORMAL  -> serves Regular only
    // PRIORITY-> serves Priority only
    // HYBRID  -> serves Priority first, then Regular

    private static boolean hasWaitingType(String type) {
        String t = normType(type);
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (!isWaitingStatus(r[COL_STATUS])) continue;

            if (t.equalsIgnoreCase(normType(r[COL_TYPE]))) return true;
        }
        return false;
    }

    private static boolean hasSkippedType(String type) {
        String t = normType(type);
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (!isSkippedStatus(r[COL_STATUS])) continue;

            if (t.equalsIgnoreCase(normType(r[COL_TYPE]))) return true;
        }
        return false;
    }

    private static String preferredTypeForCounter(String counter) {
        String c = normCounter(counter);
        String lane = CounterManager.getLane(c);

        if (CounterManager.LANE_PRIORITY.equalsIgnoreCase(lane)) return "Priority";
        if (CounterManager.LANE_NORMAL.equalsIgnoreCase(lane)) return "Regular";
        // HYBRID
        return "Priority";
    }

    private static String backupTypeForCounter(String counter) {
        String c = normCounter(counter);
        String lane = CounterManager.getLane(c);

        if (CounterManager.LANE_HYBRID.equalsIgnoreCase(lane)) return "Regular";
        // NORMAL / PRIORITY should never fallback to the other lane
        return "";
    }

    private static String[] pickNextWaitingOfType(String type) {
        String t = normType(type);
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (!isWaitingStatus(r[COL_STATUS])) continue;

            if (t.equalsIgnoreCase(normType(r[COL_TYPE]))) return r;
        }
        return null;
    }

    // Optional overload if you want to store who served it (username)
    public static synchronized String[] serveNextForCounter(String counter, String servedByUser) {
        String c = normCounter(counter);
        String servingKey = "Serving-" + c;

        if (hasServing(servingKey)) return null;

        String preferred = preferredTypeForCounter(c);
        String backup    = backupTypeForCounter(c);

        // 1) Try preferred lane
        String[] next = pickNextWaitingOfType(preferred);

        // 2) Only HYBRID can fallback to backup, and only when no preferred is waiting
        if (next == null && backup != null && !backup.trim().isEmpty()) {
            boolean preferredWaiting = hasWaitingType(preferred);
            if (!preferredWaiting) {
                next = pickNextWaitingOfType(backup);
            }
        }

        if (next != null) {
            next[COL_STATUS] = "Serving-" + c;
            next[COL_COUNTER] = c;
            next[COL_SERVED_BY] = (servedByUser == null ? "-" : servedByUser);
            return next;
        }

        return null;
    }

    public static synchronized String[] serveNextForCounter(String counter) {
        return serveNextForCounter(counter, "-");
    }

    // ================== ACTIONS ==================
    public static synchronized boolean markDone(String queueNo) {
        String q = norm(queueNo);

        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;

            if (norm(r[COL_QUEUE_NO]).equals(q) && isServingStatus(r[COL_STATUS])) {
                r[COL_STATUS] = "Done";
                r[COL_FINISH_DT] = LocalDateTime.now().format(DT);
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean skipQueue(String queueNo) {
        String q = norm(queueNo);

        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;

            if (norm(r[COL_QUEUE_NO]).equals(q) && isWaitingStatus(r[COL_STATUS])) {
                r[COL_STATUS] = "Skipped";
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean recallSkipped(String queueNo) {
        String q = norm(queueNo);

        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;

            if (norm(r[COL_QUEUE_NO]).equals(q) && isSkippedStatus(r[COL_STATUS])) {
                r[COL_STATUS] = "Waiting";
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean cancelQueue(String queueNo) {
        String q = norm(queueNo);

        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;

            if (norm(r[COL_QUEUE_NO]).equals(q)) {
                r[COL_STATUS] = "Cancelled";
                r[COL_FINISH_DT] = LocalDateTime.now().format(DT);
                return true;
            }
        }
        return false;
    }

    // ===== ADMIN TOTALS (ignore preferred-lane rules) =====
    public static synchronized int CountTotalWaitingAll() {
        int c = 0;
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (isWaitingStatus(r[COL_STATUS])) c++;
        }
        return c;
    }

    public static synchronized int CountTotalSkippedAll() {
        int c = 0;
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (isSkippedStatus(r[COL_STATUS])) c++;
        }
        return c;
    }

    public static synchronized int countWaitingByType(String type) {
        String t = normType(type);
        int c = 0;
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (!isWaitingStatus(r[COL_STATUS])) continue;

            if (t.isEmpty() || "All".equalsIgnoreCase(t) || t.equalsIgnoreCase(normType(r[COL_TYPE]))) c++;
        }
        return c;
    }

    public static synchronized int countSkippedByType(String type) {
        String t = normType(type);
        int c = 0;
        for (String[] r : queueList) {
            if (r == null || r.length < 8) continue;
            if (isFinishedStatus(r[COL_STATUS])) continue;
            if (!isSkippedStatus(r[COL_STATUS])) continue;

            if (t.isEmpty() || "All".equalsIgnoreCase(t) || t.equalsIgnoreCase(normType(r[COL_TYPE]))) c++;
        }
        return c;
    }
}
