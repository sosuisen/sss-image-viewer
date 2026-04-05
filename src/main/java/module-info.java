module com.sosuisha.imageviewer {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires io.github.sosuisen.api.jfxbuilder.controls;
    requires io.github.sosuisen.api.jfxbuilder.graphics;
    exports com.sosuisha.imageviewer to javafx.graphics, javafx.controls;
}