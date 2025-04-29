module com.sosuisha.imageviewer {
    requires javafx.controls;
    requires javafx.swing;
    requires transitive javafx.graphics;
    exports com.sosuisha.imageviewer to javafx.graphics, javafx.controls;
}
