package wolf.games.mobile.tabmaker.editor.util;

public class FingerFollower {
	double startX;
	double startY;
	double minDist;
	boolean traveledDist = false;

	public FingerFollower(double x, double y, double minDist) {
		this.startX = x;
		this.startY = y;
		this.minDist = minDist;
	}

	public double getDistanceChange(double x, double y) {
		double xDist = startX - x;
		double yDist = startY - y;
		//Log.e("", "Start x: " + startX);
		//Log.e("", "Cur x: " + x);
		return Math.sqrt(xDist * xDist + yDist * yDist);
	}

	public double getDistanceX(double curX) {
		return Math.abs(curX - startX);
	}
	
	public boolean hastraveledMinDist(double x, double y) {
		if (traveledDist)
			return true;
		traveledDist = getDistanceChange(x,y) > minDist;
		//Log.e("", "finger dist: " + getDistanceChange(x,y));
		return traveledDist;
	}

}
