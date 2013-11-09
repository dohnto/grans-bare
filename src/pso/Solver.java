package pso;

import java.util.ArrayList;

public class Solver {

	private FitnessFunction fitness;
	private int maxIterations;
	private int dimension;
	private double epsilon;
	private int rounds;
	private double inertiaWeightEnd;
	private double inertiaWeightStart;
	private int connections;
	private ArrayList<Particle> particles = new ArrayList<Particle>();
	private double c2;
	private double c1;

	public Solver(FitnessFunction fitness, int maxIterations, int dimension,
			double epsilon, int rounds, double inertiaWeightStart,
			double inertiaWeightEnd, int connections, double c1, double c2) {
		this.fitness = fitness;
		this.maxIterations = maxIterations;
		this.dimension = dimension;
		this.epsilon = epsilon;
		this.rounds = rounds;
		this.inertiaWeightStart = inertiaWeightStart;
		this.inertiaWeightEnd = inertiaWeightEnd;
		this.connections = connections;
		this.c1 = c1;
		this.c2 = c2;

		int particleCount = 10 + (int) (2 * Math.sqrt(dimension));

		for (int i = 0; i < particleCount; i++)
			particles.add(new Particle(fitness, dimension, c1, c2, inertiaWeightStart));

		// find the best global position and set it to all of them
		setBestGlobalPosition(getBestGlobalPosition());
	}

	public void solve() {
		for (int i = 0; i < maxIterations; i++) {
			step(i);
			 
			double fitnessValue = fitness.get(getBestGlobalPosition()); 
			if (fitnessValue < epsilon) {
				System.out.println("Found solution with evaluatiation " + fitnessValue);
				return;
			}
		}
	}

	private void step(int step) {
		updateParticlesVelocity();
		updateParticlesPosition();

		// find the best global position and set it to all of them
		setBestGlobalPosition(getBestGlobalPosition());
		System.out.println("=== Step " + (step + 1) + " ===");
		printParticlesData();
	}

	private void printParticlesData() {
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).print(i);
		}
	}

	private void updateParticlesPosition() {
		for (Particle particle : particles) {
			particle.updatePosition();
		}
	}

	private void updateParticlesVelocity() {
		for (Particle particle : particles) {
			particle.updateVelocity();
		}
	}

	private void setBestGlobalPosition(ArrayList<Double> pos) {
		for (Particle p : particles) {
			p.setBestGlobalPosition(pos);
		}
	}

	private ArrayList<Double> getBestGlobalPosition() {
		ArrayList<Double> retval = new ArrayList<>();
		double best = Double.MAX_VALUE;
		for (Particle p : particles) {
			ArrayList<Double> position = p.getBestParticlePosition();
			double fitnessValue = fitness.get(position);
			if (fitnessValue < best) {
				retval = new ArrayList<Double>(position);
				best = fitnessValue;
			}
		}
		return retval;
	}
}
