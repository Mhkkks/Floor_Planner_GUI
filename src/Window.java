import java.awt.*;
import java.io.Serializable;

public class Window implements Serializable {
    private static final long serialVersionUID = 1L;
    private int x, y, width, height;
    private Color color;
    private boolean isBetweenRooms;

    // Constructor to initialize a window's properties
    public Window(int x, int y, int width, int height, Color color, boolean isBetweenRooms) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.isBetweenRooms = isBetweenRooms;
    }

    // Method to move the window by a specified delta (dx, dy)
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    // Method to get the bounds of the window as a Rectangle
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters and setters for the window's properties
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Checks if the window is between two rooms
    public boolean isBetweenRooms() {
        return isBetweenRooms;
    }

    public void setBetweenRooms(boolean isBetweenRooms) {
        this.isBetweenRooms = isBetweenRooms;
    }
}
