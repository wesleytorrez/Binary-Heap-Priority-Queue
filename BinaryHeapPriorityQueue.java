package data_structures;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Programming Assignment #3
 * The BinaryHeapPriorityQueue class is a Minimum
 * Heap implementation of a Priority Queue. This
 * means that all parent nodes are smaller than
 * its children node(s). It is actually an array
 * that behaves as a Binary Tree data structure.
 * It's insert and remove methods operate in
 * O(log n) time due to the use of trickle up
 * and trickle down methods. These methods use
 * arithmetic division on the current index which
 * gives its complexity logarithmic behavior. The
 * Wrapper class allows "stable" behavior, meaning
 * that the minHeap will preserve the order of
 * duplicate elements. It provides a unique
 * sequenceNumber for each object in the minHeap,
 * which in turn provides stable behavior. However,
 * adding the Wrapper class adds overhead and
 * doubles the storage requirement. Consequently,
 * this minHeap is not "in place", meaning that
 * it requires extra storage space in terms of
 * the input size n.
 * 4/8/2019
 * @author Wesley Torrez cssc1517
 */

@SuppressWarnings("unchecked")
public class BinaryHeapPriorityQueue<E extends Comparable<E>> implements PriorityQueue<E> {
    private static final int DEFAULT_MAX_CAPACITY = 1000;
    private Wrapper<E>[] minHeap;
    private int capacity;
    private int currentSize;
    private int root;
    private int lastIndex;
    private long modificationCounter;
    private long entryNumber;

    private class Wrapper<E> implements Comparable<Wrapper<E>>{
        private E data;
        private long sequenceNumber; // this variable provides a
                                     // unique sequenceNumber for
                                     // each object.

        private Wrapper(E object) {
            data = object;
            sequenceNumber = entryNumber++;
        }

        public int compareTo(Wrapper<E> object) {
            int wrappedElement = (((Comparable<E>)data).compareTo(object.data));
            if (wrappedElement == 0)
                return (int) (sequenceNumber - object.sequenceNumber);
            return wrappedElement;
        }

        @Override
        public String toString() {
            return ""+data;
        }
    }

    public BinaryHeapPriorityQueue() { this(DEFAULT_MAX_CAPACITY); }

    public BinaryHeapPriorityQueue(int capacity) {
        this.capacity = capacity;
        minHeap = new Wrapper[capacity];
        currentSize = 0;
        root = 0;
        lastIndex = capacity - 1;
        modificationCounter = 0;
    }

    @Override
    public boolean insert(E object) {
        if (isFull())
            return false;
        int index = currentSize;
        Wrapper<E> insertedElement = new Wrapper(object);
        minHeap[index] =  insertedElement;
        trickleUp(index);
        currentSize++;
        modificationCounter++;
        return true;
    }

    @Override
    public E remove() {
        if (isEmpty())
            return null;
        if (lastIndex != currentSize - 1)
            lastIndex = currentSize - 1;
        Wrapper<E> rootElement = minHeap[root];
        minHeap[root] = minHeap[lastIndex];
        minHeap[lastIndex] = null;
        currentSize--;
        modificationCounter++;
        trickleDown(root);
        return rootElement.data;
    }

    /*
     * The delete method operates in O(n log n) time.
     * It must search through the entire array to find
     * all instances of the specified object that must
     * be deleted, then it must delete the objects and
     * sort the array to preserve its minHeap order
     * requirements.
     */
    @Override
    public boolean delete(E obj) {
        if (isEmpty())
            return false;
        int counter = 0;
        if (lastIndex != currentSize - 1)
            lastIndex = currentSize - 1;
        int check = lastIndex;
        while (counter < currentSize) {
            Wrapper<E> rootElement = minHeap[counter];
            if (rootElement.data.compareTo(obj) == 0) {
                minHeap[counter] = minHeap[lastIndex];
                minHeap[lastIndex] = null;
                lastIndex--;
                currentSize--;
                modificationCounter++;
                trickleDown(counter);
                counter--;
            }
            counter++;
        }
        if (lastIndex != check)
            return true;
        return false;
    }

    @Override
    public E peek() {
        if (isEmpty())
        return null;
        return minHeap[root].data;
        }

    @Override
    public boolean contains(E obj) {
        int parentIndex = (root - 1) / 2;
        while (parentIndex < currentSize) {
            if (obj.compareTo(minHeap[parentIndex].data) == 0)
                return true;
            parentIndex++;
        }
        return false;
    }

    @Override
    public int size() { return currentSize; }

    @Override
    public void clear() {
        currentSize = 0;
        modificationCounter++;
    }

    @Override
    public boolean isEmpty() { return currentSize == 0; }

    @Override
    public boolean isFull() { return currentSize == capacity; }

    @Override
    public Iterator<E> iterator() { return new IteratorHelper(); }

    private void trickleUp(int position) {
        int parentIndex = (position - 1) / 2;
        int currentIndex = position;
        Wrapper<E> newElement = minHeap[currentIndex];
        while (currentIndex > 0 && minHeap[parentIndex].compareTo(newElement) > 0) {
            swap(currentIndex, parentIndex);
            currentIndex = parentIndex;
            parentIndex = (parentIndex - 1) / 2;
        }
        minHeap[currentIndex] = newElement;
    }

    private void trickleDown(int position) {
        int currentIndex = position;
        int leftChildIndex = 2 * position + 1;
        int rightChildIndex = 2 * position + 2;
        if (leftChildIndex < currentSize && minHeap[currentIndex].compareTo(minHeap[leftChildIndex]) > 0)
            currentIndex = leftChildIndex;
        if (rightChildIndex < currentSize && minHeap[currentIndex].compareTo(minHeap[rightChildIndex]) > 0)
            currentIndex = rightChildIndex;
        if (currentIndex != position) {
            swap(position, currentIndex);
            trickleDown(currentIndex);
        }
    }

    private void swap(int a, int b) {
        Wrapper<E> swappedElements = minHeap[a];
        minHeap[a] = minHeap[b];
        minHeap[b] = swappedElements;
    }

    public class IteratorHelper implements Iterator<E> {
        private int iterIndex;
        private long stateCheck;

        public IteratorHelper() {
            iterIndex = 0;
            stateCheck = modificationCounter;
        }

        @Override
        public boolean hasNext() {
            if (stateCheck != modificationCounter)
                throw new ConcurrentModificationException();
            return iterIndex < currentSize;
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            Wrapper<E> nextElement = minHeap[iterIndex++];
            return nextElement.data;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
