import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.management.ThreadMXBean;

import Main.TransformerSimulator;

/**
 * Esta clase solo sirve para probar
 * 
 * @author Eva
 *
 */
public class Test {

	public static void main(String[] args) {
		


		// Create jar
		/*try {

			OutputStream output = new FileOutputStream(
					"C:/Users/Usuario-Pc/Desktop/eclipse/project.jar");
			JarUtil.jar(new File("C:/Users/Usuario-Pc/Documents/Prueba/eclipse-modeling-mars-2-win32-x86_64/eclipse/workspace/Simulator"),
					output, false);

		} catch (Exception e) {

		}*/

		/*
		 * try {
		 * 
		 * java.util.jar.JarFile jar; jar = new java.util.jar.JarFile(
		 * "C:/Users/Usuario-Pc/Desktop/eclipse mars/eclipse/plugins/UCM2DEVS/simulator.jar"
		 * );
		 * 
		 * java.util.Enumeration enumEntries = jar.entries(); while
		 * (enumEntries.hasMoreElements()) { java.util.jar.JarEntry file =
		 * (java.util.jar.JarEntry) enumEntries.nextElement(); java.io.File f =
		 * new java.io.File( "C:/Users/Usuario-Pc/Desktop/Hola" +
		 * java.io.File.separator + file.getName()); if (file.isDirectory()) {
		 * // if its a directory, create it f.mkdirs(); continue; }
		 * java.io.InputStream is = jar.getInputStream(file); // get the //
		 * input // stream java.io.FileOutputStream fos = new
		 * java.io.FileOutputStream(f); while (is.available() > 0) { // write
		 * contents of 'is' to 'fos' fos.write(is.read()); } fos.close();
		 * is.close(); } } catch (IOException e) { System.out.println(e); }
		 */


		  TransformerSimulator ts = new TransformerSimulator();
		  
		  //System.out.print("aaa" + ts.callChequerUCM("C:/Users/Usuario-Pc/git/devs-ts/DEVS-TS/src/Test/chequerUCMTest/UCM/prueba211.jucm"));
		  
		// System.out.println(ts.callChequerUCM( "C:/Users/Usuario-Pc/Documents/Prueba/workspace nuevo/Hola/src/cs-pf.jucm"));
		  
		  ///C:/Users/Usuario-Pc/Documents/Prueba/workspace nuevo/Hola/src/Ejemplo.jucm"
		  //C:/Users/Usuario-Pc/Documents/Prueba/workspace nuevo/Hola/src/cs-pf.jucm
		  //C:/Users/Usuario-Pc/git/devs-ts/DEVS-TS/src/Test/Ejemplo.jucm
		  System.out.println(ts.callTransformer("C:/Users/Usuario-Pc/Documents/Prueba/workspace nuevo/Hola/src/cs-pfs.jucm",
		  "C:/Users/Usuario-Pc/Documents/Prueba/eclipse-modeling-mars-2-win32-x86_64/eclipse/plugins/SAE"));
		 ts.setInt_arr_t(28800);
		/* ts.callSimulator("C:/Users/Usuario-Pc/Documents/Prueba/eclipse-modeling-mars-2-win32-x86_64/eclipse/plugins/SAE", 3147483.647);
		 */
/**
		try {
			ArrayList<String> classNames = new ArrayList<String>();
			ZipInputStream zip = new ZipInputStream(new FileInputStream(
					"C:/Users/Usuario-Pc/Desktop/eclipse mars/eclipse/plugins/UCM2DEVS/reports.jar"));
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				if (!entry.isDirectory()) { // This ZipEntry represents a class.
											// Now, what class does it
											// represent? 
					String className = entry.getName().replace('/', '.'); // including
																	// ".class"
					classNames.add(className);
				}
			}

			for (int i = 0; i < classNames.size(); i++) {
				System.out.println(classNames.get(i));
			}
		} catch (Exception e) {

		}*/

		/*
		 * if (ts.callSimulator(
		 * "C:/Users/Usuario-Pc/Desktop/eclipse mars/eclipse/plugins/UCM2DEVS",
		 * 2000)) { System.out.println("asdfsafasfdasfasfdsafasd"); }
		 */

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

}

