package com.elmakers.mine.bukkit.plugins.persistence.dao;

import org.bukkit.Location;

import com.elmakers.mine.bukkit.plugins.persistence.annotation.Persist;
import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistClass;

@PersistClass(schema="global", name="orientation", contained=true)
public class Orientation
{
	public Orientation()
	{
		
	}
	
	public Orientation(Location location)
	{
		yaw = location.getYaw();
		pitch = location.getPitch();
	}
	
	@Persist
	public float getPitch()
	{
		return pitch;
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}
	
	@Persist
	public float getYaw()
	{
		return yaw;
	}
	
	public void setYaw(float yaw)
	{
		this.yaw = yaw;
	}
	
	protected float pitch;
	protected float yaw;
}
