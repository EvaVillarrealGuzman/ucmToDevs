package Transformer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class executes a command in the cmd to compile previously .java classes
 * creates previously
 * 
 * @author: María Eva Villarreal Guzmán. E-mail: villarrealguzman@gmail.com
 *
 */
public class ExecuteShellComand {
	public static void compileCommand(String path) {
		try {
			path = path.replace("/", "\\");

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
					"cd \"" + path + "\" && javac -cp . SimEnvironment/SimEnvironment.java");

			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) {
					break;
				}
				System.out.println(line);
			}
		} catch (Exception e) {

		}

	}

}
