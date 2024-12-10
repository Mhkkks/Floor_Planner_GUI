import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x, y, width, height;
    private Color color;
    private String name;

    private final List<Wall> walls; // Each room has its own walls
    private final List<Door> doors; // Each room has its own doors
    private final List<Window> windows; // Each room has its own windows

    // Constructor
    public Room(int x, int y, int width, int height, Color color, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.name = name;

        this.walls = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.windows = new ArrayList<>();
        initializeWalls();
    }

    // Initialize the walls when the room is created
    private void initializeWalls() {
        // Top wall
        walls.add(new Wall(x, y, width, 10));
        // Bottom wall
        walls.add(new Wall(x, y + height - 10, width, 10));
        // Left wall
        walls.add(new Wall(x, y, 10, height));
        // Right wall
        walls.add(new Wall(x + width - 10, y, 10, height));
    }

    public void setX(int x) {
        this.x = x;
        updateWallPositions();
    }

    public void setY(int y) {
        this.y = y;
        updateWallPositions();
    }

    public void setWidth(int width) {
        this.width = width;
        updateWallPositions();
    }

    public void setHeight(int height) {
        this.height = height;
        updateWallPositions();
    }

    // Update the positions of all walls when the room changes
    private void updateWallPositions() {
        // Update the walls based on the new position of the room
        walls.get(0).setX(x); // Top wall
        walls.get(0).setY(y);
        walls.get(0).setWidth(width);

        walls.get(1).setX(x); // Bottom wall
        walls.get(1).setY(y + height - 10);
        walls.get(1).setWidth(width);

        walls.get(2).setX(x); // Left wall
        walls.get(2).setY(y);
        walls.get(2).setHeight(height);

        walls.get(3).setX(x + width - 10); // Right wall
        walls.get(3).setY(y);
        walls.get(3).setHeight(height);
    }

    public void addDoor(Door door) {
        if (!checkOverlapWithDoors(door)) {
            doors.add(door);
        } else {
            throw new IllegalArgumentException("Door overlaps with an existing door!");
        }
    }

    public void addWindow(Window window) {
        if (!checkOverlapWithWindows(window)) {
            windows.add(window);
        } else {
            throw new IllegalArgumentException("Window overlaps with an existing window!");
        }
    }

    private boolean checkOverlapWithDoors(Door door) {
        for (Door existingDoor : doors) {
            if (overlap(door.getX(), door.getY(), door.getWidth(), door.getHeight(),
                    existingDoor.getX(), existingDoor.getY(), existingDoor.getWidth(), existingDoor.getHeight())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOverlapWithWindows(Window window) {
        for (Window existingWindow : windows) {
            if (overlap(window.getX(), window.getY(), window.getWidth(), window.getHeight(),
                    existingWindow.getX(), existingWindow.getY(), existingWindow.getWidth(), existingWindow.getHeight())) {
                return true;
            }
        }
        return false;
    }

    // Helper method to check overlap
    private boolean overlap(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return (x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2);
    }

    public List<Wall> getWalls() {
        return new ArrayList<>(walls); // Return a copy to prevent external modification
    }

    public List<Door> getDoors() {
        return new ArrayList<>(doors); // Return a copy to prevent external modification
    }

    public List<Window> getWindows() {
        return new ArrayList<>(windows); // Return a copy to prevent external modification
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Room [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", color=" + color + ", name=" + name + "]";
    }
}