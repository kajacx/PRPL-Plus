package com.prplplus.test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class TabsTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Tabs text");

            @SuppressWarnings("serial")
            JTabbedPane tabs = new JTabbedPane() {
                @Override
                public Dimension getPreferredSize() {
                    int tabsWidth = 0;

                    for (int i = 0; i < getTabCount(); i++) {
                        tabsWidth += getBoundsAt(i).width;
                        System.out.println("Width at " + i + ": " + getBoundsAt(i).width);
                    }

                    Dimension preferred = super.getPreferredSize();

                    preferred.width = Math.max(preferred.width, tabsWidth);

                    return preferred;
                }
            };

            tabs.addTab("Tab1", new JLabel("Content1"));
            tabs.addTab("Tab2", new JLabel("Content2"));
            tabs.addTab("Tab3", new JLabel("Content3"));
            tabs.addTab("Tab4", new JLabel("Content4"));

            frame.add(tabs);

            frame.pack();
            frame.pack();

            for (int i = 0; i < tabs.getTabCount(); i++) {
                System.out.println(tabs.getUI().getTabBounds(tabs, i));
            }

            frame.setVisible(true);

        });
    }
}
