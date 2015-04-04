/**
	This program makes an estimate of the value of pi. Assuming a circle with a radius of one has a surface area equal to pi, the value of pi can be estimated
	by taking the amount of times a random point in a square with sides equal to one lies within a quarter of the circle. If x*x + y*y < 1, then the point lies
	within this quarter-circle. Many trials are done and an average is taken of the results, which should approximate pi.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class PiEstimate extends JPanel implements ActionListener{
	
	private static JLabel piEstimateLabel;
	private static JLabel trials;
	private static Timer estimateUpdate;
	private volatile static long trialNum = 0;
	private volatile static long inCircleCount;
	private static JButton startStop;
	private volatile static boolean running = true;
	
	public static void main(String[] args){
		
		//Display the panel
		JFrame window = new JFrame("Pi Estimator");
		PiEstimate content = new PiEstimate();
		window.setContentPane(content);
		window.pack();
		window.setVisible(true);
		
		//Set up threads
		int threadCount = Runtime.getRuntime().availableProcessors();
		for(int i = 0; i < threadCount; i++){
			new EstimatorThread();
		}
		
	}
	
	public PiEstimate(){//Create the panel.
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel pi = new JLabel("Pi: " + Math.PI);
		pi.setBorder(BorderFactory.createEtchedBorder());
		add(pi);
		
		piEstimateLabel = new JLabel("Estimate: 0");
		piEstimateLabel.setBorder(BorderFactory.createEtchedBorder());
		add(piEstimateLabel);
		
		trials = new JLabel("Trials: " + trialNum);
		trials.setBorder(BorderFactory.createEtchedBorder());
		add(trials);
		
		startStop = new JButton("Stop");
		startStop.addActionListener(this);
		add(startStop);
		
		estimateUpdate = new Timer(500, new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				
				double piEstimate = 4 * ((double)inCircleCount / trialNum);
				piEstimateLabel.setText("Estimate: " + piEstimate);
				trials.setText("Trials: " + trialNum);
				
			}
			
		});
		estimateUpdate.start();
		
	}
	
	synchronized public void actionPerformed(ActionEvent e){
		
		if(running){
			startStop.setText("Start");
			running = false;
			estimateUpdate.stop();
		}
		else{
			startStop.setText("Stop");
			synchronized(startStop){
				startStop.notifyAll();
			}
			running = true;
			estimateUpdate.start();
		}
		
	}
	
	private static class EstimatorThread extends Thread{
		
		public EstimatorThread(){
			start();
		}
		
		public void run(){
			
			Random myRandom = new Random();
			
			while(true){
				
				synchronized(startStop){
					if(!running){
						try{
							startStop.wait();
						}
						catch(InterruptedException e){
							e.printStackTrace();
						}
					}
				}
				
				double x = myRandom.nextDouble();
				double y = myRandom.nextDouble();
				trialNum++;
				if (x*x + y*y < 1)
					inCircleCount++;
				
			}
			
		}
		
	}
	
}