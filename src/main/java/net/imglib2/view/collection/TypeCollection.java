package net.imglib2.view.collection;

import java.util.Collection;
import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.type.Type;


/**
 * A {@link Collection} wrapper over an {@link IterableInterval}.
 * 
 * @author Albert Cardona
 *
 * @param <T>
 */
public class TypeCollection<T extends Type<T>> implements Collection<T>
{

	
	private IterableInterval<T> img;
	
	public TypeCollection(final IterableInterval<T> ii) {
		this.img = ii;
	}
	
	@Override
	public boolean add(T arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(final Object v) {
		final T t = this.img.iterator().next();
		if (t.getClass().isInstance(v)) {
			@SuppressWarnings("unchecked")
			final T tv = (T)v;
			for (final T s : this.img) {
				if (s.valueEquals(tv)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return this.img.cursor();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	/*
	 * WARNING: size might overflow. Use longSize() instead.
	 */
	@Override
	public int size() {
		return (int)this.longSize();
	}

	public long longSize() {
		long size = 1;
		for (int i=0; i<this.img.numDimensions(); ++i) {
			size *= this.img.dimension(i);
		}
		return size;
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <E> E[] toArray(E[] a) {
		throw new UnsupportedOperationException();
	}
}
