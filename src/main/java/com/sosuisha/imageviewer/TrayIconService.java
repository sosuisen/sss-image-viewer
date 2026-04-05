package com.sosuisha.imageviewer;

import javafx.application.Platform;

import com.sosuisha.imageviewer.view.HistoryWindow;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

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

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // ignore
        }

        UIManager.put("MenuItem.checkIcon", new javax.swing.Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {}
            @Override public int getIconWidth() { return 0; }
            @Override public int getIconHeight() { return 0; }
        });
        var popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        var historyItem = new JMenuItem("History", loadIcon("/history_icon.png"));
        historyItem.addActionListener(_ -> Platform.runLater(HistoryWindow::new));
        popup.add(historyItem);
        var quitItem = new JMenuItem("Quit", loadIcon("/quit_icon.png"));
        quitItem.addActionListener(_ -> Platform.runLater(Platform::exit));
        popup.add(quitItem);

        trayIcon = new TrayIcon(createIcon(), "SSS Image Viewer");
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(_ -> Platform.runLater(HistoryWindow::new));
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(popup);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(popup);
                }
            }

            private void showPopup(JPopupMenu menu) {
                var mousePos = MouseInfo.getPointerInfo().getLocation();
                int x = mousePos.x;
                int y = mousePos.y - menu.getPreferredSize().height;
                menu.setLocation(x, y);
                menu.setInvoker(menu);
                menu.setVisible(true);
            }
        });

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

    private static javax.swing.Icon loadIcon(String resourcePath) {
        try {
            var stream = TrayIconService.class.getResourceAsStream(resourcePath);
            if (stream != null) {
                var original = ImageIO.read(stream);
                int targetSize = 16;
                var scaled = original.getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);
                return new javax.swing.ImageIcon(scaled);
            }
        } catch (IOException e) {
            System.err.println("Failed to load " + resourcePath + ": " + e.getMessage());
        }
        return null;
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
