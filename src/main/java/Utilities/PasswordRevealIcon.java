package Utilities;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.AnimatedIcon;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class PasswordRevealIcon implements AnimatedIcon {

    private final Icon icon;
    private final int space;

    public PasswordRevealIcon() {
        this(new FlatSVGIcon("Icon/eye.svg", 0.75f), 3);
    }

    public PasswordRevealIcon(Icon icon, int space) {
        this.icon = icon;
        this.space = space;
    }

    @Override
    public void paintIconAnimated(Component c, Graphics g, int x, int y, float animatedValue) {
        Graphics2D g2 = (Graphics2D) g.create();
        int s = UIScale.scale(space);
        icon.paintIcon(c, g2, x, y);
        if (animatedValue > 0) {
            float startX = x + s;
            float startY = y + getIconHeight() - s;

            float endX = x + getIconWidth() - s;
            float endY = y + s;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Shape shape = new Line2D.Float(startX, startY, startX + (endX - startX) * animatedValue, startY + (endY - startY) * animatedValue);

            drawLine(g2, shape, c.getParent().getBackground(), 4f);
            drawLine(g2, shape, new Color(150, 150, 150), 1.5f);
        }

        g2.dispose();
    }

    private void drawLine(Graphics2D g2, Shape shape, Color color, float size) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(UIScale.scale(size), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(shape);
    }

    @Override
    public float getValue(Component c) {
        return ((AbstractButton) c).isSelected() ? 1 : 0;
    }

    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }
}
