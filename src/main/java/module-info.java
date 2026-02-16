module com.sosuisha.imageviewer {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires java.desktop;
    exports com.sosuisha.imageviewer to javafx.graphics, javafx.controls;
}