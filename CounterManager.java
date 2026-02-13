import java.io.*;
import java.util.*;

/**
 * CounterManager
 * - Stores counters and lane assignment in a simple text file: counters.txt
 * - Format per line: C1,NORMAL
 * Lane values:
 *   NORMAL  -> serves Normal/Regular only
 *   PRIORITY-> serves Priority only
 *   HYBRID  -> serves Priority first, then Normal/Regular
 *
 * MAX COUNTERS = 6 (for monitor screen layout)
 */
public class CounterManager {

    public static final String FILE_NAME = "counters.txt";
    public static final int MAX_COUNTERS = 6;

    // ===== Lane constants (stored in file) =====
    public static final String LANE_NORMAL   = "NORMAL";
    public static final String LANE_PRIORITY = "PRIORITY";
    public static final String LANE_HYBRID   = "HYBRID";

    private static String norm(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String normCounter(String counter) {
        String c = norm(counter).toUpperCase();
        if (c.matches("^\\d+$")) return "C" + c;
        if (!c.startsWith("C") && c.matches("^[A-Z]?\\d+$")) {
            // fallback for weird inputs
            return "C" + c.replaceAll("\\D+", "");
        }
        return c;
    }

    private static String normLane(String lane) {
        String l = norm(lane).toUpperCase();
        if (l.equals("REGULAR") || l.equals("NORMAL")) return LANE_NORMAL;
        if (l.equals("PRIO") || l.equals("PRIORITY")) return LANE_PRIORITY;
        if (l.equals("HYBRID") || l.equals("MIXED")) return LANE_HYBRID;
        return LANE_HYBRID;
    }

    // Create default counters if file doesn't exist:
    // C1 = NORMAL, C2 = PRIORITY (matches typical setup)
    public static synchronized void initialize() {
        File f = new File(FILE_NAME);
        if (f.exists()) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            pw.println("C1," + LANE_NORMAL);
            pw.println("C2," + LANE_PRIORITY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Map<String, String> loadAll() {
        initialize();

        Map<String, String> map = new LinkedHashMap<>();
        File f = new File(FILE_NAME);
        if (!f.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                String c = normCounter(parts[0]);
                String lane = (parts.length >= 2) ? normLane(parts[1]) : LANE_HYBRID;

                if (c.isEmpty()) continue;
                map.put(c, lane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Keep only up to MAX_COUNTERS, in order C1..C6
        Map<String, String> normalized = new LinkedHashMap<>();
        for (int i = 1; i <= MAX_COUNTERS; i++) {
            String c = "C" + i;
            if (map.containsKey(c)) normalized.put(c, map.get(c));
        }
        return normalized;
    }

    private static synchronized void saveAll(Map<String, String> map) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, String> e : map.entrySet()) {
                pw.println(e.getKey() + "," + normLane(e.getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized List<String> getCounters() {
        Map<String, String> map = loadAll();
        return new ArrayList<>(map.keySet());
    }

    public static synchronized int countCounters() {
        return getCounters().size();
    }

    public static synchronized boolean hasCounter(String counter) {
        String c = normCounter(counter);
        return loadAll().containsKey(c);
    }

    public static synchronized String getLane(String counter) {
        String c = normCounter(counter);
        Map<String, String> map = loadAll();
        String lane = map.get(c);
        return (lane == null) ? LANE_HYBRID : normLane(lane);
    }

    public static synchronized boolean setLane(String counter, String lane) {
        String c = normCounter(counter);
        Map<String, String> map = loadAll();
        if (!map.containsKey(c)) return false;
        map.put(c, normLane(lane));
        saveAll(map);
        return true;
    }

    // Adds the next available counter code up to MAX_COUNTERS.
    // Returns the created counter code (e.g., "C3") or null if max reached.
    public static synchronized String addCounter(String lane) {
        Map<String, String> map = loadAll();

        if (map.size() >= MAX_COUNTERS) return null;

        for (int i = 1; i <= MAX_COUNTERS; i++) {
            String c = "C" + i;
            if (!map.containsKey(c)) {
                map.put(c, normLane(lane));
                saveAll(map);
                return c;
            }
        }
        return null;
    }

    public static synchronized boolean deleteCounter(String counter) {
        String c = normCounter(counter);
        Map<String, String> map = loadAll();
        if (!map.containsKey(c)) return false;

        map.remove(c);
        saveAll(map);
        return true;
    }
}
