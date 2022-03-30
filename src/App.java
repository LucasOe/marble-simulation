public class App {

	private static double FPS = 60; // Asssumes static fps

	public static void main(String[] args) throws Exception {
		// Start Values
		Vector startPos = new Vector(0, 5);
		Vector startVel = new Vector(2, 1);
		Vector startInf = new Vector(0, -9.83);

		double deltaTime = 1 / FPS;

		Vector newPos = calculateNewPos(startPos, startVel, startInf, deltaTime);
		Vector newVel = calculateNewVel(startPos, startVel, startInf, deltaTime);

		newPos.print();
		newVel.print();
	}

	private static Vector calculateNewPos(Vector pos, Vector vel, Vector inf, double deltaTime) {
		return pos.addVector(vel.multiply(deltaTime)).addVector(inf.multiply(0.5)
				.multiply(deltaTime * deltaTime));
	}

	private static Vector calculateNewVel(Vector pos, Vector vel, Vector inf, double deltaTime) {
		return vel.addVector(inf.multiply(deltaTime));
	}
}
