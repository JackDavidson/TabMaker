package wolf.games.mobile.tools;

import android.util.Log;

public class MathStuff {
	
	public static final double getScaledVectorX(float x, float y, int scale)
	{
		if ((x > 0 && y > 0) || (y < 0 && x > 0))
		{
			return Math.cos(Math.atan(y/x))*scale;
		}
		else if ((x < 0 && y < 0) || (y > 0 && x < 0))
		{
			return -Math.cos(Math.atan(y/x))*scale;
		}
		
		return 0;
	}

	public static final double getScaledVectorY(float x, float y, int scale)
	{
		if ((x > 0 && y > 0) || (y < 0 && x > 0))
		{
			return Math.sin(Math.atan(y/x))*scale;
		}
		else if ((x < 0 && y < 0) || (y > 0 && x < 0))
		{
			return -Math.sin(Math.atan(y/x))*scale;
		}
		
		
		return 0;
	}
}
