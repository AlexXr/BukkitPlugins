package com.elmakers.mine.bukkit.plugins.persistence.stores;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elmakers.mine.bukkit.plugins.persistence.DataType;
import com.elmakers.mine.bukkit.plugins.persistence.PersistedClass;
import com.elmakers.mine.bukkit.plugins.persistence.PersistedField;
import com.elmakers.mine.bukkit.plugins.persistence.PersistedList;
import com.elmakers.mine.bukkit.plugins.persistence.PersistedReference;

public abstract class SqlStore extends PersistenceStore
{
	public abstract String getDriverClassName();
	public abstract String getDriverFileName();
	public abstract String getMasterTableName();
	public abstract String getConnectionString(String schema, String user, String password);
	public abstract String getTypeName(DataType dataType);
	public abstract Object getFieldValue(Object field, DataType dataType);
	public abstract Object getDataValue(Object storedValue, DataType dataType);
	
	public boolean onConnect()
	{
		return true;
	}
	
	@Override
	public boolean connect(String schema)
	{
		this.schema = schema;
		
		try 
		{
			// Check to see if the driver is loaded
			String jdbcClass = getDriverClassName();
			try
			{
				Class.forName(jdbcClass);
			}
			catch (ClassNotFoundException e)
			{
				log.info("Persistence: Loading sqlite drivers from plugins folder");
				String fileName = getDriverFileName();
				
				File dataPath = dataFolder.getAbsoluteFile();
				File pluginsPath = new File(dataPath.getParent());
				File cbPath = new File(pluginsPath.getParent());
				File sqlLiteFile = new File(cbPath, fileName + ".jar");
	            if (!sqlLiteFile.exists()) 
	            {
	                log.severe("Persistence: Failed to find sql driver: " + fileName + ".jar");
	                return false;
	            }
	            
	            try 
	            {
	            	URL u = new URL("jar:file:" + sqlLiteFile.getAbsolutePath() + "!/");
	        		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
	        		Driver d = (Driver)Class.forName(jdbcClass, true, ucl).newInstance();
	        		DriverManager.registerDriver(new DriverShim(d));
	            } 
	            catch (MalformedURLException ex) 
	            {
	                log.severe("Persistence: Exception while loading sql drivers");
	                ex.printStackTrace();
	                return false;
	            }
	            catch (IllegalAccessException ex) 
	            {
	                log.severe("Persistence: Exception while loading sql drivers");
	                ex.printStackTrace();
	                return false;
	            }
	            catch (InstantiationException ex) 
	            {
	                log.severe("Persistence: Exception while loading sql drivers");
	                ex.printStackTrace();
	                return false;
	            }
				catch (ClassNotFoundException e1)
				{
					log.severe("Persistence: JDBC class not found in sql jar");
				}
			}			
			// Create or connect to the database
			
			// TODO: user, password
			String user = "";
			String password = "";
					
			connection = DriverManager.getConnection(getConnectionString(schema, user, password));
		}
		catch(SQLException e)
		{
			connection = null;
			log.severe("Permissions: error connecting to sqllite db: " + e.getMessage());
		}
		
		return isConnected() && onConnect();
	}

	@Override
	public void disconnect()
	{
		if (connection != null)
		{
			try
			{
				connection.close();
			}
			catch (SQLException e)
			{
				
			}
		}
		connection = null;
	}
	
	@Override
	public void reset(PersistedClass persisted)
	{
		String tableName = persisted.getTableName();
		if (tableExists(tableName))
		{
			String dropQuery = "DROP TABLE " + tableName;
			try
			{
				PreparedStatement ps = connection.prepareStatement(dropQuery);
				ps.execute();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
				return;
			}
			log.info("Dropped table " + schema + "." + tableName);
		}
		return;
	}

	public boolean tableExists(String tableName)
	{
		String checkQuery = "SELECT name FROM " + getMasterTableName() + " WHERE type='table' AND name='" + tableName + "'";
		boolean tableExists = false;
		try
		{
			PreparedStatement ps = connection.prepareStatement(checkQuery);
			ResultSet rs = ps.executeQuery();
			tableExists = rs.next();
			rs.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			return false;
		}
		return tableExists;
	}
	
	@Override
	public void validateTable(PersistedClass persisted)
	{
		String tableName = persisted.getTableName();
		if (!tableExists(tableName))
		{
			List<PersistedField> fields = persisted.getPersistedFields();
			String createStatement = "CREATE TABLE " + tableName + "(";
			int fieldCount = 0;
			for (PersistedField field : fields)
			{
				if (field instanceof PersistedList)
				{
					continue;
				}
				DataType fieldType = field.getColumnType();
				String fieldName = field.getColumnName();
				if (fieldCount != 0)
				{
					createStatement += ",";
				}
				fieldCount++;
				createStatement += fieldName + " " + getTypeName(fieldType);
				if (field.isIdField())
				{
					createStatement += " PRIMARY KEY";
				}
			}
			createStatement += ")";
			
			if (fieldCount == 0)
			{
				log.warning("Persistence: class " + tableName + " has no fields");
				return;
			}
			
			log.info(createStatement);
			log.info("Persistence: Create table " + schema + "." + tableName);
			try
			{
				PreparedStatement ps = connection.prepareStatement(createStatement);
				ps.execute();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			// TODO: validate schema, migrate data if necessary
		}
	}
	
	@Override
	public boolean save(PersistedClass persisted, Object o)
	{
		String tableName = persisted.getTableName();
		String fieldList = "";
		String valueList = "";
		int fieldCount = 0;
		List<PersistedField> fields = persisted.getPersistedFields();
		for (PersistedField field : fields)
		{
			if (field instanceof PersistedList)
			{
				continue;
			}
			if (fieldCount != 0)
			{
				fieldList += ", ";
				valueList += ", ";
			}
			fieldCount++;
			fieldList += field.getColumnName();
			valueList += "?";
		}
		if (fieldCount == 0)
		{
			log.warning("Persistence: class " + tableName + " has no fields");
			return false;
		}
		
		String updateSql = "INSERT OR REPLACE INTO " + tableName + "(" + fieldList + ") VALUES (" + valueList + ")";

		try
		{
			PreparedStatement updateStatement = connection.prepareStatement(updateSql);
			
			List<PersistedList> listFields = new ArrayList<PersistedList>();
			
			int index = 1;
			for (PersistedField field : fields)
            {
				if (field instanceof PersistedList)
				{
					listFields.add((PersistedList)field);
					continue;
				}
				Object value = field.getColumnData(o);
				
				if (value != null)
				{
					updateStatement.setObject(index, getFieldValue(value, field.getColumnType()));
				}
				else
				{
					updateStatement.setNull(index, java.sql.Types.NULL);
				}
				index++;
            }
			
			updateStatement.execute();
			
			// Update list data
			for (PersistedList listField : listFields)
			{
				Class<?> contentsType = listField.getListType();
				DataType contentsDataType = DataType.getTypeFromClass(contentsType);
				
				if (contentsDataType == DataType.NULL)
				{
					log.warning("Error persisting list " + listField.getName() + " in " + persisted.getTableName());
					continue;
				}
				
				if (contentsDataType == DataType.LIST)
				{
					log.warning("Lists of lists not supported:" + listField.getName() + " in " + persisted.getTableName());
					continue;
				}
				
				if (contentsDataType == DataType.OBJECT)
				{
					log.warning("Lists of references not implemented yet " + listField.getName() + " in " + persisted.getTableName());
					continue;
				}

				// Construct table and column names
				DataType idFieldType = persisted.getIdField().getColumnType();
				String valueField = listField.getColumnName();
				String fieldTableName = listField.getTableName();
				String joinTableName = persisted.getTableName() + fieldTableName;
				
				PersistedField idField = persisted.getIdField();
				String idFieldName = idField.getColumnName();
				idFieldName = tableName.substring(0, 1).toLowerCase() + tableName.substring(1) 
					+ idFieldName.substring(0, 1).toUpperCase() + idFieldName.substring(1);
				String joinFieldList = idFieldName + "," + valueField;
				
				// Validate table schema for join table
				if (!tableExists(joinTableName))
				{	
					String createSql = "CREATE TABLE " + joinTableName + "("
						+	idFieldName + " " + getTypeName(idFieldType) + ", " + valueField + " " + getTypeName(contentsDataType)
						+ ", PRIMARY KEY (" + joinFieldList + "))";
					
					PreparedStatement createStatement = connection.prepareStatement(createSql);
					createStatement.execute();
				}
				
				List<? extends Object> objectValues = listField.getList(o);
				if (objectValues.size() > 0)
				{
					String listDelete = "DELETE FROM " + joinTableName + " WHERE " + idFieldName + " = "
						+ getFieldValue(idField.getColumnData(o), idField.getColumnType())
						+ " AND " + valueField + " NOT IN (";
					
					boolean firstItem = true;
					for (Object lisItem : objectValues)
					{
						if (!firstItem)
						{
							listDelete += ", ";
						}
						firstItem = false;
						listDelete += getFieldValue(lisItem, contentsDataType);
					}
					
					listDelete += ")";
					
					PreparedStatement deleteStatement = connection.prepareStatement(listDelete);
					deleteStatement.execute();
					
					for (Object listItem : objectValues)
					{
						String joinValueList = getFieldValue(idField.getColumnData(o), idField.getColumnType()) + "," + getFieldValue(listItem, contentsDataType);
						String itemUpdateSql = "INSERT OR REPLACE INTO " + joinTableName + "(" + joinFieldList + ") VALUES (" + joinValueList + ")";
						
						PreparedStatement listUpdateStatement = connection.prepareStatement(itemUpdateSql);
						listUpdateStatement.execute();
					}
				}
				else
				{
					String listDelete = "DELETE FROM " + joinTableName;
					PreparedStatement deleteStatement = connection.prepareStatement(listDelete);
					deleteStatement.execute();
				}
			}
		}
		catch (SQLException ex)
		{
			log.warning("Persistence: Error updating table " + tableName + ": " + ex.getMessage());
			return false;
		}
		
		return true;
	}

	@Override
	public boolean loadAll(PersistedClass persisted)
	{
		String tableName = persisted.getTableName();
		String selectQuery = "SELECT ";
		int fieldCount = 0;
		List<PersistedField> fields = persisted.getPersistedFields();
		for (PersistedField field : fields)
		{
			if (fieldCount != 0)
			{
				selectQuery += ", ";
			}
			fieldCount++;
			selectQuery += field.getColumnName();
		}
		if (fieldCount == 0)
		{
			log.warning("Persistence: class " + tableName + " has no fields");
			return false;
		}
		selectQuery +=  " FROM " + tableName + ";";
		
		try
		{
			// Begin deferred referencing, to prevent the problem of DAO's referencing unloaded DAOs.
			// DAOs will be loaded recursively as needed,
			// and then all deferred references will be resolved afterward.
			PersistedReference.beginDefer();
			
			PreparedStatement ps = connection.prepareStatement(selectQuery);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				Object newObject = createInstance(rs, persisted);
				if (newObject != null)
				{
					persisted.put(newObject);
				}
			}
			rs.close();
			
			// Bind deferred references, to handle DAOs referencing other DAOs, even of the
			// Same type. 
			// DAOs will be loaded recursively as needed, and then references bound when everything has been
			// resolved.
			PersistedReference.endDefer();
		}
		catch (SQLException ex)
		{
			log.warning("Persistence: Error selecting from table " + tableName + ": " + ex.getMessage());
			return false;
		}
		
		return true;
	}
	
	protected Object createInstance(ResultSet rs, PersistedClass persisted)
	{
		Object newObject = null;
		
		try
		{
			newObject = persisted.getPersistClass().newInstance();
			List<PersistedField> fields = persisted.getPersistedFields();
	        for (PersistedField field : fields)
	        {
        		Object value = rs.getObject(field.getColumnName());
        		DataType sqlType = field.getColumnType();
        		field.setColumnData(newObject, getDataValue(value, sqlType));
	        }
		}
		catch (Exception e)
		{
			newObject = null;
			log.warning("Persistence error getting fields for " + persisted.getTableName() + ": " + e.getMessage());
		}

		return newObject;
	}
	
	public boolean isConnected()
	{
		boolean isClosed = true;
		try
		{
			isClosed = connection == null || connection.isClosed();
		}
		catch (SQLException e)
		{
			isClosed = true;
		}
		return (connection != null && !isClosed);
	}

	public void setDataFolder(File dataFolder)
	{
		this.dataFolder = dataFolder;
	}
	
	protected File dataFolder = null;
	protected Connection connection = null;
	protected String schema = null;
}
