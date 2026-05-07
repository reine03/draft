package byod;

/**
 * Application entry point.
 *
 * This project is a JavaFX desktop GUI application. JavaFX requires a
 * graphical display server (X11 / Wayland) which is not available in
 * Railway's headless Linux container environment.
 *
 * To run this application on Railway it must be converted to a headless
 * web service — for example, a Spring Boot REST API that exposes the same
 * student/device/log functionality over HTTP instead of through a GUI.
 *
 * This stub class exists solely so that Maven can compile and package the
 * project (Railpack requires a pom.xml with a valid main class to detect
 * and build a Java project).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("BYOD Registration and Monitoring System");
        System.out.println("----------------------------------------");
        System.out.println("This application is a JavaFX desktop GUI and cannot render");
        System.out.println("in a headless container environment.");
        System.out.println("Please refactor to a web service (e.g. Spring Boot REST API)");
        System.out.println("before deploying on Railway.");
    }
}
