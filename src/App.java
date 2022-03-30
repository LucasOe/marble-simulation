public class App {

	private static double FPS = 60; // Asssumes static fps

	public static void main(String[] args) throws Exception {
		// Start Values
		Vector pos = new Vector(0, 5);
		Vector vel = new Vector(2, 1);
		Vector inf = new Vector(0, -9.83);

		double deltaTime = 1 / FPS;

		for (int i = 0; i < 3; i++) {
			pos = calculateNewPos(pos, vel, inf, deltaTime);
			vel = calculateNewVel(pos, vel, inf, deltaTime);

			// Print
			System.out.println("Frame: " + i);
			System.out.println("Pos: [x: " + pos.getX() + ", y: " + pos.getY() + "]");
			System.out.println("Vel: [x: " + vel.getX() + ", y: " + vel.getY() + "]");
		}

	}

	private static Vector calculateNewPos(Vector pos, Vector vel, Vector inf, double deltaTime) {
		return pos.addVector(vel.multiply(deltaTime)).addVector(inf.multiply(0.5)
				.multiply(deltaTime * deltaTime));
	}

	private static Vector calculateNewVel(Vector pos, Vector vel, Vector inf, double deltaTime) {
		return vel.addVector(inf.multiply(deltaTime));
	}
}
