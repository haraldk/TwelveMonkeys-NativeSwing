import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a> */
public class TestPaletteWindow {

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Foo");

                frame.setUndecorated(true);
                frame.getRootPane().setBorder(new PaletteWindowBorder());

                frame.add(new JLabel("Testing, testing, 1-2-3", JLabel.CENTER));
                frame.add(new JButton(new AbstractAction("Toggle Border") {
                    Border border;

                    public void actionPerformed(ActionEvent e) {
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                        JRootPane root = frame.getRootPane();
                        if (root.getBorder() != null) {
                            border = root.getBorder();
                            root.setBorder(null);
                        }
                        else {
                            root.setBorder(border);
                        }
                    }
                }), BorderLayout.SOUTH);
                frame.setPreferredSize(new Dimension(300, 200));

                JMenuBar bar = new JMenuBar();
                bar.add(new JMenu("File"));
                bar.add(new JMenu("Edit"));
                frame.setJMenuBar(bar);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private static class DragHandler extends MouseAdapter {
        private final Window owner;
        private Point point;

        public DragHandler(Window owner) {
            this.owner = owner;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            point = e.getLocationOnScreen();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            point = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (point == null) {
                return;
            }

            Point newLoc = e.getLocationOnScreen();
            int dX = newLoc.x - point.x;
            int dY = newLoc.y - point.y;

            Point location = owner.getLocation();
            owner.setLocation(location.x + dX, location.y + dY);

            point = newLoc;
        }
    }

    public static class PaletteTitleBar extends JComponent implements UIResource {
        private Window owner;
        private PropertyChangeListener titleListener;
        private MouseAdapter dragHandler;

        protected final JLabel titleLabel = new TitleLabel(); // TODO: Consider "rubberstamp" instead of using component hierarchy

        private static final Color HIGHLIGHT = new Color(0x80FFFFFF, true);
        private static final Color SHADOW = new Color(0x80000000, true);
        private static final Color SHADOW_LIGHT = new Color(0x4B000000, true);
        private static final Color[] FOCUS_GRADIENT = new Color[]{new Color(0xD0D0D0), new Color(0xA0A0A0)};
        private static final Color[] NON_FOCUS_GRADIENT = new Color[]{new Color(0xEEEEEE), new Color(0xD8D8D8)};
        private WindowFocusListener focusListener;

        public PaletteTitleBar() {
            setLayout(new BorderLayout());
            add(titleLabel);

            if ("!Aqua".equals(UIManager.getLookAndFeel().getID())) {
                titleLabel.putClientProperty("JComponent.sizeVariant", "small");
            }
            else {
                Font font = titleLabel.getFont();
                titleLabel.setFont(font.deriveFont(font.getSize2D() * 0.8f));
            }
        }

        @Override
        public void addNotify() {
            super.addNotify();

            owner = SwingUtilities.getWindowAncestor(this);

            if (owner instanceof Frame) {
                Frame frame = (Frame) owner;
                titleLabel.setText(frame.getTitle());
            }

            // Update window title if it changes
            titleListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    titleLabel.setText((String) evt.getNewValue());
                }
            };
            owner.addPropertyChangeListener("title", titleListener);

            // Repaint whenever window gains/looses focus
            focusListener = new WindowFocusListener() {
                public void windowGainedFocus(WindowEvent e) {
                    repaint();
                }

                public void windowLostFocus(WindowEvent e) {
                    repaint();
                }
            };
            owner.addWindowFocusListener(focusListener);

            dragHandler = new DragHandler(owner);
            addMouseListener(dragHandler);
            addMouseMotionListener(dragHandler);
        }

        @Override
        public void removeNotify() {
            removeMouseListener(dragHandler);
            removeMouseListener(dragHandler);
            owner.removeWindowFocusListener(focusListener);
            owner.removePropertyChangeListener("title", titleListener);

            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr;

//            g.setColor(getBackground());
//            g.fillRect(0, 0, getWidth(), getHeight());

            // TODO: Consider blending bg with gradient or other way of customizable color...
            boolean windowHasFocus = owner.isActive();

            g.setPaint(new LinearGradientPaint(
                    0, 0, 0, getHeight(),
                    new float[]{0.0f, 1.0f},
                    windowHasFocus ? FOCUS_GRADIENT : NON_FOCUS_GRADIENT
            ));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setPaint(HIGHLIGHT);
            g.fillRect(0, 0, getWidth(), 1);

            g.setPaint(windowHasFocus ? SHADOW : SHADOW_LIGHT);
            g.fillRect(0, getHeight() - 1, getWidth(), 1);
        }

        private class TitleLabel extends JLabel implements UIResource {
            private Color foregroundOverride;

            private TitleLabel() {
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
            }

            private void setForegroundOverride(Color color) {
                // No events to avoid unwanted repaint
                foregroundOverride = color;
            }

            @Override
            public Color getForeground() {
                return foregroundOverride != null ? foregroundOverride : super.getForeground();
            }


            @Override
            protected void paintComponent(Graphics g) {
                // Paint the label with shadow effect
                g.translate(0, 1);
                setForegroundOverride(HIGHLIGHT);
                super.paintComponent(g);

                setForegroundOverride(owner.isActive() ? null : SHADOW);
                g.translate(0, -1);
                super.paintComponent(g);
            }
        }
    }

    // Hack border, that makes sure the title bar component is added to the hierarchy and does custom layout.
    // TODO: Allow Windows style window border (not just title bar)
    public static class PaletteWindowBorder extends AbstractBorder {
        final JComponent bar;

        public PaletteWindowBorder() {
            bar = new PaletteTitleBar();
        }

        public PaletteWindowBorder(JComponent bar) {
            this.bar = bar;
        }

        private void assureChildComponent(final Component c) {
            final Container container = (Container) c;

            if (container.getComponentZOrder(bar) < 0) {
                // If not present, add title bar to hierarchy
                container.add(bar);

                // Remove title bar, if border is removed
                c.addPropertyChangeListener("border", new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        c.removePropertyChangeListener("border", this);
                        container.remove(bar);
                    }
                });
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0, 0, 0, 0));
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            assureChildComponent(c);
            insets.top = bar.getPreferredSize().height;

            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            assureChildComponent(c);
            // Layout title bar
            bar.setBounds(x, y, width, bar.getPreferredSize().height);
        }
    }
}
