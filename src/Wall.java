import java.awt.*;
import java.io.Serializable;

public class Wall implements Serializable {
    private static final long serialVersionUID = 1L;
    private int x, y, width, height;

    public Wall(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Getter methods
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() { return height; }

    public Color getColor() {
        return Color.black;
    }
}