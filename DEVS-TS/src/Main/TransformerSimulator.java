package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.codemodel.JClassAlreadyExistsException;

import Transformer.Broker;
import chequerUCM.Chequer;

/**
 * This class acts as an interface
 * 
 * @author: María Eva Villarreal Guzmán. E-mail: villarrealguzman@gmail.com
 *
 */
public class TransformerSimulator {

	private double int_arr_t;
	private Boolean isFinished = false;

	public String callChequerUCM(String path) {
		Chequer c = new Chequer(path);
		return c.isValid();
	}

	public String callTransformer(String inputPath, String outputPath) {
		Broker broker = new Broker();
		try {
			broker.generateSource(inputPath, outputPath);
			this.setInt_arr_t(broker.getStartPoint().getMeanTimeBRequest());
			return "";
		} catch (FileNotFoundException e) {
			return "FileNotFoundException";
		} catch (JClassAlreadyExistsException | IOException | ParserConfigurationException | SAXException e) {
			return e.toString();
		}
	}

	public Boolean callSimulator(String simEnvironmentPath, double observe_t) {

		try {

			// Load the jar
			File f = new File(simEnvironmentPath + "/simulator.jar");

			URLClassLoader urlClassLoaderSM = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });

			// Generate SimEnvironment object
			for (int i = 1; i < 11; i++) {
				Class classSE = Class.forName("SimEnvironment.SimEnvironment", true, urlClassLoaderSM);

				Class[] paramTypesSM = { String.class, double.class, double.class, String.class };
				Object[] paramValuesSM = { "prueba", this.getInt_arr_t(), observe_t,
						simEnvironmentPath + "/Run/Run" + i };
				Object objectSM = classSE.getConstructor(paramTypesSM).newInstance(paramValuesSM);

				// Generate digraph class, to make the cast
				Class classDigraph = Class.forName("model.modeling.digraph", true, urlClassLoaderSM);

				// Generate DevsSuiteFacade object
				Class classDSF = Class.forName("DevsSuiteFacade", true, urlClassLoaderSM);

				Class[] paramTypesDSF = { classDigraph };
				Object[] paramValuesDSF = { classDigraph.cast(objectSM) };

				Object objectDSF = classDSF.getConstructor(paramTypesDSF).newInstance(paramValuesDSF);

				// Generate entity object
				Class classEntity = Class.forName("GenCol.entity", true, urlClassLoaderSM);

				Class[] paramTypesEntity = { String.class };
				Object[] paramValuesEntity = { "start" };

				Object objectEntity = classEntity.getConstructor(paramTypesEntity).newInstance(paramValuesEntity);

				// invoke simulate methods

				Class[] paramTypesM1 = { double.class, String.class, classEntity };
				Object[] paramValuesM1 = { 0.0, "sip", classEntity.cast(objectEntity) };

				Class[] paramTypesM2 = { Integer.class };
				Object[] paramValuesM2 = { 2147483647 };

				Method methodS1 = classDSF.getDeclaredMethod("simulate", paramTypesM1);
				methodS1.invoke(objectDSF, paramValuesM1);

				Method methodS2 = classDSF.getDeclaredMethod("simulate", paramTypesM2);
				methodS2.invoke(objectDSF, paramValuesM2);

			}

			// The code above dynamically generates the following:
			// SimEnvironment model = new SimEnvironment("prueba");
			// DevsSuiteFacade fachada = new DevsSuiteFacade(model);
			// fachada.simulate(0, "sip", new entity("start"));
			// fachada.simulate(2147483647);

			// This cycle is used to synchronize with the end of the simulation
			while (!isFinished) {
				File file1 = new File(simEnvironmentPath + "/Run/Run1/reliability.csv");
				File file2 = new File(simEnvironmentPath + "/Run/Run2/reliability.csv");
				File file3 = new File(simEnvironmentPath + "/Run/Run3/reliability.csv");
				File file4 = new File(simEnvironmentPath + "/Run/Run4/reliability.csv");
				File file5 = new File(simEnvironmentPath + "/Run/Run5/reliability.csv");
				File file6 = new File(simEnvironmentPath + "/Run/Run6/reliability.csv");
				File file7 = new File(simEnvironmentPath + "/Run/Run7/reliability.csv");
				File file8 = new File(simEnvironmentPath + "/Run/Run8/reliability.csv");
				File file9 = new File(simEnvironmentPath + "/Run/Run9/reliability.csv");
				File file10 = new File(simEnvironmentPath + "/Run/Run10/reliability.csv");

				if (file1.exists() && file2.exists() && file3.exists() && file4.exists() && file5.exists()
						&& file6.exists() && file7.exists() && file8.exists() && file9.exists() && file10.exists())
					isFinished = true;
			}

			return true;
		} catch (Exception e) {
			System.err.println(e);
			return false;

		}
	}

	public double getInt_arr_t() {
		return int_arr_t;
	}

	public void setInt_arr_t(double int_arr_t) {
		this.int_arr_t = int_arr_t;
	}

}
