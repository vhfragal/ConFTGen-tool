package br.usp.icmc.uml;

import java.util.ArrayList;

public class Configuration {

	protected ArrayList<CState> configuration;
	
	public Configuration(ArrayList<CState> configuration) {
		this.configuration = configuration;
	}
			
	public void setConfiguration(ArrayList<CState> configuration) {
		this.configuration = configuration;
	}
		
	public ArrayList<CState> getConfiguration() {
		return configuration;
	}
}
