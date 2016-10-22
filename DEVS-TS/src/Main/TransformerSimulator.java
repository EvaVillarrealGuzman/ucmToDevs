package Main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import com.sun.codemodel.JClassAlreadyExistsException;

import Transformer.Broker;
import chequerUCM.Chequer;

public class TransformerSimulator {

	private double int_arr_t;
	
	public String callChequerUCM(String path) {
		Chequer c = new Chequer(path);
		return c.isValid();
	}

	public Boolean callTransformer(String inputPath, String outputPath) {
		Broker broker = new Broker();
		try {
			broker.generateSource(inputPath, outputPath);
			this.setInt_arr_t(broker.getStartPoint().getMeanTimeBRequest());
			return true;
		} catch (JClassAlreadyExistsException | IOException e) {
			return false;
		}
	}

	public Boolean callSimulator(String simEnvironmentPath, double observe_t) {

		try {

			// Carga el jar
			File f = new File(simEnvironmentPath + "/simulator.jar");

			URLClassLoader urlClassLoaderSM = URLClassLoader.newInstance(new URL[] { f.toURI().toURL() });

			// Genera el objeto SimEnvironment
			Class classSE = Class.forName("SimEnvironment.SimEnvironment", true, urlClassLoaderSM);

			Class[] paramTypesSM = { String.class, double.class, double.class };
			Object[] paramValuesSM = { "prueba", this.getInt_arr_t(), observe_t };
			Object objectSM = classSE.getConstructor(paramTypesSM).newInstance(paramValuesSM);

			// Genera la clase digraph, para hacer el casteo
			Class classDigraph = Class.forName("model.modeling.digraph", true, urlClassLoaderSM);

			// Genera el objeto DevsSuiteFacade
			Class classDSF = Class.forName("Main.DevsSuiteFacade", true, urlClassLoaderSM);

			Class[] paramTypesDSF = { classDigraph };
			Object[] paramValuesDSF = { classDigraph.cast(objectSM) };
			// System.out.println(classSE.cast(objectSM));

			Object objectDSF = classDSF.getConstructor(paramTypesDSF).newInstance(paramValuesDSF);

			// Genera el objeto entity
			Class classEntity = Class.forName("GenCol.entity", true, urlClassLoaderSM);

			Class[] paramTypesEntity = { String.class };
			Object[] paramValuesEntity = { "start" };
			// System.out.println(classSE.cast(objectSM));

			Object objectEntity = classEntity.getConstructor(paramTypesEntity).newInstance(paramValuesEntity);

			// invoca m�todos simulate

			Class[] paramTypesM1 = { double.class, String.class, classEntity };
			Object[] paramValuesM1 = { 0.0, "sip", classEntity.cast(objectEntity) };

			Method methodS1 = classDSF.getDeclaredMethod("simulate", paramTypesM1);
			methodS1.invoke(objectDSF, paramValuesM1);

			Class[] paramTypesM2 = { Integer.class };
			Object[] paramValuesM2 = { 20000 };

			Method methodS2 = classDSF.getDeclaredMethod("simulate", paramTypesM2);
			methodS2.invoke(objectDSF, paramValuesM2);

			// El c�digo anterior intenta generar en forma din�mica lo que
			// sigue:
			// SimEnvironment model = new SimEnvironment("prueba");
			// DevsSuiteFacade fachada = new DevsSuiteFacade(model);
			// fachada.simulate(0, "sip", new entity("start"));
			// fachada.simulate(2000000);
			return true;
		} catch (Exception e) {
			System.out.println(e);
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
