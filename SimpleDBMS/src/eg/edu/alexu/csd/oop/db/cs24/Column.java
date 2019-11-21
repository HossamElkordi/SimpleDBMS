package eg.edu.alexu.csd.oop.db.cs24;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class Column<T> {

	private String name;
	private ArrayList<T> elements;
	private final Class<T> parameterType;
	
	public Column(String name, Class<T> type) {
		this.parameterType = type;
		this.name = name;
		this.elements = new ArrayList<T>();
	}

	public String getName() {
		return name;
	}

	public ArrayList<T> getElements() {
		return elements;
	}

	public void add(T element) {
		elements.add(element);
	}
	
	public void set(int index, T element) {
		elements.set(index, element);
	}
	
	public Class<?> getType() {
		return this.parameterType;
	}
	
}
