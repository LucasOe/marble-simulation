public class Main {

	private static double FPS = 60; // Asssumes static fps for now

	public static void main(String[] args) throws Exception {
		// Start Values
		Vector startPosition = new Vector(0, 5);
		Vector startVelocity = new Vector(2, 1);
		Vector startInfluences = new Vector(0, -9.81);

		Marble marble = new Marble(startPosition, startVelocity, startInfluences);

		for (int i = 0; i < 5; i++) {
			double deltaTime = 1 / FPS;

			// Calculates and return new position and velocity
			Vector position = marble.calculateNewPos(deltaTime);
			Vector velocity = marble.calculateNewVel(deltaTime);

			// Print
			System.out.println("Frame: " + i);
			System.out.println("Pos: [x: " + position.getX() + ", y: " + position.getY() + "]");
			System.out.println("Vel: [x: " + velocity.getX() + ", y: " + velocity.getY() + "]");
		}

	}
}
