import java.io.*;
import java.util.*;

public class AccountManager {

    public static final String FILE_NAME = "accounts.txt";

    // Create default accounts once (Role,Username,Password,Counter)
    public static void initialize() {

        File file = new File(FILE_NAME);
        if (file.exists()) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Admin,Admin1,12345,-");
            pw.println("Staff,staff1,11111,1");
            pw.println("Staff,staff2,22222,2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns role if valid, null if invalid
    public static/*  */ String authenticateRole(String username, String password) {

        File f = new File(FILE_NAME);
        if (!f.exists()) return null;

        String u = (username == null) ? "" : username.trim();
        String p = (password == null) ? "" : password.trim();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] d = line.split(",");
                if (d.length < 3) continue;

                String role = d[0].trim();
                String user = d[1].trim();
                String pass = d[2].trim();

                if (user.equalsIgnoreCase(u) && pass.equals(p)) {
                    return role;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Returns counter (string like "1","2","-")
    public static String getAssignedCounter(String username) {

        File f = new File(FILE_NAME);
        if (!f.exists()) return "-";

        String u = (username == null) ? "" : username.trim();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] d = line.split(",");
                if (d.length < 2) continue;

                String user = d[1].trim();
                if (user.equalsIgnoreCase(u)) {
                    return (d.length >= 4) ? d[3].trim() : "-";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "-";
    }

    // ===== STAFF MANAGEMENT =====
    // Rows for JTable: Role, Username, Password, Counter
    public static List<String[]> getStaffRows() {

        List<String[]> out = new ArrayList<>();

        File f = new File(FILE_NAME);
        if (!f.exists()) return out;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] d = line.split(",");
                if (d.length < 3) continue;

                String role = d[0].trim();
                String user = d[1].trim();
                String pass = d[2].trim();
                String counter = (d.length >= 4) ? d[3].trim() : "-";

                if (!role.equalsIgnoreCase("Staff")) continue;

                out.add(new String[]{ role, user, pass, counter });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static boolean usernameExists(String username) {

        File f = new File(FILE_NAME);
        if (!f.exists()) return false;

        String u = (username == null) ? "" : username.trim();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] d = line.split(",");
                if (d.length >= 2 && d[1].trim().equalsIgnoreCase(u)) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean addStaff(String username, String password) {

        if (username == null || password == null) return false;

        String u = username.trim();
        String p = password.trim();

        if (u.isEmpty() || p.isEmpty()) return false;
        if (usernameExists(u)) return false;

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            pw.println("Staff," + u + "," + p + ",-");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Update staff (by old username key)
    public static boolean updateStaff(String oldUsername, String newUsername, String newPassword) {

        File input = new File(FILE_NAME);
        if (!input.exists()) return false;

        File temp = new File("temp_accounts.txt");
        boolean updated = false;

        String oldU = (oldUsername == null) ? "" : oldUsername.trim();
        String newU = (newUsername == null) ? "" : newUsername.trim();
        String newP = (newPassword == null) ? "" : newPassword.trim();

        if (newU.isEmpty() || newP.isEmpty()) return false;

        // block duplicates if changing username
        if (!oldU.equalsIgnoreCase(newU) && usernameExists(newU)) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(input));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] d = line.split(",");
                if (d.length < 3) continue;

                String role = d[0].trim();
                String user = d[1].trim();
                String pass = d[2].trim();
                String counter = (d.length >= 4) ? d[3].trim() : "-";

                if (role.equalsIgnoreCase("Staff") && user.equalsIgnoreCase(oldU)) {
                    pw.println("Staff," + newU + "," + newP + "," + counter);
                    updated = true;
                } else {
                    // normalize to 4 columns
                    pw.println(role + "," + user + "," + pass + "," + counter);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (input.delete()) temp.renameTo(input);
        return updated;
    }

    public static boolean deleteStaff(String username) {

        File input = new File(FILE_NAME);
        if (!input.exists()) return false;

        File temp = new File("temp_accounts.txt");
        boolean deleted = false;

        String u = (username == null) ? "" : username.trim();

        try (BufferedReader br = new BufferedReader(new FileReader(input));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] d = line.split(",");
                if (d.length < 3) continue;

                String role = d[0].trim();
                String user = d[1].trim();
                String pass = d[2].trim();
                String counter = (d.length >= 4) ? d[3].trim() : "-";

                if (role.equalsIgnoreCase("Staff") && user.equalsIgnoreCase(u)) {
                    deleted = true;
                    continue;
                }

                pw.println(role + "," + user + "," + pass + "," + counter);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (input.delete()) temp.renameTo(input);
        return deleted;
    }

    public static boolean assignCounter(String username, String counterValue) {

        File input = new File(FILE_NAME);
        if (!input.exists()) return false;

        File temp = new File("temp_accounts.txt");
        boolean updated = false;

        String u = (username == null) ? "" : username.trim();
        String c = (counterValue == null) ? "" : counterValue.trim();

        if (c.isEmpty()) c = "-";

        try (BufferedReader br = new BufferedReader(new FileReader(input));
             PrintWriter pw = new PrintWriter(new FileWriter(temp))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] d = line.split(",");
                if (d.length < 3) continue;

                String role = d[0].trim();
                String user = d[1].trim();
                String pass = d[2].trim();
                String counter = (d.length >= 4) ? d[3].trim() : "-";

                if (role.equalsIgnoreCase("Staff") && user.equalsIgnoreCase(u)) {
                    pw.println("Staff," + user + "," + pass + "," + c);
                    updated = true;
                } else {
                    pw.println(role + "," + user + "," + pass + "," + counter);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (input.delete()) temp.renameTo(input);
        return updated;
    }


    // ===== STAFF PROFILE =====
    // Optional password change (used in Staff Profile menu)
    public static synchronized boolean changePassword(String username, String oldPassword, String newPassword) {

        File f = new File(FILE_NAME);
        if (!f.exists()) return false;

        String u = (username == null) ? "" : username.trim();
        String oldP = (oldPassword == null) ? "" : oldPassword.trim();
        String newP = (newPassword == null) ? "" : newPassword.trim();

        if (u.isEmpty() || oldP.isEmpty() || newP.isEmpty()) return false;

        java.util.List<String> lines = new java.util.ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String raw = line;
                line = line.trim();
                if (line.isEmpty()) {
                    lines.add(raw);
                    continue;
                }

                String[] d = line.split(",");
                if (d.length < 3) {
                    lines.add(raw);
                    continue;
                }

                String role = d[0].trim();
                String user = d[1].trim();
                String pass = d[2].trim();

                if (user.equalsIgnoreCase(u) && pass.equals(oldP)) {
                    // rewrite password only
                    d[2] = newP;
                    // keep the rest of columns as-is
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < d.length; i++) {
                        if (i > 0) sb.append(",");
                        sb.append(d[i].trim());
                    }
                    lines.add(sb.toString());
                    updated = true;
                } else {
                    lines.add(raw);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!updated) return false;

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (String l : lines) pw.println(l);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    // Add a new STAFF account with an auto-generated temporary password.
    // Returns the generated password, or null if add failed.
    public static String addStaffAuto(String username) {

        String u = (username == null) ? "" : username.trim();
        if (u.isEmpty()) return null;

        // Basic username validation (student-friendly)
        if (!u.matches("[A-Za-z0-9_]+")) return null;

        // Prevent duplicates
        if (userExists(u)) return null;

        String tempPass = generateTempPassword(6);
        String line = "Staff," + u + "," + tempPass + ",-";

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            pw.println(line);
            return tempPass;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // -------- helpers --------
    private static boolean userExists(String username) {

        File f = new File(FILE_NAME);
        if (!f.exists()) return false;

        String u = (username == null) ? "" : username.trim();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 2) continue;
                if (p[1].trim().equalsIgnoreCase(u)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String generateTempPassword(int len) {
        // Digits-only is easiest for students to type at the counter
        String digits = "0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < len; i++) {
            sb.append(digits.charAt(r.nextInt(digits.length())));
        }
        return sb.toString();
    }

}
