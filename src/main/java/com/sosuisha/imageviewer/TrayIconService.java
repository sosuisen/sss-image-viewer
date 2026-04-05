package com.sosuisha.imageviewer;

import javafx.application.Platform;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Manages the system tray icon for the resident application.
 */
class TrayIconService {

    private TrayIcon trayIcon;

    /**
     * Adds an icon to the system tray with a "Quit" menu item.
     * Does nothing if the system tray is not supported.
     */
    void install() {
        if (!SystemTray.isSupported()) {
            return;
        }

        PopupMenu popup = new PopupMenu();
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.addActionListener(_ -> Platform.runLater(Platform::exit));
        popup.add(quitItem);

        trayIcon = new TrayIcon(createIcon(), "SSS Image Viewer", popup);
        trayIcon.setImageAutoSize(true);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            System.err.println("Failed to add tray icon: " + e.getMessage());
        }
    }

    /**
     * Removes the tray icon from the system tray.
     */
    void uninstall() {
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
            trayIcon = null;
        }
    }

    private static Image createIcon() {
        try {
            var stream = TrayIconService.class.getResourceAsStream("/icon.ico");
            if (stream != null) {
                return ImageIO.read(stream);
            }
        } catch (IOException e) {
            System.err.println("Failed to load icon.ico: " + e.getMessage());
        }
        return createFallbackIcon();
    }

    private static Image createFallbackIcon() {
        Dimension traySize = SystemTray.getSystemTray().getTrayIconSize();
        int size = Math.max(traySize.width, traySize.height);
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0x33, 0x99, 0xFF));
        int arc = Math.max(4, size / 4);
        g.fillRoundRect(0, 0, size, size, arc, arc);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size * 11 / 16));
        FontMetrics fm = g.getFontMetrics();
        int x = (size - fm.stringWidth("S")) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString("S", x, y);
        g.dispose();
        return img;
    }
}
