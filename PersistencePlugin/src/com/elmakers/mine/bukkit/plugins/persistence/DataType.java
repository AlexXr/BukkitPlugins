package com.elmakers.mine.bukkit.plugins.persistence;

import java.util.Date;
import java.util.List;

import com.elmakers.mine.bukkit.plugins.persistence.annotations.PersistClass;

public enum DataType
{
	INTEGER,
	BOOLEAN,
	DOUBLE,
	STRING,
	DATE,
	OBJECT,
	LIST,
	NULL;
	
	public static DataType getTypeFromClass(Class<?> fieldType)
	{
		DataType sqlType = NULL;
		
		if (fieldType.isAssignableFrom(List.class))
		{
			sqlType = DataType.LIST;
		}
		else if (fieldType.isAssignableFrom(Date.class))
		{
			sqlType = DataType.DATE;
		}
		else if (fieldType.isAssignableFrom(Boolean.class))
		{
			sqlType = DataType.BOOLEAN;
		}
		else if (fieldType.isAssignableFrom(Integer.class))
		{
			sqlType = DataType.INTEGER;
		}
		else if (fieldType.isAssignableFrom(Double.class))
		{
			sqlType = DataType.DOUBLE;
		}
		else if (fieldType.isAssignableFrom(Float.class))
		{
			sqlType = DataType.DOUBLE;
		}
		else if (fieldType.isAssignableFrom(String.class))
		{
			sqlType = DataType.STRING;
		}
		else if (fieldType.isAssignableFrom(boolean.class))
		{
			sqlType = DataType.BOOLEAN;
		}
		else if (fieldType.isAssignableFrom(int.class))
		{
			sqlType = DataType.INTEGER;
		}
		else if (fieldType.isAssignableFrom(double.class))
		{
			sqlType = DataType.DOUBLE;
		}
		else if (fieldType.isAssignableFrom(float.class))
		{
			sqlType = DataType.DOUBLE;
		}
		else
		{
			// Don't get the PersistedClass here, or you might cause recursion issues with circular dependencies.
			// Instead, manually look for the annotation.
			PersistClass classSettings = fieldType.getAnnotation(PersistClass.class);
			if (classSettings != null)
			{
				sqlType = DataType.OBJECT;
			}
		}
		return sqlType;
	}
}
