package br.usp.icmc.uml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHandler {

	public FileHandler(){
		
	}
	
	public void print_file(String out, String path) throws IOException {
		File file3 = new File(path);
		if (!file3.exists()) {
			file3.createNewFile();
		}
		FileWriter w = new FileWriter(file3);
		w.write(out);
		w.close();
	}
	
	public String getProcessOutput(String[] commands) throws IOException, InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder processOutput = new StringBuilder();

        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));)
        {
            String readLine;

            while ((readLine = processOutputReader.readLine()) != null)
            {
                processOutput.append(readLine + System.lineSeparator());
            }

            process.waitFor();
        }

        return processOutput.toString().trim();
    }
}
