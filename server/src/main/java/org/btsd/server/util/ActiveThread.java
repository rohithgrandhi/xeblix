package org.btsd.server.util;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import org.btsd.server.messages.Message;

public abstract class ActiveThread extends Thread {

	private final Semaphore available = new Semaphore(0, true);
	private LinkedList<Message> messages;
	
	public ActiveThread(){
		this.messages = new LinkedList<Message>();
	}
	
	public final  synchronized void addMessage(Message message){
		this.messages.add(message);
		available.release();
	}
	
	public final synchronized void addMessage(final Message message, final long delayInMS) {
		
		Thread delayThread = new Thread(){
			@Override
			public void run() {
				
				try{
					System.out.println("Scheduling message to run in: " + delayInMS + "MS");
					sleep(delayInMS);
					System.out.println("Running scheduled message");
					addMessage(message);					
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}
		};
		delayThread.start();
	}
	
	private final synchronized Message getMessage(){
		return this.messages.poll();
	}
	
	@Override
	public final void run() {
		
		try{
			while(true){
				available.acquire();
				handleMessage(getMessage());
			}
		
		}catch(InterruptedException ex){
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}catch(ShutdownException ex){
			//shutting down thread. Do nothing
		}
	}
	
	public abstract void handleMessage(Message msg);
}
