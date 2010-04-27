package com.btsd.util;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import android.os.Message;

import com.btsd.ShutdownException;

public abstract class ActiveThread extends Thread {

	private final Semaphore available = new Semaphore(0, true);
	private LinkedList<Message> messages;
	
	public ActiveThread(){
		this.messages = new LinkedList<Message>();
	}
	
	public synchronized void addMessage(Message message){
		this.messages.add(message);
		available.release();
	}
	
	public synchronized Message getMessage(){
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
			
		}catch(ShutdownException ex){
			
		}
	}
	
	public abstract void handleMessage(Message msg);
}
