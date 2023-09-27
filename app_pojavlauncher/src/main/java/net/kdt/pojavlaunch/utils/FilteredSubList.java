package net.kdt.pojavlaunch.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Provide a "mostly immutable" view to a "mother" list, by reference.
 * The difference from List.sublist() is:
 *  - the ability to apply a FILTER on listGeneration
 *  - "immutability", you can't add elements to the list from here, but it is backed by the real list.
 * @param <E>
 */
public class FilteredSubList<E> extends AbstractList<E> implements List<E> {

    private final ArrayList<E> mArrayList;

    public FilteredSubList(E[] motherList, BasicPredicate<E> filter){
        mArrayList = new ArrayList<>();
        refresh(motherList, filter);
    }

    public void refresh(E[] motherArray, BasicPredicate<E> filter){
        if(!mArrayList.isEmpty()) mArrayList.clear();

        for(E item : motherArray){
            if(filter.test(item)){
                mArrayList.add(item);
            }
        }
        // Should we trim ?
        mArrayList.trimToSize();
    }

    @Override
    public int size() {
        return mArrayList.size();
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return mArrayList.iterator();
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return mArrayList.remove(o);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return mArrayList.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return mArrayList.retainAll(c);
    }

    @Override
    public void clear() {
        mArrayList.clear();
    }

    @Override
    public E get(int index) {
        return mArrayList.get(index);
    }

    @Override
    public E remove(int index) {
        return mArrayList.remove(index);
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator() {
        return mArrayList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return mArrayList.listIterator(index);
    }

    @NonNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return mArrayList.subList(fromIndex, toIndex);
    }



    // Predicate is API 24+, so micro backport
    public interface BasicPredicate<E> {
        boolean test(E item);
    }
}
