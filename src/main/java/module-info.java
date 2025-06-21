module com.sosuisha.imageviewer {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.swing;
    requires java.desktop;
    requires org.apache.commons.imaging;
    exports com.sosuisha.imageviewer to javafx.graphics, javafx.controls;
}