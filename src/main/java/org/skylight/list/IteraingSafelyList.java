package org.skylight.list;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class IteraingSafelyList<E> implements List<E> {
    private final List<E> backing;
    public IteraingSafelyList(){
        backing = new Vector<>();
    }
    public IteraingSafelyList(Collection<E> otherElemet){
        backing = new Vector<>(otherElemet);
    }
    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backing.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        AtomicReference<E> valueNow = new AtomicReference<>();
        List bak = new Vector(backing);
        return new Iterator<E>() {
            private final Iterator<E> itr = bak.iterator();

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public E next() {
                E value = itr.next();
                valueNow.set(value);
                return value;
            }

            @Override
            public void remove() {
                E willRemove = valueNow.get();
                if (willRemove == null) {
                    return;
                }
                backing.remove(willRemove);
            }
        };
    }

    @Override
    public Object[] toArray() {
        return  backing.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return backing.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return backing.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return backing.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backing.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return backing.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return backing.addAll(index,c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return backing.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return backing.retainAll(c);
    }

    @Override
    public void clear() {
        backing.clear();
    }

    @Override
    public E get(int index) {
        return backing.get(index);
    }

    @Override
    public E set(int index, E element) {
        return backing.set(index,element);
    }

    @Override
    public void add(int index, E element) {
        backing.add(index,element);
    }

    @Override
    public E remove(int index) {
        return backing.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return backing.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backing.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return backing.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return backing.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return backing.subList(fromIndex,toIndex);
    }
}
