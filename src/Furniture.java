import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;

public class Furniture implements Serializable {
    public String furnitureType;
    public Room room; // Room containing the furniture
    public int x, y;  // Position (top-left corner of the furniture)
    public int width, height; // Dimensions of the furniture
    public int rotation; // Rotation angle in degrees

    // Static map to cache furniture images
    private static final HashMap<String, Image> furnitureImages = new HashMap<>();

    static {
        // Preload images to optimize performance
        try {
            furnitureImages.put("bed", ImageIO.read(new File("\"C:\\Users\\91838\\Pictures\\Screenshots\\Screenshot 2024-11-23 162008.png")));
            furnitureImages.put("chair", ImageIO.read(new File("./Images/Furnitures/chair.png")));
            furnitureImages.put("dining_set", ImageIO.read(new File("./Images/Furnitures/dining_set.png")));
            furnitureImages.put("sofa", ImageIO.read(new File("./Images/Furnitures/sofa.png")));
            furnitureImages.put("table", ImageIO.read(new File("./Images/Furnitures/table.png")));
            furnitureImages.put("commode", ImageIO.read(new File("./Images/Furnitures/commode.png")));
            furnitureImages.put("closet", ImageIO.read(new File("./Images/Furnitures/closet.png")));
            furnitureImages.put("shower", ImageIO.read(new File("./Images/Furnitures/shower.png")));
            furnitureImages.put("kit_sink", ImageIO.read(new File("./Images/Furnitures/kitchensink.png")));
            furnitureImages.put("stove", ImageIO.read(new File("./Images/Furnitures/stove.png")));
        } catch (IOException e) {
            System.out.println("Error loading furniture images: " + e.getMessage());
        }
    }

    // Constructor
    public Furniture(String furnitureType, int x, int y, int width, int height, int rotation, List<Room> roomList) {
        this.furnitureType = furnitureType;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        assignRoom(roomList); // Assign the room based on position
    }

    // Assigns the furniture to the room containing it
    private void assignRoom(List<Room> roomList) {
        for (Room room : roomList) {
            if (isWithinRoom(room)) {
                this.room = room;
                return;
            }
        }
        this.room = null; // No room contains this furniture
    }

    // Checks if the furniture is within the given room
    private boolean isWithinRoom(Room room) {
        return this.x >= room.getX() && this.x + this.width <= room.getX() + room.getWidth() &&
                this.y >= room.getY() && this.y + this.height <= room.getY() + room.getHeight();
    }

    // Checks if the furniture overlaps with other furniture
    public boolean checkOverlap(List<Furniture> furnitureList) {
        for (Furniture other : furnitureList) {
            if (this != other && overlaps(other)) {
                return true;
            }
        }
        return false;
    }

    // Checks for overlap with a specific piece of furniture
    private boolean overlaps(Furniture other) {
        return !(this.x + this.width <= other.x || this.x >= other.x + other.width ||
                this.y + this.height <= other.y || this.y >= other.y + other.height);
    }

    // Checks if the furniture overlaps with any walls
    public boolean checkWallOverlap(List<Wall> wallList) {
        for (Wall wall : wallList) {
            if (overlapsWall(wall)) {
                return true;
            }
        }
        return false;
    }

    // Checks for overlap with a specific wall
    private boolean overlapsWall(Wall wall) {
        if (wall.getWidth() == 0) { // Vertical wall
            return this.x < wall.getX() && this.x + this.width > wall.getX() &&
                    this.y < wall.getY() + wall.getHeight() && this.y + this.height > wall.getY();
        } else if (wall.getHeight() == 0) { // Horizontal wall
            return this.y < wall.getY() && this.y + this.height > wall.getY() &&
                    this.x < wall.getX() + wall.getWidth() && this.x + this.width > wall.getX();
        }
        return false;
    }

    // Draws the furniture on the canvas
    public void draw(Graphics g) {
        if (!furnitureImages.containsKey(furnitureType)) {
            System.out.println("Furniture type not found: " + furnitureType);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        // Calculate the center for rotation
        int centerX = this.x + this.width / 2;
        int centerY = this.y + this.height / 2;

        // Apply rotation
        double radians = Math.toRadians(rotation);
        g2d.rotate(radians, centerX, centerY);

        // Draw the image
        g2d.drawImage(furnitureImages.get(this.furnitureType), this.x, this.y, this.width, this.height, null);

        // Restore the original transform
        g2d.setTransform(originalTransform);
    }
}