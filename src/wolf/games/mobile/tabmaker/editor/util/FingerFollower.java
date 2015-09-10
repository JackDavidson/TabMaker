package wolf.games.mobile.tabmaker.editor.util;

public class FingerFollower {
	double startX;
	double startY;
	double minDist = 30;
	boolean traveledDist = false;

	public FingerFollower(double x, double y) {
		this.startX = x;
		this.startY = y;
	}

	public double getDistanceChange(double x, double y) {
		double xDist = startX - x;
		double yDist = startY - y;
		return Math.sqrt(xDist * xDist + yDist * yDist);
	}

	public double getDistanceX(double curX) {
		return Math.abs(curX - startX);
	}
	
	public boolean hastraveledMinDist(double x, double y) {
		if (traveledDist)
			return true;
		traveledDist = getDistanceChange(x,y) > minDist;
		return traveledDist;
	}

}