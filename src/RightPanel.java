import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the panel on the right side which contains all the
 * elements like rooms
 */
public class RightPanel extends JPanel implements Serializable {
    private static final long serialVersionUID = -8653344894680136701L;
    private final List<Room> rooms;
    private Room selectedRoom = null;
    private int initialX, initialY;

    public RightPanel() {
        this.rooms = new ArrayList<>();

        setBackground(Color.BLACK);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Check if a room is clicked
                selectedRoom = getRoomAt(e.getX(), e.getY());
                if (selectedRoom != null) {
                    initialX = selectedRoom.getX();
                    initialY = selectedRoom.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedRoom != null) {
                    if (checkOverlap(selectedRoom)) {
                        // Show error message if overlap
                        JOptionPane.showMessageDialog(RightPanel.this, "Room overlaps with another room!", "Error", JOptionPane.ERROR_MESSAGE);
                        // Snap back to the original position
                        selectedRoom.setX(initialX);
                        selectedRoom.setY(initialY);
                    }
                    repaint(); // Repaint the panel after release
                    selectedRoom = null; // Reset selected room
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedRoom != null) {
                    // Update the room's position based on the mouse drag
                    int newX = e.getX() - selectedRoom.getWidth() / 2;  // Center the room on the mouse
                    int newY = e.getY() - selectedRoom.getHeight() / 2; // Center the room on the mouse

                    // Update the room’s position
                    selectedRoom.setX(newX);
                    selectedRoom.setY(newY);

                    // Repaint the panel to reflect the movement of the room and its contents (doors and windows)
                    repaint();
                }
            }
        });
    }


    public void addRoom(Room room) {
        System.out.println("--------ADD ROOM: Before:" + rooms.size());
        for (Room existingRoom : rooms) {
            if (checkOverlap(room.getX(), room.getY(), room.getWidth(), room.getHeight(), existingRoom.getX(), existingRoom.getY(), existingRoom.getWidth(), existingRoom.getHeight())) {
                JOptionPane.showMessageDialog(this, "Room overlaps with another room!");
                return;
            }
        }
        rooms.add(room);
        System.out.println("--------ADD ROOM: AFTER :" + rooms.size());
        repaint();
    }

    public boolean hasNoRooms() {
        return rooms.isEmpty();
    }

    private boolean checkOverlap(Room room) {
        for (Room existingRoom : rooms) {
            // Ensure the room is not checking itself
            if (room != existingRoom && checkOverlap(room.getX(), room.getY(), room.getWidth(), room.getHeight(),
                    existingRoom.getX(), existingRoom.getY(), existingRoom.getWidth(), existingRoom.getHeight())) {
                return true; // Return true if there is an overlap
            }
        }
        return false; // Return false if no overlap
    }

    // Helper method to check if two rectangular objects overlap
    public boolean checkOverlap(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return (x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Ensure parent panel is painted first

        if (rooms != null && !rooms.isEmpty()) {
            for (Room room : rooms) {
                // Draw the room
                Graphics2D g2d = (Graphics2D) g;
                g.setColor(room.getColor());
                g.fillRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());

                // Draw walls (static, unchanging)
                if (room.getWalls() != null) {
                    for (Wall wall : room.getWalls()) {
                        g.setColor(wall.getColor());
                        g.fillRect(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
                    }
                }

                // Draw doors
                if (room.getDoors() != null) {
                    for (Door door : room.getDoors()) {
                        g.setColor(room.getColor());
                        g.fillRect(door.getX(), door.getY(), door.getWidth(), door.getHeight());
                    }
                }

                // Draw windows
                if (room.getWindows() != null) {
                    for (Window window : room.getWindows()) {
                        g.setColor(room.getColor());
                        g.fillRect(window.getX(), window.getY(), window.getWidth(), window.getHeight());

                        // Optional: Draw a line to represent the window gap
                        if (window.getHeight() > window.getWidth()) { // Vertical window
                            g2d.drawLine(window.getX() + window.getWidth() / 2, window.getY(),
                                    window.getX() + window.getWidth() / 2, window.getY() + window.getHeight());
                        } else { // Horizontal window
                            g2d.drawLine(window.getX(), window.getY() + window.getHeight() / 2,
                                    window.getX() + window.getWidth(), window.getY() + window.getHeight() / 2);
                        }
                    }
                }
                    g.setColor(Color.WHITE); // Set color for the room name
                    g.drawString(room.getName(), room.getX() + 5, room.getY() + 15);

            }
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    private Room getRoomAt(int x, int y) {
        for (Room room : rooms) {
            if (x >= room.getX() && x <= room.getX() + room.getWidth() &&
                    y >= room.getY() && y <= room.getY() + room.getHeight()) {
                return room;
            }
        }
        return null;
    }
}