package app;

public class VectorUtil {

	public enum VectorType {
		POSITION,
		VELOCITY,
		LENGTH,
		GRAVITY,
		DOWNHILL_ACCELERATION,
		FRICTION
	}

	public static String toString(VectorType vectorType) {
		switch (vectorType) {
			case POSITION:
				return "Position";
			case VELOCITY:
				return "Velocity";
			case LENGTH:
				return "Length";
			case GRAVITY:
				return "Gravity";
			case DOWNHILL_ACCELERATION:
				return "Downhill Acceleration";
			case FRICTION:
				return "Friction";
			default:
				return null;
		}
	}
}
