package simulations;

import java.util.List;
import java.util.Random;

/**
 * A simple model of a rabbit. Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class Rabbit extends Animal {
	// Characteristics shared by all rabbits (class variables).
	// The age at which an animal can start to breed.
	protected static final int BREEDING_AGE = 5;
	// The age to which an animal can live.
	protected static final int MAX_AGE = 40;
	// The likelihood of an animal breeding.
	protected static final double BREEDING_PROBABILITY = 0.12;
	// The maximum number of births.
	protected static final int MAX_LITTER_SIZE = 4;

	public Rabbit(boolean randomAge, Field field, Location location) {
		// inherit the constructor of the superclass
		super(field, location);
		setAge(0);
		if (randomAge) {
			setAge(getRandom().nextInt(MAX_AGE));
		}

	}

	/**
	 * This is what the rabbit does most of the time - it runs around. Sometimes it
	 * will breed or die of old age.
	 * 
	 * @param newAnimals A list to return newly born animals.
	 */
	@Override
	public void act(List<Animal> newAnimals) {
		incrementAge();
		if (isAlive()) {
			giveBirth(newAnimals);
			// Try to move into a free location.
			Location newLocation = getField().freeAdjacentLocation(getLocation());
			if (newLocation != null) {
				setLocation(newLocation);
			} else {
				// Overcrowding.
				setDead();
			}
		}
	}

	/**
	 * Check whether or not this rabbit is to give birth at this step. New births
	 * will be made into free adjacent locations.
	 * 
	 * @param newAnimals A list to return newly born animals.
	 */
	@Override
	public void giveBirth(List<Animal> newAnimals) {
		// New rabbits are born into adjacent locations.
		// Get a list of adjacent free locations.
		List<Location> free = getField().getFreeAdjacentLocations(getLocation());
		int births = breed();
		for (int b = 0; b < births && free.size() > 0; b++) {
			Location loc = free.remove(0);
			Rabbit young = new Rabbit(false, getField(), loc);
			newAnimals.add(young);
		}
	}

	@Override
	protected int getMaxAge() {
		// TODO Auto-generated method stub
		return Rabbit.MAX_AGE;
	}

	@Override
	protected int getBreedingAge() {
		// TODO Auto-generated method stub
		return Rabbit.BREEDING_AGE;
	}

	@Override
	protected double getBreedingProbability() {
		// TODO Auto-generated method stub
		return Rabbit.BREEDING_PROBABILITY;
	}

	@Override
	protected int getMaxLitterSize() {
		// TODO Auto-generated method stub
		return Rabbit.MAX_LITTER_SIZE;
	}

}
