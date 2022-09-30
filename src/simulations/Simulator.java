package simulations;

import java.awt.event.ActionEvent;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple predator-prey simulator, based on a rectangular field containing
 * rabbits and foxes.
 * 
 * @author David J. Barnes and Michael KÃ¶lling
 * @version 2016.02.29
 */
public class Simulator {
	// Constants representing configuration information for the simulation.
	// The default width for the grid.
	private static final int DEFAULT_WIDTH = 120;
	// The default depth of the grid.
	private static final int DEFAULT_DEPTH = 80;
	// The probability that a fox will be created in any given grid position.
	private static final double FOX_CREATION_PROBABILITY = 0.02;
	// The probability that a rabbit will be created in any given position.
	private static final double RABBIT_CREATION_PROBABILITY = 0.08;

	// Lists of animals in the field.
	private List<Animal> animals;
	// The current state of the field.
	private Field field;
	// The current step of the simulation.
	private int step;
	// A graphical view of the simulation.
	private SimulatorView view;

	/**
	 * Construct a simulation field with default size.
	 */
	public Simulator() {
		this(DEFAULT_DEPTH, DEFAULT_WIDTH);
	}

	/**
	 * Create a simulation field with the given size.
	 * 
	 * @param depth Depth of the field. Must be greater than zero.
	 * @param width Width of the field. Must be greater than zero.
	 */
	public Simulator(int depth, int width) {
		if (width <= 0 || depth <= 0) {
			System.out.println("The dimensions must be >= zero.");
			System.out.println("Using default values.");
			depth = DEFAULT_DEPTH;
			width = DEFAULT_WIDTH;
		}

		animals = new ArrayList<>();
		field = new Field(depth, width);

		// Create a view of the state of each location in the field.
		view = new SimulatorView(depth, width);
		view.setColor(Rabbit.class, Color.ORANGE);
		view.setColor(Fox.class, Color.BLUE);

		// Setup a valid starting point.
		reset();
		// Give functions to the buttons of the view.
		setButtons();

	}

	// get the number of steps from the user and run the simulation.
	private void getUserInput() {
		String input = view.textfield.getText();
		int steps;
		// If the user enters an invalid input, show a message.
		try {
			steps = Integer.parseInt(input);

			if (steps <= 0) {
				JOptionPane.showMessageDialog(view, "Please enter a positive integer.");
			}
			simulate(steps);
		} catch (Exception a) {
			JOptionPane.showMessageDialog(view, "Please enter a positive integer.");
		}
		;
	}

	/**
	 * Give functions to the buttons of the view.
	 */

	public void setButtons() {
		view.oneStepButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				simulateOneStep();

			}
		});

		view.aHundredStepsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				simulateAhundredSteps();

			}
		});

		view.reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				reset();

			}
		});
		view.longsimulation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				runLongSimulation();

			}
		});

		view.button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				getUserInput();

			}
		});
		// Using "enter" key to run the simulation using the user input number of steps.
		view.textfield.setFocusable(true);
		view.textfield.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					getUserInput();

				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	/**
	 * Run the simulation from its current state for a reasonably long period (500
	 * steps).
	 */
	public void runLongSimulation() {
		simulate(500);
	}

	/**
	 * Run the simulation from its current state for 100 steps.
	 */
	public void simulateAhundredSteps() {
		simulate(100);
	}

	/**
	 * Run the simulation for the given number of steps. Stop before the given
	 * number of steps if it ceases to be viable.
	 * 
	 * @param numSteps The number of steps to run for.
	 */
	public void simulate(int numSteps) {
		for (int step = 1; step <= numSteps && view.isViable(field); step++) {
			simulateOneStep();
			// delay(60); // uncomment this to run more slowly
		}
	}

	/**
	 * Run the simulation from its current state for a single step. Iterate over the
	 * whole field updating the state of each fox and rabbit.
	 */
	public void simulateOneStep() {
		step++;

		// Provide space for newborn animals.
		List<Animal> newAnimals = new ArrayList<>();
		// Let all foxes act.
		for (Iterator<Animal> it = animals.iterator(); it.hasNext();) {
			Animal animal = it.next();
			animal.act(newAnimals);
			if (!animal.isAlive()) {
				it.remove();
			}
		}

		// Add the newly born animal to the main list.

		animals.addAll(newAnimals);

		view.showStatus(step, field);
	}

	/**
	 * Reset the simulation to a starting position.
	 */
	public void reset() {
		step = 0;

		animals.clear();
		populate();

		// Show the starting state in the view.
		view.showStatus(step, field);
	}

	/**
	 * Randomly populate the field with foxes and rabbits.
	 */
	private void populate() {
		Random rand = Randomizer.getRandom();
		field.clear();
		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				if (rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
					Location location = new Location(row, col);
					Animal fox = new Fox(true, field, location);
					animals.add(fox);
				} else if (rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
					Location location = new Location(row, col);
					Animal rabbit = new Rabbit(true, field, location);
					animals.add(rabbit);
				}
				// else leave the location empty.
			}
		}
	}

	/**
	 * Pause for a given time.
	 * 
	 * @param millisec The time to pause for, in milliseconds
	 */
	private void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException ie) {
			// wake up
		}
	}

	/**
	 * A graphical view of the simulation grid. The view displays a colored
	 * rectangle for each location representing its contents. It uses a default
	 * background color. Colors for each type of species can be defined using the
	 * setColor method.
	 * 
	 * @author David J. Barnes and Michael Kölling
	 * @version 2016.02.29
	 */
	private class SimulatorView extends JFrame {
		// Colors used for empty locations.
		private final Color EMPTY_COLOR = Color.white;

		// Color used for objects that have no defined color.
		private final Color UNKNOWN_COLOR = Color.gray;

		private final String STEP_PREFIX = "Step: ";
		private final String POPULATION_PREFIX = "Population: ";
		private JLabel stepLabel, population;
		private FieldView fieldView;

		private JPanel panel;
		private JButton oneStepButton;
		private JButton aHundredStepsButton;
		private JButton longsimulation;
		private JLabel input;
		private JTextField textfield;
		private JButton button;
		private JButton reset;

		// A map for storing colors for participants in the simulation
		private Map<Class, Color> colors;
		// A statistics object computing and storing simulation information
		private FieldStats stats;

		/**
		 * Create a view of the given width and height.
		 * 
		 * @param height The simulation's height.
		 * @param width  The simulation's width.
		 */
		public SimulatorView(int height, int width) {
			stats = new FieldStats();
			colors = new LinkedHashMap<>();

			setTitle("Fox and Rabbit Simulation");
			stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
			population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			oneStepButton = new JButton("Run 1 step");
			aHundredStepsButton = new JButton("Run 100 steps");
			longsimulation = new JButton("Run long simulation");
			reset = new JButton("Reset");
			input = new JLabel("Number of steps: ");
			textfield = new JTextField();
			button = new JButton("Run");

			panel.add(oneStepButton);
			panel.add(aHundredStepsButton);
			panel.add(longsimulation);
			panel.add(input);
			panel.add(textfield);
			panel.add(button);
			panel.add(reset);

			setLocation(100, 50);

			fieldView = new FieldView(height, width);

			Container contents = getContentPane();
			contents.add(stepLabel, BorderLayout.WEST);
			contents.add(fieldView, BorderLayout.CENTER);
			contents.add(population, BorderLayout.SOUTH);
			contents.add(panel, BorderLayout.NORTH);
			pack();
			setVisible(true);
		}

		/**
		 * Define a color to be used for a given class of animal.
		 * 
		 * @param animalClass The animal's Class object.
		 * @param color       The color to be used for the given class.
		 */
		public void setColor(Class animalClass, Color color) {
			colors.put(animalClass, color);
		}

		/**
		 * @return The color to be used for a given class of animal.
		 */
		private Color getColor(Class animalClass) {
			Color col = colors.get(animalClass);
			if (col == null) {
				// no color defined for this class
				return UNKNOWN_COLOR;
			} else {
				return col;
			}
		}

		/**
		 * Show the current status of the field.
		 * 
		 * @param step  Which iteration step it is.
		 * @param field The field whose status is to be displayed.
		 */
		public void showStatus(int step, Field field) {
			if (!isVisible()) {
				setVisible(true);
			}

			stepLabel.setText(STEP_PREFIX + step);
			stats.reset();

			fieldView.preparePaint();

			for (int row = 0; row < field.getDepth(); row++) {
				for (int col = 0; col < field.getWidth(); col++) {
					Object animal = field.getObjectAt(row, col);
					if (animal != null) {
						stats.incrementCount(animal.getClass());
						fieldView.drawMark(col, row, getColor(animal.getClass()));
					} else {
						fieldView.drawMark(col, row, EMPTY_COLOR);
					}
				}
			}
			stats.countFinished();

			population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
			fieldView.repaint();
		}

		/**
		 * Determine whether the simulation should continue to run.
		 * 
		 * @return true If there is more than one species alive.
		 */
		public boolean isViable(Field field) {
			return stats.isViable(field);
		}

		/**
		 * Provide a graphical view of a rectangular field. This is a nested class (a
		 * class defined inside a class) which defines a custom component for the user
		 * interface. This component displays the field. This is rather advanced GUI
		 * stuff - you can ignore this for your project if you like.
		 */
		private class FieldView extends JPanel {
			private final int GRID_VIEW_SCALING_FACTOR = 7;

			private int gridWidth, gridHeight;
			private int xScale, yScale;
			Dimension size;
			private Graphics g;
			private Image fieldImage;

			/**
			 * Create a new FieldView component.
			 */
			public FieldView(int height, int width) {
				gridHeight = height;
				gridWidth = width;
				size = new Dimension(0, 0);
			}

			/**
			 * Tell the GUI manager how big we would like to be.
			 */
			public Dimension getPreferredSize() {
				return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR, gridHeight * GRID_VIEW_SCALING_FACTOR);
			}

			/**
			 * Prepare for a new round of painting. Since the component may be resized,
			 * compute the scaling factor again.
			 */
			public void preparePaint() {
				if (!size.equals(getSize())) { // if the size has changed...
					size = getSize();
					fieldImage = fieldView.createImage(size.width, size.height);
					g = fieldImage.getGraphics();

					xScale = size.width / gridWidth;
					if (xScale < 1) {
						xScale = GRID_VIEW_SCALING_FACTOR;
					}
					yScale = size.height / gridHeight;
					if (yScale < 1) {
						yScale = GRID_VIEW_SCALING_FACTOR;
					}
				}
			}

			/**
			 * Paint on grid location on this field in a given color.
			 */
			public void drawMark(int x, int y, Color color) {
				g.setColor(color);
				g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
			}

			/**
			 * The field view component needs to be redisplayed. Copy the internal image to
			 * screen.
			 */
			public void paintComponent(Graphics g) {
				if (fieldImage != null) {
					Dimension currentSize = getSize();
					if (size.equals(currentSize)) {
						g.drawImage(fieldImage, 0, 0, null);
					} else {
						// Rescale the previous image.
						g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
					}
				}
			}
		}
	}

	/**
	 * Classes were imported from OFWJ resources. "main" method was added by Dr.
	 * Olagbemi to create an instance of the Simulator class, get user input and run
	 * the long simulation. Date: 8-27-20
	 * 
	 * @param args Arguments for main method..
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// startSimulation();
		new Simulator();
	}

}
