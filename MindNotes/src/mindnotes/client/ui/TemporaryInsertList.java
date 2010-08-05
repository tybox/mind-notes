package mindnotes.client.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TemporaryInsertList<T> implements List<T> {

	private final List<? extends T> _originalList;
	private T _insert;
	private int _insertIndex;

	public TemporaryInsertList(List<? extends T> originalList) {
		_originalList = originalList;

	}

	public void setTemporaryInsert(int index, T element) {
		_insert = element;
		_insertIndex = index;
	}

	public void clearInsert() {
		_insert = null;
		_insertIndex = 0;
	}

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		clearInsert();
		_originalList.clear();
	}

	@Override
	public boolean contains(Object o) {
		return o == _insert || _originalList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		clearInsert();
		return _originalList.containsAll(c);
	}

	@Override
	public T get(int index) {
		if (_insert != null) {
			if (index == _insertIndex)
				return _insert;
			if (index > _insertIndex)
				index--;
		}
		return _originalList.get(index);
	}

	@Override
	public int indexOf(Object o) {
		if (_insert == o)
			return _insertIndex;
		int i = _originalList.indexOf(o);
		if (_insert != null && i >= _insertIndex)
			i++;
		return i;
	}

	@Override
	public boolean isEmpty() {
		return _insert != null || _originalList.isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		if (_insert == null)

			return (Iterator<T>) _originalList.iterator();
		return new Iterator<T>() {

			private Iterator<T> _originalIterator;
			private int _i = 0;

			{
				_originalIterator = (Iterator<T>) _originalList.iterator();
			}

			@Override
			public boolean hasNext() {
				return _originalIterator.hasNext() || (_i == _insertIndex - 1);
			}

			@Override
			public T next() {
				if (_i++ == _insertIndex) {
					return _insert;
				}
				return _originalIterator.next();
			}

			@Override
			public void remove() {
				if (_i == _insertIndex) {
					clearInsert();
				} else {
					_originalIterator.remove();
				}
			}

		};
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o == _insert)
			return _insertIndex;
		return lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		if (o == _insert) {
			clearInsert();
			return true;
		}
		return _originalList.remove(o);
	}

	@Override
	public T remove(int index) {
		if (_insert != null) {
			if (_insertIndex == index) {
				T ins = _insert;
				clearInsert();
				return ins;
			}
			if (index > _insertIndex) {
				index--;
			}
		}
		return _originalList.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		clearInsert();
		return _originalList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		clearInsert();
		return _originalList.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return _originalList.size() + (_insert == null ? 0 : 1);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		clearInsert();
		return _originalList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		clearInsert();
		return _originalList.toArray(a);
	}

}
