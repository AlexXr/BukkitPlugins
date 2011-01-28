package com.elmakers.mine.bukkit.plugins.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elmakers.mine.bukkit.plugins.persistence.annotations.Persist;
import com.elmakers.mine.bukkit.plugins.persistence.annotations.PersistClass;
import com.elmakers.mine.bukkit.plugins.persistence.stores.PersistenceStore;

public class PersistedClass
{
	public boolean bind(Class<? extends Object> persistClass)
	{
		this.persistClass = persistClass;
		
		/*
		 * Set up persisted class
		 */
		PersistClass classSettings = persistClass.getAnnotation(PersistClass.class);
		if (classSettings == null)
		{
			// TODO : Log error
			return false;
		}		

		cacheObjects = classSettings.cache();
		schema = classSettings.schema();
		name = classSettings.name();
		store = Persistence.getInstance().getStore(schema);
		
		/*
		 * Find fields, getters and setters
		 */
		
		idField = null;
		orderByField = null;

		
		for (Field field : persistClass.getDeclaredFields())
		{
			Persist persist = field.getAnnotation(Persist.class);
			if (persist != null)
			{
				PersistedField pField = new PersistedField(field);
				fields.add(pField);
				if (persist.id())
				{
					idField = pField;
				}
				if (persist.order())
				{
					orderByField = pField;
				}
			}
		}
	
		for (Method method : persistClass.getDeclaredMethods())
		{
			Persist persist = method.getAnnotation(Persist.class);
			if (persist != null)
			{
				Method setter = null;
				Method getter = null;
				String fieldName = getNameFromMethod(method);
				
				if (isSetter(method))
				{
					setter = method;
					getter = findGetter(fieldName, persistClass);
				}
				else if (isGetter(method))
				{
					getter = method;
					setter = findSetter(fieldName, persistClass);
				}
				
				if (getter == null || setter == null)
				{
					// TODO: Log error!
					continue;
				}
				
				PersistedField pField = new PersistedField(getter, setter);
				fields.add(pField);
				if (persist.id())
				{
					idField = pField;
				}
				if (persist.order())
				{
					orderByField = pField;
				}
			}
		}
		
		return (fields.size() > 0);
	}
	
	public CachedObject put(Object o)
	{
		Object idField = getId(o);
		CachedObject co = cacheMap.get(idField);
		if (co == null)
		{
			co = new CachedObject(o);
			cacheMap.put(idField, co);
			cache.add(co);
		}
		co.setCached(cacheObjects);
		return co;
	}

	public CachedObject get(Object o)
	{
		return cacheMap.get(o);
	}
		
	public boolean isDirty()
	{
		return dirty;
	}
	
	public int getFieldCount()
	{
		return fields.size();
	}
	
	/*
	 * Protected members
	 */
	
	protected boolean isGetter(Method method)
	{
		String methodName = method.getName();
		return methodName.substring(0, 3).equals("get");
	}
	
	protected boolean isSetter(Method method)
	{
		String methodName = method.getName();
		return methodName.substring(0, 3).equals("get");
	}
	
	protected String getNameFromMethod(Method method)
	{
		String methodName = method.getName();
		return methodName.substring(3);
	}
	
	protected Method findSetter(String name, Class<? extends Object> c)
	{
		Method setter = null;
		try
		{
			setter = c.getMethod("set" + name, Object.class);
		}
		catch (NoSuchMethodException e)
		{
			setter = null;
		}
		return setter;
	}
	
	protected Method findGetter(String name, Class<?> c)
	{
		Method getter = null;
		try
		{
			getter = c.getMethod("get" + name);
		}
		catch (NoSuchMethodException e)
		{
			getter = null;
		}
		return getter;
	}
	
	protected Object getId(Object o)
	{
		Object value = null;
		if (idField != null)
		{
			value = idField.get(o);
		}
		return value;
	}
	
	/*
	 * Private data
	 */
	
	protected boolean dirty = false;
	
	protected boolean cacheObjects;
	protected Class<? extends Object> persistClass;
	
	protected List<PersistedField> fields = new ArrayList<PersistedField>();
	protected PersistedField idField;
	protected PersistedField orderByField;
	
	protected String schema;
	protected String name;
	
	protected PersistenceStore store = null;
	
	protected HashMap<Object, CachedObject> cacheMap = new HashMap<Object, CachedObject>();
	protected List<CachedObject>			cache =  new ArrayList<CachedObject>();
}
