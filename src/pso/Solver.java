package pso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Comparison;

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
	private int step;
	double inertiaStep;

	public Solver(FitnessFunction fitness, int maxIterations, int dimension,
			double epsilon, double inertiaWeightStart,
			double inertiaWeightEnd, int connections, double c1, double c2) {
		this.fitness = fitness;
		this.maxIterations = maxIterations;
		this.dimension = dimension;
		this.epsilon = epsilon;
		this.inertiaWeightStart = inertiaWeightStart;
		this.inertiaWeightEnd = inertiaWeightEnd;
		this.inertiaStep = (inertiaWeightStart - inertiaWeightEnd)
				/ maxIterations;
		this.connections = connections;
		this.c1 = c1;
		this.c2 = c2;

		int particleCount = 10 + (int) (2 * Math.sqrt(dimension));
		Particle.setC1(c1);
		Particle.setC2(c2);
		Particle.setW(inertiaWeightStart);
		Particle.setFitness(fitness);

		for (int i = 0; i < particleCount; i++)
			particles.add(new Particle(dimension));

		// find the best global position and set it to all of them
		updateBestGlobalPosition(connections);
	}

	public int solve() {
		System.out.println("#step bestFitnessFunction");
		for (step = 0; step < maxIterations; step++) {
			doStep();

			double fitnessValue = fitness.get(getBestGlobalPosition());
			if (fitnessValue < epsilon) {
				System.out.println("#Found solution in step " + (step + 1)
						+ " with fitness function " + fitnessValue);
				// System.out.println("#Best solution is "
				// + getBestGlobalPosition());
				return step + 1;
			}
		}
		return maxIterations;
	}

	private void doStep() {
		// printParticlesData();
		updateParticles();

		System.out.println((step + 1) + " "
				+ fitness.get(getBestGlobalPosition()));
	}

	private void updateParticles() {
		updateParticlesVelocity();
		updateParticlesPosition();
		updateParticlesInertia();
		updateBestGlobalPosition(connections);
	}

	private void updateParticlesInertia() {
		Particle.setW(Particle.getW() - inertiaStep);

	}

	private void updateBestGlobalPosition(int connections) {
		if (connections == -1) {
			setBestGlobalPositionToAllParticles(getBestGlobalPosition());
		} else {
			for (Particle p : particles) {
				// pick closest neighbours
				ArrayList<Particle> possibleNeighbours = new ArrayList<>(
						particles);
				Collections.sort(possibleNeighbours,
						new ParticleDistanceComparator(p));
				// pick connections + 1 closest particles (+1 because the examined particle is also in the list
				possibleNeighbours = new ArrayList<>(possibleNeighbours.subList(0, connections + 1));
				p.setBestGlobalPosition(getBestGlobalPosition(possibleNeighbours));
			}
		}
	}

	class ParticleDistanceComparator implements Comparator<Particle> {
		Particle particle;

		public ParticleDistanceComparator(Particle p) {
			particle = p;
		}

		private double getDistance(Particle p) {
			double distance = 0; 
			for (int i = 0; i < particle.getPosition().size(); i++) {
				distance += Math.pow(particle.getPosition().get(i)
						- p.getPosition().get(i), 2);
			}
			return Math.sqrt(distance);
		}

		@Override
		public int compare(Particle o1, Particle o2) {
			double o1distance = getDistance(o1);
			double o2distance = getDistance(o2);
			return Double.compare(o1distance, o2distance);
		}
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

	private void setBestGlobalPositionToAllParticles(ArrayList<Double> pos) {
		for (Particle p : particles) {
			p.setBestGlobalPosition(pos);
		}
	}

	private ArrayList<Double> getBestGlobalPosition(
			ArrayList<Particle> particles) {
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

	private ArrayList<Double> getBestGlobalPosition() {
		return getBestGlobalPosition(particles);
	}
}