import java.util.*;

/**
 * SessionManager
 * - Tracks which counters are currently in use by logged-in staff.
 * - Uses counter codes like "C1".."C6".
 */
public class SessionManager {

    // counterCode -> username currently using it
    private static final Map<String, String> counterInUseBy = new HashMap<>();

    private static String normCounter(String counter) {
        if (counter == null) return null;
        String c = counter.trim().toUpperCase();
        if (c.isEmpty() || c.equals("-") || c.equals("NONE")) return null;
        if (c.matches("^\\d+$")) return "C" + c;
        if (c.matches("^C\\d+$")) return c;

        String digits = c.replaceAll("\\D+", "");
        if (!digits.isEmpty()) return "C" + digits;
        return null;
    }

    // Check if user is allowed to use their assigned counter
    public static synchronized boolean canStartSession(String username, String assignedCounter) {
        String u = (username == null) ? "" : username.trim();
        String c = normCounter(assignedCounter);
        if (u.isEmpty() || c == null) return false;

        // Counter must exist (Admin manages counters)
        if (!CounterManager.hasCounter(c)) return false;

        // Counter already in use by someone else
        String currentUser = counterInUseBy.get(c);
        return (currentUser == null || currentUser.equalsIgnoreCase(u));
    }

    // Reserve counter for this user (call on successful staff login)
    public static synchronized boolean startSession(String username, String assignedCounter) {
        if (!canStartSession(username, assignedCounter)) return false;
        String u = username.trim();
        String c = normCounter(assignedCounter);
        counterInUseBy.put(c, u);
        return true;
    }

    // Release counter (call on staff signout / window close)
    public static synchronized void endSession(String username, String counter) {
        String u = (username == null) ? "" : username.trim();
        String c = normCounter(counter);
        if (u.isEmpty() || c == null) return;

        String currentUser = counterInUseBy.get(c);
        if (currentUser != null && currentUser.equalsIgnoreCase(u)) {
            counterInUseBy.remove(c);
        }
    }

    public static synchronized boolean isCounterInUse(String counter) {
        String c = normCounter(counter);
        return c != null && counterInUseBy.containsKey(c);
    }

    public static synchronized String whoUsesCounter(String counter) {
        String c = normCounter(counter);
        if (c == null) return null;
        return counterInUseBy.get(c);
    }

    // For Admin Call-Next popup: list only counters with active staff sessions.
    public static synchronized java.util.List<String> getActiveCounters() {
        java.util.List<String> out = new ArrayList<>(counterInUseBy.keySet());
        Collections.sort(out, (a, b) -> {
            try {
                int ia = Integer.parseInt(a.replaceAll("\\D+", ""));
                int ib = Integer.parseInt(b.replaceAll("\\D+", ""));
                return Integer.compare(ia, ib);
            } catch (Exception e) {
                return a.compareTo(b);
            }
        });
        return out;
    }
}