package simulations;

import java.util.List;
import java.util.Random;

public abstract class Animal {

	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();

	private int age;
	private boolean alive;
	private Location location;
	private Field field;

	/**
	 * Create a new animal. An animal may be created with age zero (a new born) or
	 * with a random age.
	 * 
	 * @param field    The field currently occupied.
	 * @param location The location within the field.
	 */
	public Animal(Field field, Location location) {

		alive = true;
		this.field = field;
		setLocation(location);

	}

	/**
	 * Check whether the animal is alive or not.
	 * 
	 * @return True if the animal is still alive.
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Return the animal's location.
	 * 
	 * @return The animal's location.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Place the animal at the new location in the given field.
	 * 
	 * @param newLocation The animal's new location.
	 */
	protected void setLocation(Location newLocation) {
		if (location != null) {
			field.clear(location);
		}
		location = newLocation;
		field.place(this, newLocation);
	}

	/**
	 * Increase the age. This could result in the animal's death.
	 */
	protected void incrementAge() {
		setAge(getAge() + 1);
		if (getAge() > getMaxAge()) {
			setDead();
		}
	}

	/*
	 * This is what the animal does.
	 * 
	 * @param newAnimals A list to return newly born animals.
	 */
	public abstract void act(List<Animal> newAnimals);

	/**
	 * Check whether or not this animal is to give birth at this step. New births
	 * will be made into free adjacent locations.
	 * 
	 * @param newAnimals A list to return newly born animals.
	 */
	public abstract void giveBirth(List<Animal> newAnimals);

	/*
	 * Get the max age of the animal.
	 */
	protected abstract int getMaxAge();

	/*
	 * Get the breeding age of the animal.
	 */
	protected abstract int getBreedingAge();

	/*
	 * Get the breeding probability of the animal.
	 */
	protected abstract double getBreedingProbability();

	/*
	 * Get the max litter size of the animal.
	 */
	protected abstract int getMaxLitterSize();

	/**
	 * Generate a number representing the number of births, if it can breed.
	 * 
	 * @return The number of births (may be zero).
	 */
	protected int breed() {
		int births = 0;
		if (canBreed() && getRandom().nextDouble() <= getBreedingProbability()) {
			births = getRandom().nextInt(getMaxLitterSize()) + 1;
		}
		return births;
	}

	/**
	 * An animal can breed if it has reached the breeding age.
	 * 
	 * @return true if the animal can breed, false otherwise.
	 */
	protected boolean canBreed() {
		return age >= getBreedingAge();
	}

	/**
	 * Indicate that the animal is no longer alive. It is removed from the field.
	 */
	protected void setDead() {
		alive = false;
		if (location != null) {
			field.clear(location);
			location = null;
			field = null;
		}
	}

	/*
	 * Get the age of the animal.
	 */
	public int getAge() {
		return age;
	}

	/*
	 * Set the age of the animal
	 * 
	 * @param age The age of the animal.
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/*
	 * Get the field.
	 */
	public Field getField() {
		return field;
	}

	/*
	 * Set the field.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/*
	 * Mutator of the field alive.
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/*
	 * Accessor of the field rand.
	 */
	protected Random getRandom() {
		return rand;
	}

}
