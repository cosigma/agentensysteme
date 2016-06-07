package de.hsb.ants.map;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunkMap<T> {

	static final Logger LOG = LoggerFactory.getLogger(ChunkMap.class);

	private static final int chunkSizeDefault = 16;

	private final int chunkSize;

	private final Map<Point, Chunk> chunks;

	public ChunkMap() {
		this(chunkSizeDefault);
	}

	public ChunkMap(int chunkSize) {
		if(!(chunkSize > 0)){
			throw new IllegalArgumentException("chunk size must be greater then 0");
		}
		this.chunkSize = chunkSize;
		this.chunks = new HashMap<Point, Chunk>(25);
	}

	public T get(int x, int y) {
		Point key = getKey(x, y);
		Chunk chunk = chunks.get(key);
		if (chunk == null) {
			return null;
		} else {
			return chunk.get(x, y);
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param elem
	 */
	public void set(int x, int y, T elem) {
		Point key = getKey(x, y);
		Chunk chunk = chunks.get(key);
		if (chunk == null) {
			chunk = new Chunk(key, chunkSize);
			chunks.put(key, chunk);
		}
		chunk.set(x, y, elem);
	}

	/**
	 * Computes the point of reference for the associated chunk in the chunks
	 * map for the given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Point getKey(int x, int y) {
		int xKey;
		if (x >= 0) {
			xKey = (x / chunkSize) * chunkSize;
		} else {
			xKey = (((x + 1) / chunkSize) - 1) * chunkSize;
		}
		int yKey;
		if (y >= 0) {
			yKey = (y / chunkSize) * chunkSize;
		} else {
			yKey = (((y + 1) / chunkSize) - 1) * chunkSize;
		}
		Point key = new Point(xKey, yKey);
		return key;
	}

	private class Chunk {
		private final int size;
		private final T[] elements;
		private final Point reference;

		@SuppressWarnings("unchecked")
		Chunk(Point ref, int chunkSize) {
			this.size = chunkSize;
			this.elements = (T[]) new Object[size * size];
			this.reference = ref;
		}

		/**
		 * Gets the element at the given coordinates. The coordinates are
		 * truncated to fit the size of the chunk.
		 * 
		 * @param x
		 * @param y
		 * @return
		 */
		T get(int x, int y) {
			return elements[getIndex(x, y)];
		}

		/**
		 * Sets the element at the given coordinates to the given value. The
		 * coordinates are truncated to fit the size of the chunk.
		 * 
		 * @param x
		 * @param y
		 * @param value
		 */
		void set(int x, int y, T value) {
			elements[getIndex(x, y)] = value;
		}

		/**
		 * Calculates the index within the chunk for the given coordinates.
		 * Calling this method never throws an out of bounds exception, as all
		 * indices are considered valid.
		 * 
		 * @param x
		 * @param y
		 * @return
		 */
		private int getIndex(int x, int y) {
			int xIndex = x - reference.x;
			if (xIndex < 0 || xIndex > size) {
				LOG.error("x index out of bounds: {} is not inside [{}, {})", x, reference.x, reference.x + size);
				throw new IndexOutOfBoundsException();
			}
			int yIndex = y - reference.y;
			if (yIndex < 0 || yIndex > size) {
				LOG.error("y index out of bounds: {} is not inside [{}, {})", y, reference.y, reference.y + size);
				throw new IndexOutOfBoundsException();
			}
			int index = xIndex * size + yIndex;
			return index;
		}

	}

}
