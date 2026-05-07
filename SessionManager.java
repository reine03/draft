package byod.session;

/**
 * Holds the currently logged-in user's session data.
 * Set this after a successful login before showing any other screen.
 */
public class SessionManager {

    private static int    currentUserId;
    private static String currentUsername;
    private static String currentFullName;
    private static String currentRole;

    public static void login(int id, String username, String fullName, String role) {
        currentUserId   = id;
        currentUsername = username;
        currentFullName = fullName;
        currentRole     = role;
    }

    public static void logout() {
        currentUserId   = 0;
        currentUsername = null;
        currentFullName = null;
        currentRole     = null;
    }

    public static int    getCurrentUserId()   { return currentUserId; }
    public static String getCurrentUsername() { return currentUsername; }
    public static String getCurrentFullName() { return currentFullName; }
    public static String getCurrentRole()     { return currentRole; }
    public static boolean isAdmin()           { return "ADMIN".equals(currentRole); }
}
