import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class Door implements Serializable {
    private static final long serialVersionUID = 1L;
    private int x, y, width, height;
    private Color color;  // Color for the door
    private List<Door> doors; // List of doors for updating their positions
    private List<Window> windows; // List of windows for updating their positions
    private int initialX, initialY; // Store initial position for comparison

    // Constructor to initialize door properties and initialize lists
    public Door(int x, int y, int width, int height, Color roomColor, List<Door> doors, List<Window> windows) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = roomColor;  // Set the door color to match the room color
        this.doors = doors;
        this.windows = windows;
        this.initialX = x; // Store the initial position of the door
        this.initialY = y;
    }

    // Getters and Setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
        updateDoorsAndWindowsPosition();
    }

    public void setY(int y) {
        this.y = y;
        updateDoorsAndWindowsPosition();
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

    // Method to update the position of other doors and windows
    private void updateDoorsAndWindowsPosition() {
        // Adjust the positions of other doors
        for (Door door : doors) {
            door.setX(door.getX() + (x - initialX)); // Adjust by the difference in X
            door.setY(door.getY() + (y - initialY)); // Adjust by the difference in Y
        }

        // Adjust the positions of windows
        for (Window window : windows) {
            window.setX(window.getX() + (x - initialX)); // Adjust by the difference in X
            window.setY(window.getY() + (y - initialY)); // Adjust by the difference in Y
        }

        // Update initial position to the new position after movement
        this.initialX = this.x;
        this.initialY = this.y;
    }

    // Methods to add doors and windows
    public void addDoor(Door door) {
        if (!doors.contains(door)) {
            doors.add(door);
        }
    }

    public void addWindow(Window window) {
        if (!windows.contains(window)) {
            windows.add(window);
        }
    }
}
