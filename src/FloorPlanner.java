
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import javax.swing.*;

/**
 *
 * This is the main class which loads left and right panel.
 *
 */
public class FloorPlanner extends JFrame {

	private transient JComboBox<String> presentRooms = new JComboBox<>();
	private transient JComboBox<String> selectRoomDropdown = new JComboBox<>();
	private transient JComboBox<String> directionSelection = new JComboBox<>();
	private transient JComboBox<String> roomTypeBox = new JComboBox<>();
	private JCheckBox betweenRoomsCheckbox = new JCheckBox("Between Rooms");

	private JTextField widthField = new JTextField(10);
	private JTextField heightField = new JTextField(10);
	private RightPanel floorCanvas = new RightPanel();

	public static void main(String args[]) {
		JFrame frame = new JFrame("2D Floor Planner");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 500);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		FloorPlanner floorPlanner = new FloorPlanner();
		floorPlanner.addLeftPanel(frame);
		floorPlanner.addRightPanel(frame);
		floorPlanner.addExitButton(frame);
		floorPlanner.widthField.setText("100");
		floorPlanner.heightField.setText("100");
	}

	public String[] getRoomNames() {
		if (floorCanvas.getRooms() == null || floorCanvas.getRooms().size() < 1) {
			return new String[0];
		}

		String[] roomsArray = new String[floorCanvas.getRooms().size()];
		for (int g = 0; g < floorCanvas.getRooms().size(); g++) {
			Room rm = floorCanvas.getRooms().get(g);
			roomsArray[g] = rm.getName();
		}

		return roomsArray;
	}

	private HashMap<String, Integer> roomCounters = new HashMap();

	public void addRightPanel(JFrame frame) {
		floorCanvas.setBackground(Color.lightGray);
		floorCanvas.setSize(400, 500);
		frame.add(floorCanvas);
		frame.setVisible(true);
	}

	public void addLeftPanel(JFrame frame) {
		JPanel canvas = new JPanel();
		canvas.setSize(400, 500);
		canvas.setLayout(new GridLayout(10, 1, 0, 0));
		canvas.setAlignmentY(0);

		canvas.setBorder(BorderFactory.createTitledBorder("Control Panel"));

		roomCounters = new HashMap<>();
		roomCounters.put("Bedroom", 1);
		roomCounters.put("Bathroom", 1);
		roomCounters.put("Kitchen", 1);
		roomCounters.put("Drawing room", 1);

		// Room type selection
		roomTypeBox = new JComboBox<>(new String[] { "Bedroom", "Bathroom", "Kitchen", "Drawing room" });

		JPanel canvasRoomType = new JPanel();
		canvasRoomType.setBackground(Color.lightGray);
		canvasRoomType.add(new JLabel("Room Type:"));
		canvasRoomType.add(roomTypeBox);
		canvas.add(canvasRoomType);

		JPanel canvasRoomHeight = new JPanel();
		canvasRoomHeight.add(new JLabel("Room Height:"));
		canvasRoomHeight.add(heightField);
		canvas.add(canvasRoomHeight);

		JPanel canvasRoomWidth = new JPanel();
		canvasRoomWidth.setBackground(Color.lightGray);
		canvasRoomWidth.add(new JLabel("Room Width:"));
		canvasRoomWidth.add(widthField);
		canvas.add(canvasRoomWidth);

		JPanel canvasPositionBox = new JPanel();

		directionSelection = new JComboBox<>(new String[] {  "North-Left", "North-Center", "North-Right",
				"South-Left", "South-Center", "South-Right",
				"East-Top", "East-Center", "East-Bottom",
				"West-Top", "West-Center", "West-Bottom" });
		canvasPositionBox.add(new JLabel("Relative Position"));
		canvasPositionBox.add(directionSelection);
		canvas.add(canvasPositionBox);
		JPanel canvasExistingRooms = new JPanel();
		canvasExistingRooms.setBackground(Color.lightGray);

		refreshRoomDropdown();
		canvasExistingRooms.add(new JLabel("Existing Room:"));
		canvasExistingRooms.add(presentRooms);
		canvas.add(canvasExistingRooms);

		JPanel canvasButton = new JPanel();
		JButton addRoomBtn = new JButton("Add Room");
		addRoomBtn.addActionListener(this::addRoomAction);
		canvasButton.add(addRoomBtn);
		addRoomBtn.setBackground(Color.black);
		addRoomBtn.setForeground(Color.WHITE);
		addRoomBtn.setFont(new Font("Arial", Font.BOLD, 16));
		canvas.add(canvasButton);

		JPanel updateAlignmentPanel = new JPanel();
		updateAlignmentPanel.setBackground(Color.LIGHT_GRAY);

		refreshRoomDropdown();
		updateAlignmentPanel.add(new JLabel("Select Room:"));
		updateAlignmentPanel.add(selectRoomDropdown);

		JButton updateAlignmentBtn = new JButton("Update Alignment");
		updateAlignmentBtn.setBackground(Color.black);
		updateAlignmentBtn.setForeground(Color.WHITE);
		updateAlignmentBtn.setFont(new Font("Arial", Font.BOLD, 12));
		updateAlignmentBtn.addActionListener(e -> updateRoomAlignment(selectRoomDropdown));
		updateAlignmentPanel.add(updateAlignmentBtn);
		canvas.add(updateAlignmentPanel);

		// Panel for Add Door and Add Window buttons
		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align buttons horizontally

		JComboBox<String> marginSelection = new JComboBox<>(new String[] { "Top", "Bottom", "Left", "Right" });
		betweenRoomsCheckbox = new JCheckBox("Between Room");

		toolbarPanel.add(new JLabel("Margin:"));
		toolbarPanel.add(marginSelection);
		toolbarPanel.add(betweenRoomsCheckbox);  // Add the checkbox to the panel

		JButton addDoorButton = new JButton("Door");
		addDoorButton.setBackground(Color.black);
		addDoorButton.setForeground(Color.WHITE);
		addDoorButton.addActionListener(e -> {
			String selectedMargin = (String) marginSelection.getSelectedItem();
			addDoorToSelectedRoom(selectedMargin);
		});

		JButton addWindowButton = new JButton("Window");
		addWindowButton.setBackground(Color.black);
		addWindowButton.setForeground(Color.WHITE);
		addWindowButton.addActionListener(e -> {
			String selectedMargin = (String) marginSelection.getSelectedItem();
			boolean isBetweenRooms = betweenRoomsCheckbox.isSelected();
			addWindowToSelectedRoom(selectedMargin, isBetweenRooms);
		});

		toolbarPanel.add(addDoorButton);
		toolbarPanel.add(addWindowButton);

		canvas.add(toolbarPanel);
		JPanel canvasSaveData = new JPanel();

		JButton saveDataBtn = new JButton("Save Plan");
		saveDataBtn.addActionListener(this::saveDataToFile);
		canvasSaveData.add(saveDataBtn);

		JButton loadDataBtn = new JButton("Load Plan");
		canvasSaveData.add(loadDataBtn);

		saveDataBtn.setBackground(Color.GREEN);
		saveDataBtn.setForeground(Color.WHITE);
		saveDataBtn.setFont(new Font("Arial", Font.BOLD, 12));

		loadDataBtn.setBackground(Color.ORANGE);
		loadDataBtn.setForeground(Color.WHITE);
		loadDataBtn.setFont(new Font("Arial", Font.BOLD, 12));

		loadDataBtn.addActionListener(e -> {
			loadDataFromFile(frame);

			if (floorCanvas != null) {

				frame.getContentPane().removeAll();

				frame.setContentPane(floorCanvas);

				frame.revalidate();

				frame.repaint();
			}

			if (floorCanvas != null) {
				floorCanvas.repaint();
				floorCanvas.revalidate();
			}

			frame.repaint();
			frame.revalidate();
		});

		floorCanvas.repaint();
		floorCanvas.revalidate();
		frame.repaint();
		frame.revalidate();

		canvas.add(canvasSaveData);

		if (floorCanvas.hasNoRooms()) {
			directionSelection.setEnabled(false);
			presentRooms.setEnabled(false);
		}

		frame.add(canvas, BorderLayout.WEST);
		addExitButton(frame);

		frame.setVisible(true);
	}

	private void refreshRoomDropdown() {
		presentRooms.removeAllItems();

		String[] roomNames = getRoomNames();
		for (String roomName : roomNames) {
			presentRooms.addItem(roomName);
			Room room = getRoomByName(roomName);
			if (room != null) {

				addRoom(room);
			}
		}

		boolean hasRooms = roomNames.length > 0;
		directionSelection.setEnabled(hasRooms);
		presentRooms.setEnabled(hasRooms);

		refreshAlignmentDropdown(selectRoomDropdown);
	}

	private void refreshAlignmentDropdown(JComboBox<String> selectRoomDropdown) {
		selectRoomDropdown.removeAllItems();
		String[] roomNames = getRoomNames();
		for (String roomName : roomNames) {
			selectRoomDropdown.addItem(roomName);
		}
		selectRoomDropdown.setEnabled(roomNames.length > 0);
	}
	private void updateRoomAlignment(JComboBox<String> selectRoomDropdown) {
		try {
			String selectedRoomName = (String) selectRoomDropdown.getSelectedItem();
			if (selectedRoomName == null || selectedRoomName.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please select a room to update.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			Room selectedRoom = getRoomByName(selectedRoomName);
			if (selectedRoom == null) {
				JOptionPane.showMessageDialog(null, "Room not found.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}


			String newPosition = (String) directionSelection.getSelectedItem();
			String baseRoomName = (String) presentRooms.getSelectedItem();
			Room baseRoom = getRoomByName(baseRoomName);

			if (baseRoom == null) {
				JOptionPane.showMessageDialog(null, "Base room for alignment not found.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Calculate new position
			int newX = baseRoom.getX();
			int newY = baseRoom.getY();
			switch (newPosition) {
				case "North-Left":
					newX = baseRoom.getX(); // Align left wall
					newY = baseRoom.getY() - selectedRoom.getHeight(); // Above the base room
					break;
				case "North-Center":
					newX = baseRoom.getX() + (baseRoom.getWidth() - selectedRoom.getWidth()) / 2; // Centered horizontally
					newY = baseRoom.getY() - selectedRoom.getHeight();
					break;
				case "North-Right":
					newX = baseRoom.getX() + baseRoom.getWidth() - selectedRoom.getWidth();
					newY = baseRoom.getY() - selectedRoom.getHeight();
					break;
				case "South-Left":
					newX = baseRoom.getX();
					newY = baseRoom.getY() + baseRoom.getHeight();
					break;
				case "South-Center":
					newX = baseRoom.getX() + (baseRoom.getWidth() - selectedRoom.getWidth()) / 2;
					newY = baseRoom.getY() + baseRoom.getHeight();
					break;
				case "South-Right":
					newX = baseRoom.getX() + baseRoom.getWidth() - selectedRoom.getWidth();
					newY = baseRoom.getY() + baseRoom.getHeight();
					break;
				case "East-Top":
					newX = baseRoom.getX() + baseRoom.getWidth();
					newY = baseRoom.getY();
					break;
				case "East-Center":
					newX = baseRoom.getX() + baseRoom.getWidth();
					newY = baseRoom.getY() + (baseRoom.getHeight() - selectedRoom.getHeight()) / 2;
					break;
				case "East-Bottom":
					newX = baseRoom.getX() + baseRoom.getWidth();
					newY = baseRoom.getY() + baseRoom.getHeight() - selectedRoom.getHeight();
					break;
				case "West-Top":
					newX = baseRoom.getX() - selectedRoom.getWidth();
					newY = baseRoom.getY();
					break;
				case "West-Center":
					newX = baseRoom.getX() - selectedRoom.getWidth();
					newY = baseRoom.getY() + (baseRoom.getHeight() - selectedRoom.getHeight()) / 2;
					break;
				case "West-Bottom":
					newX = baseRoom.getX() - selectedRoom.getWidth();
					newY = baseRoom.getY() + baseRoom.getHeight() - selectedRoom.getHeight();
					break;
				default:
					JOptionPane.showMessageDialog(null, "Invalid alignment position selected.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
			}


			for (Room room : floorCanvas.getRooms()) {
				if (!room.getName().equals(selectedRoomName) &&
						floorCanvas.checkOverlap(newX, newY, selectedRoom.getWidth(), selectedRoom.getHeight(), room.getX(), room.getY(), room.getWidth(), room.getHeight())) {
					JOptionPane.showMessageDialog(null, "The new alignment causes an overlap.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}


			selectedRoom.setX(newX);
			selectedRoom.setY(newY);
			floorCanvas.repaint();

			JOptionPane.showMessageDialog(null, "Room alignment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Failed to update room alignment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void addRoomAction(ActionEvent e) {
		try {
			// Get the room dimensions and type
			System.out.println("--------addRoomAction---------");

			int width = Integer.parseInt(widthField.getText());
			int height = Integer.parseInt(heightField.getText());
			String roomType = (String) roomTypeBox.getSelectedItem();
			String position = (String) directionSelection.getSelectedItem();
			String existingRoomName = (String) presentRooms.getSelectedItem();

			System.out.println("Position is:" + position + ", width:" + width + ", height:" + height);
			// Define color based on room type
			Color roomColor;
			switch (roomType) {
			case "Bedroom":
				roomColor = Color.GREEN;
				break;
			case "Bathroom":
				roomColor = Color.BLUE;
				break;
			case "Kitchen":
				roomColor = Color.RED;
				break;
			case "Drawing room":
				roomColor = Color.YELLOW;
				break;
			default:
				roomColor = Color.LIGHT_GRAY;
				break;
			}

			int newX = 700, newY = 350; // Default position for first room

			if (floorCanvas.getRooms() == null || floorCanvas.getRooms().size() < 1) {
			} else {
				Room existingRoom = getRoomByName(existingRoomName);
				if (existingRoom == null) {
					return;
				}

				// Calculate the position of the new room relative to the selected room
				newX = existingRoom.getX();
				newY = existingRoom.getY();

				switch (position) {
					case "North-Left":
						newX = existingRoom.getX();
						newY = existingRoom.getY() - height;
						break;
					case "North-Center":
						newX = existingRoom.getX() + (existingRoom.getWidth() - width) / 2;
						newY = existingRoom.getY() - height;
						break;
					case "North-Right":
						newX = existingRoom.getX() + existingRoom.getWidth() - width;
						newY = existingRoom.getY() - height;
						break;
					case "South-Left":
						newX = existingRoom.getX();
						newY = existingRoom.getY() + existingRoom.getHeight();
						break;
					case "South-Center":
						newX = existingRoom.getX() + (existingRoom.getWidth() - width) / 2;
						newY = existingRoom.getY() + existingRoom.getHeight();
						break;
					case "South-Right":
						newX = existingRoom.getX() + existingRoom.getWidth() - width;
						newY = existingRoom.getY() + existingRoom.getHeight();
						break;
					case "East-Top":
						newX = existingRoom.getX() + existingRoom.getWidth();
						newY = existingRoom.getY();
						break;
					case "East-Center":
						newX = existingRoom.getX() + existingRoom.getWidth();
						newY = existingRoom.getY() + (existingRoom.getHeight() - height) / 2;
						break;
					case "East-Bottom":
						newX = existingRoom.getX() + existingRoom.getWidth();
						newY = existingRoom.getY() + existingRoom.getHeight() - height;
						break;
					case "West-Top":
						newX = existingRoom.getX() - width;
						newY = existingRoom.getY();
						break;
					case "West-Center":
						newX = existingRoom.getX() - width;
						newY = existingRoom.getY() + (existingRoom.getHeight() - height) / 2;
						break;
					case "West-Bottom":
						newX = existingRoom.getX() - width;
						newY = existingRoom.getY() + existingRoom.getHeight() - height;
						break;
				}

			}

			// Increment the room counter for the selected room type
			int count = roomCounters.get(roomType);

			String roomName = roomType + " " + count;
			count++;
			roomCounters.put(roomType, count);

			System.out.println("newX:" + newX);
			System.out.println("newY:" + newY);
			System.out.println("width:" + width);
			System.out.println("height:" + height);

			Room rm = new Room(newX, newY, width, height, roomColor, roomName);
			floorCanvas.addRoom(rm);
			refreshRoomDropdown();


		} catch (NumberFormatException ex) {
			ex.printStackTrace();

		}
	}

	private void addDoorToSelectedRoom(String selectedMargin) {
		try {
			String selectedRoomName = (String) selectRoomDropdown.getSelectedItem();
			if (selectedRoomName == null || selectedRoomName.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please select a room to add a door.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			Room selectedRoom = getRoomByName(selectedRoomName);
			if (selectedRoom == null) {
				JOptionPane.showMessageDialog(this, "Selected room not found.", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			}

			String roomName = selectedRoom.getName();  // Get the name of the room

			// Prevent adding a door to the outside for "Bedroom" and "Bathroom"
			if (roomName.startsWith("Bedroom") || roomName.startsWith("Bathroom")) {
				if (!isRoomAdjacent(selectedRoom, selectedMargin)) {
					JOptionPane.showMessageDialog(this, "There is no adjacent room in the " + selectedMargin + " direction.", "Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("No adjacent room for Bedroom/Bathroom. Aborting door addition.");
					return;
				}
				System.out.println("Adjacent room found for Bedroom/Bathroom. Proceeding with door addition.");
			}

			// Proceed with the logic to add a door, assuming selectedMargin specifies door position (Left, Right, Top, Bottom)
			int doorWidth = 30;
			int doorHeight = 10;
			int doorX = 0, doorY = 0;

			switch (selectedMargin) {
				case "Top":
					doorX = selectedRoom.getX() + selectedRoom.getWidth() / 2 - doorWidth / 2;
					doorY = selectedRoom.getY();
					break;

				case "Bottom":
					doorX = selectedRoom.getX() + selectedRoom.getWidth() / 2 - doorWidth / 2;
					doorY = selectedRoom.getY() + selectedRoom.getHeight() - doorHeight;
					break;

				case "Left":
					doorX = selectedRoom.getX();
					doorY = selectedRoom.getY() + selectedRoom.getHeight() / 2 - doorHeight / 2;
					break;

				case "Right":
					doorX = selectedRoom.getX() + selectedRoom.getWidth() - doorWidth;
					doorY = selectedRoom.getY() + selectedRoom.getHeight() / 2 - doorHeight / 2;
					break;

				default:
					JOptionPane.showMessageDialog(this, "Invalid margin selection.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
			}

			// Check if the new door overlaps with any existing window or door
			if (isOverlapWithExistingOpenings(selectedRoom, doorX, doorY, doorWidth, doorHeight)) {
				JOptionPane.showMessageDialog(this, "Door overlaps with an existing window or door.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Add the door to the room
			selectedRoom.addDoor(new Door(doorX, doorY, doorWidth, doorHeight, selectedRoom.getColor(), selectedRoom.getDoors(), selectedRoom.getWindows()));
			floorCanvas.repaint();  // Refresh the canvas to display the door

			JOptionPane.showMessageDialog(this, "Door added successfully to the " + selectedMargin + " margin of " + selectedRoomName + ".", "Success", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Failed to add door: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void addWindowToSelectedRoom(String selectedMargin, boolean isBetweenRooms) {
		try {
			String selectedRoomName = (String) selectRoomDropdown.getSelectedItem();
			if (selectedRoomName == null || selectedRoomName.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please select a room to add a window.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}


			Room selectedRoom = getRoomByName(selectedRoomName);
			if (selectedRoom == null) {
				JOptionPane.showMessageDialog(this, "Selected room not found.", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			}

			System.out.println("Selected Room Type: " + selectedRoom.getName());

			// Check if the room is Bedroom or Bathroom
			if (selectedRoom.getName().startsWith("Bedroom") || selectedRoom.getName().startsWith("Bathroom")) {
				if (!isRoomAdjacent(selectedRoom, selectedMargin)) {
					JOptionPane.showMessageDialog(this, "There is no adjacent room in the " + selectedMargin + " direction.", "Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("No adjacent room for Bedroom/Bathroom. Aborting window addition.");
					return;
				}
				System.out.println("Adjacent room found for Bedroom/Bathroom. Proceeding with window addition.");
			}

			int totalWidth = 30, totalHeight = 10;
			int windowX1 = 0, windowY1 = 0;
			int windowX2 = 0, windowY2 = 0;
			int gap = 10;

			switch (selectedMargin) {
				case "Top":
					windowX1 = selectedRoom.getX() + selectedRoom.getWidth() / 2 - totalWidth / 2;
					windowY1 = selectedRoom.getY();
					windowX2 = windowX1 + totalWidth / 2 + gap;
					windowY2 = windowY1;
					break;

				case "Bottom":
					windowX1 = selectedRoom.getX() + selectedRoom.getWidth() / 2 - totalWidth / 2;
					windowY1 = selectedRoom.getY() + selectedRoom.getHeight() - totalHeight;
					windowX2 = windowX1 + totalWidth / 2 + gap;
					windowY2 = windowY1;
					break;

				case "Left":
					windowX1 = selectedRoom.getX();
					windowY1 = selectedRoom.getY() + selectedRoom.getHeight() / 2 - totalHeight;
					windowX2 = windowX1;
					windowY2 = windowY1 + totalHeight + gap;
					break;

				case "Right":
					windowX1 = selectedRoom.getX() + selectedRoom.getWidth() - totalHeight;
					windowY1 = selectedRoom.getY() + selectedRoom.getHeight() / 2 - totalHeight / 2;
					windowX2 = windowX1;
					windowY2 = windowY1 + totalHeight + gap;
					break;

				default:
					JOptionPane.showMessageDialog(this, "Invalid margin selection.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
			}


			if (isOverlapWithExistingOpenings(selectedRoom, windowX1, windowY1, totalWidth / 2 - gap / 2, totalHeight) ||
					isOverlapWithExistingOpenings(selectedRoom, windowX2, windowY2, totalWidth / 2 - gap / 2, totalHeight)) {
				JOptionPane.showMessageDialog(this, "Window overlaps with an existing door or window.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Add both window parts to the room
			selectedRoom.addWindow(new Window(windowX1, windowY1, totalWidth / 2 - gap / 2, totalHeight, selectedRoom.getColor(), isBetweenRooms));
			selectedRoom.addWindow(new Window(windowX2, windowY2, totalWidth / 2 - gap / 2, totalHeight, selectedRoom.getColor(), isBetweenRooms));
			floorCanvas.repaint();

			JOptionPane.showMessageDialog(this, "Window added successfully to the " + selectedMargin + " margin of " + selectedRoomName + ".", "Success", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Failed to add window: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}


	private boolean isOverlapWithExistingOpenings(Room room, int x, int y, int width, int height) {

		for (Window w : room.getWindows()) {
			if (x < w.getX() + w.getWidth() && x + width > w.getX() &&
					y < w.getY() + w.getHeight() && y + height > w.getY()) {
				return true;
			}
		}

		for (Door d : room.getDoors()) {
			if (x < d.getX() + d.getWidth() && x + width > d.getX() &&
					y < d.getY() + d.getHeight() && y + height > d.getY()) {
				return true;
			}
		}

		return false;
	}


	private Room[] allRooms = new Room[100];
	private int roomCount = 0;

	private void addRoom(Room newRoom) {
		if (roomCount == allRooms.length) {

			Room[] newRooms = new Room[allRooms.length * 2];
			System.arraycopy(allRooms, 0, newRooms, 0, allRooms.length);
			allRooms = newRooms;
		}
		allRooms[roomCount++] = newRoom;
	}

	private boolean isRoomAdjacent(Room currentRoom, String direction) {
		for (Room otherRoom : allRooms) {
			if (currentRoom == otherRoom) {
				continue;
			}

			switch (direction) {
				case "Left":

					if (currentRoom.getX() == otherRoom.getX() + otherRoom.getWidth() &&
							currentRoom.getY() < otherRoom.getY() + otherRoom.getHeight() &&
							currentRoom.getY() + currentRoom.getHeight() > otherRoom.getY()) {
						return true;
					}
					break;
				case "Right":

					if (currentRoom.getX() + currentRoom.getWidth() == otherRoom.getX() &&
							currentRoom.getY() < otherRoom.getY() + otherRoom.getHeight() &&
							currentRoom.getY() + currentRoom.getHeight() > otherRoom.getY()) {
						return true;
					}
					break;
				case "Top":

					if (currentRoom.getY() == otherRoom.getY() + otherRoom.getHeight() &&
							currentRoom.getX() < otherRoom.getX() + otherRoom.getWidth() &&
							currentRoom.getX() + currentRoom.getWidth() > otherRoom.getX()) {
						return true;
					}
					break;
				case "Bottom":

					if (currentRoom.getY() + currentRoom.getHeight() == otherRoom.getY() &&
							currentRoom.getX() < otherRoom.getX() + otherRoom.getWidth() &&
							currentRoom.getX() + currentRoom.getWidth() > otherRoom.getX()) {
						return true;
					}
					break;
			}
		}
		return false;
	}

	private void moveRoom(Room roomToMove, int deltaX, int deltaY) {

		roomToMove.setX(roomToMove.getX() + deltaX);
		roomToMove.setY(roomToMove.getY() + deltaY);


		for (Door door : roomToMove.getDoors()) {
			door.setX(door.getX() + deltaX);
			door.setY(door.getY() + deltaY);
		}


		for (Window window : roomToMove.getWindows()) {
			window.setX(window.getX() + deltaX);
		}


		if (isOverlapWithOtherRooms(roomToMove) || isOverlapWithExistingOpenings(roomToMove)) {

			roomToMove.setX(roomToMove.getX() - deltaX);
			roomToMove.setY(roomToMove.getY() - deltaY);
			for (Door door : roomToMove.getDoors()) {
				door.setX(door.getX() - deltaX);
				door.setY(door.getY() - deltaY);
			}
			for (Window window : roomToMove.getWindows()) {
				window.setX(window.getX() - deltaX);
				window.setY(window.getY() - deltaY);
			}


			JOptionPane.showMessageDialog(this, "The room cannot be moved because it overlaps with another room or opening.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {

			floorCanvas.repaint();
			JOptionPane.showMessageDialog(this, "Room moved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	private boolean isOverlapWithOtherRooms(Room roomToMove) {
		for (Room otherRoom : allRooms) {
			if (roomToMove == otherRoom) {
				continue;
			}


			if (roomToMove.getX() < otherRoom.getX() + otherRoom.getWidth() &&
					roomToMove.getX() + roomToMove.getWidth() > otherRoom.getX() &&
					roomToMove.getY() < otherRoom.getY() + otherRoom.getHeight() &&
					roomToMove.getY() + roomToMove.getHeight() > otherRoom.getY()) {
				return true;
			}
		}
		return false;
	}


	private boolean isOverlapWithExistingOpenings(Room roomToMove) {
		for (Door door : roomToMove.getDoors()) {

			if (isOverlapWithExistingOpeningsInRoom(roomToMove, door.getX(), door.getY(), door.getWidth(), door.getHeight())) {
				return true;
			}
		}

		for (Window window : roomToMove.getWindows()) {

			if (isOverlapWithExistingOpeningsInRoom(roomToMove, window.getX(), window.getY(), window.getWidth(), window.getHeight())) {
				return true;
			}
		}

		return false;
	}

	// Check if the given coordinates overlap with existing openings (doors or windows) in the room
	private boolean isOverlapWithExistingOpeningsInRoom(Room room, int x, int y, int width, int height) {
		for (Window w : room.getWindows()) {
			if (x < w.getX() + w.getWidth() && x + width > w.getX() &&
					y < w.getY() + w.getHeight() && y + height > w.getY()) {
				return true;
			}
		}

		for (Door d : room.getDoors()) {
			if (x < d.getX() + d.getWidth() && x + width > d.getX() &&
					y < d.getY() + d.getHeight() && y + height > d.getY()) {
				return true;
			}
		}

		return false;
	}
	public Room getRoomByName(String name) {
		for (Room room : floorCanvas.getRooms()) {
			if (room.getName().equals(name)) {
				System.out.println("Room Returned for :" + name + ":" + room);
				return room;
			}
		}
		System.out.println("NO Room Returned for :" + name);
		return null;
	}

	void addExitButton(JFrame frame) {
		JPanel canvasButton = new JPanel();
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(e -> System.exit(0));
		canvasButton.add(exitButton);
		frame.add(canvasButton, BorderLayout.SOUTH);
	}


	private void saveDataToFile(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Plan");
		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Plan Files", "dat"));  // Filter for .dat files

		int result = fileChooser.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			String filePath = selectedFile.getAbsolutePath();
			if (!filePath.endsWith(".dat")) {
				selectedFile = new File(filePath + ".dat");
			}

			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
				oos.writeObject(floorCanvas);
				JOptionPane.showMessageDialog(null, "Plan saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Failed to save the plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	void loadDataFromFile(JFrame frame) {
		// Initialize JFileChooser for file selection
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open Plan");
		fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Plan Files", "dat"));

		// Show file chooser dialog
		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile))) {
				// Deserialize the floor plan object (RightPanel) from the .dat file
				floorCanvas = (RightPanel) ois.readObject();

				// Show success message
				JOptionPane.showMessageDialog(null, "Plan loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

				// Debugging: Print details of the loaded rooms (if any)
				if (floorCanvas != null && floorCanvas.getRooms() != null) {
					System.out.println("Rooms loaded: " + floorCanvas.getRooms().size());
					for (Room room : floorCanvas.getRooms()) {
						System.out.println("Room: " + room.getName());
					}
				} else {
					System.out.println("No rooms loaded or floorCanvas is null.");
				}

				// Ensure the loaded floor plan (floorCanvas) is properly displayed in the frame
				if (floorCanvas != null) {
					// Clear the existing content of the frame
					frame.getContentPane().removeAll();

					// Set the floorCanvas as the content pane
					frame.setContentPane(floorCanvas);

					// Revalidate and repaint the frame to update the UI
					frame.revalidate();
					frame.repaint();

					// Repaint and revalidate the floorCanvas (if it's a custom JPanel with the floor plan)
					floorCanvas.repaint();
					floorCanvas.revalidate();
				}
			} catch (IOException | ClassNotFoundException ex) {
				// Handle file loading exceptions (deserialization issues)
				JOptionPane.showMessageDialog(null, "Failed to load the plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
}

