package eg.edu.alexu.csd.oop.db.cs24;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

public class Column<T> {

	private String name;
	private ArrayList<T> elements;
	
	public Column(String name) {
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
	
	public Class<?> getType() {
		ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
		return (Class<?>)type.getActualTypeArguments()[0];
	}
	
}
