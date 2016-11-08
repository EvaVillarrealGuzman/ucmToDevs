import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import Main.TransformerSimulator;

public class Test {

	public static void main(String[] args) {

		TransformerSimulator ts = new TransformerSimulator();
		ts.callTransformer("C:/Users/Usuario-Pc/git/DEVS-TS/DEVS-TS/src/Test/cs-pf.jucm",
				"C:/Users/Usuario-Pc/Desktop/eclipse mars/eclipse/plugins/UCM2DEVS");

		/*
		 * try { Process pro=Runtime.getRuntime().exec("javac *", null, new
		 * File(
		 * "C:/Users/Usuario-Pc/Desktop/eclipse mars/eclipse/plugins/UCM2DEVS/Simulator/src/SimEnvironment/SAModel/SystemTemp"
		 * )); System.out.println("asdfs"); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		/*
		 * try { ArrayList<String> classNames = new ArrayList<String>();
		 * ZipInputStream zip = new ZipInputStream(new FileInputStream(
		 * "C:/Users/Usuario-Pc/Desktop/eclipse mars/eclipse/plugins/UCM2DEVS/simulator.jar"
		 * )); for (ZipEntry entry = zip.getNextEntry(); entry != null; entry =
		 * zip.getNextEntry()) { if (!entry.isDirectory()) { // This ZipEntry
		 * represents a class. Now, what class does it represent? String
		 * className = entry.getName().replace('/', '.'); // including ".class"
		 * classNames.add(className); } }
		 * 
		 * for(int i = 0; i < classNames.size(); i++) {
		 * System.out.println(classNames.get(i)); } }catch(Exception e){
		 * 
		 * }
		 */

		if (ts.callSimulator("C:/Users/Usuario-Pc/Desktop/eclipse mars/eclipse/plugins/UCM2DEVS", 2000)) {

		/*	File file = new File(
					"C:/Users/Usuario-Pc/Desktop/eclipse mars/eclipse/plugins/UCM2DEVS/Simulator/src/SimEnvironment/SAModel/SystemTemp/");

			String[] myFiles;
			if (file.isDirectory()) {
				myFiles = file.list();
				for (int i = 0; i < myFiles.length; i++) {
					File myFile = new File(file, myFiles[i]);
					System.out.println(myFile);
					myFile.delete();
				}
			}*/

			System.out.println("asdfsafasfdasfasfdsafasd");
		}

		// ConvertUCM2JTree xmlObject = new ConvertUCM2JTree();
		// xmlObject.convertToTree(System.getProperty("user.dir") +
		// "/src/UCM/cs-pf-broker.jucm");

		// Chequer c = new Chequer(System.getProperty("user.dir") +
		// "/src/Test/chequerUCMTest/UCM/prueba3.jucm");
		// System.out.println(c.isValid());

		/*
		 * ClassLoader classLoader = Test.class.getClassLoader();
		 * 
		 * 
		 * File file = new File(
		 * "C:/Users/Usuario-Pc/git/DEVS-TS/DEVS-TS/src/Main/TransformerSimulator.java"
		 * ); System.out.println("aClass.getName() = " + file.exists());
		 * 
		 * try { //Class aClass =
		 * classLoader.loadClass(Platform.getInstallLocation().getURL().getPath(
		 * ) + "plugins/UCM2DEVS/SimEnvironment/SimEnvironment.java"); //Class
		 * aClass = classLoader.loadClass("Test"); URLClassLoader
		 * urlClassLoader; try { urlClassLoader = URLClassLoader.newInstance(new
		 * URL[] { new File(
		 * "C:/Users/Usuario-Pc/git/DEVS-TS/DEVS-TS/src/Main/TransformerSimulator.java"
		 * ).toURI().toURL() });
		 * 
		 * Class aClass = urlClassLoader.loadClass("TransformerSimulator");
		 * 
		 * //Class aClass =
		 * classLoader.loadClass("Users.Usuario-Pc.git.DEVS-TS.DEVS-TS.src.Test"
		 * ); System.out.println("aClass.getName() = " + aClass.getName());
		 * 
		 * try { Object test = aClass.newInstance();
		 * 
		 * Method method; try { method = aClass.getMethod("callSimulator",
		 * null); try { method.invoke(test, null); } catch
		 * (IllegalArgumentException | InvocationTargetException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } } catch
		 * (NoSuchMethodException | SecurityException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 * 
		 * 
		 * } catch (InstantiationException | IllegalAccessException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * } catch (MalformedURLException e2) { // TODO Auto-generated catch
		 * block e2.printStackTrace(); }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * } catch (ClassNotFoundException e) { e.printStackTrace(); }
		 */

	}

	private static void printLines(String cmd, InputStream ins) throws Exception {
		String line = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			System.out.println(cmd + " " + line);
		}
	}

	private static void runProcess(String command) throws Exception {
		Process pro = Runtime.getRuntime().exec(command);
		printLines(command + " stdout:", pro.getInputStream());
		printLines(command + " stderr:", pro.getErrorStream());
		pro.waitFor();
		System.out.println(command + " exitValue() " + pro.exitValue());
	}

}
