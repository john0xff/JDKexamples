package net.sending.objects;

import java.awt.Point;
import java.io.Serializable;

public class PlayerEnvelope implements Serializable
{
	private static final long serialVersionUID = 5887378105289907074L;
	
	private String name;
	private Point position;

	public PlayerEnvelope(String name, Point position)
	{
		this.name = name;
		this.position = position;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Point getPosition()
	{
		return position;
	}

	public void setPosition(Point position)
	{
		this.position = position;
	}

}
