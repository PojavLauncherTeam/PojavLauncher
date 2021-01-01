package org.lwjgl.input;

/**
 * Internal utility class to keep track of event positions in an array. When the
 * array is full the position will wrap to the beginning.
 */
class EventQueue {

	private int maxEvents = 32;
	private int currentEventPos = -1;
	private int nextEventPos = 0;

	EventQueue(int maxEvents) {
		this.maxEvents = maxEvents;
	}

	/**
	 * add event to the queue
	 */
	void add() {
		nextEventPos++; // increment next event position
		if (nextEventPos == maxEvents)
			nextEventPos = 0; // wrap next event position

		if (nextEventPos == currentEventPos) {
			currentEventPos++; // skip oldest event is queue full
			if (currentEventPos == maxEvents)
				currentEventPos = 0; // wrap current event position
		}
	}

	/**
	 * Increment the event queue
	 * 
	 * @return - true if there is an event available
	 */
	boolean next() {
		if (currentEventPos == nextEventPos - 1)
			return false;
		if (nextEventPos == 0 && currentEventPos == maxEvents - 1)
			return false;

		currentEventPos++; // increment current event position
		if (currentEventPos == maxEvents)
			currentEventPos = 0; // wrap current event position

		return true;
	}

	int getMaxEvents() {
		return maxEvents;
	}

	int getCurrentPos() {
		return currentEventPos;
	}

	int getNextPos() {
		return nextEventPos;
	}
}
