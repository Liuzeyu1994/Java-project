package controllers;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.HashMap;

import models.Edge;
import models.GameState;
import models.Node;
import student.MySpaceship;

public class BenchmarkDriver extends Driver {
	public static final long SEED = 91;
	public static final int N_TRIALS = 40;

	public BenchmarkDriver(long seed) {
		super(seed, new MySpaceship());
	}

	@Override public void beginRescueStage() {}
	@Override public void beginReturnStage() {}
	@Override public void setCumulativeDistance(int d) {}
	@Override public void setNodeAndEdge(Node n, Edge e) {}
	@Override public void setTime(double t) {}
	@Override public void moveShipAlong(Edge e) {}
	@Override public void setHp(int hp) {}
	@Override public void setSpeed(double s) {}
	@Override public void grabSpeedUpgrade(Node n) {}

	public static void main(String[] args) {
		Random r = new Random(SEED);
		double sum = 0;
		HashMap<Double,Long> hm = new HashMap<Double,Long>();
		
		PriorityQueue<Double> pq = new PriorityQueue<Double>(201, 
	            new Comparator<Double>(){
	                public int compare(Double a, Double b){
	                    if (a>b) return 1;
	                    if (a==b) return 0;
	                    return -1;
	                }
	            });
        
		for (int i = 0; i < N_TRIALS; i++) {
			long s = r.nextLong();
			Driver.shouldPrint = false;
			BenchmarkDriver b = new BenchmarkDriver(s);
			b.runGame();
			Driver.shouldPrint = true;
			GameState gs = b.getGameState();
			if (gs.getRescueSucceeded() && gs.getReturnSucceeded()) {
				sum += gs.getScore();
				pq.add(gs.getScore());
				hm.put(gs.getScore(), s);
			} else {
				Driver.errPrintln("Your Spaceship failed for seed " + s);
				System.exit(1);
			}
		}
		while(pq.size()!=0){
			double score = pq.remove();
			long seed = hm.get(score);
			System.out.println("seed: "+seed+" score: "+score);
		}
		Driver.outPrintln(sum / N_TRIALS + "");
		System.exit(0);
	}
}
