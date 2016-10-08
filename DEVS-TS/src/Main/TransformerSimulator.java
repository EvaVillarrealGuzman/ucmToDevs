package Main;

import java.io.IOException;

import com.sun.codemodel.JClassAlreadyExistsException;

import GenCol.entity;
import SimEnvironment.SimEnvironment;
import Transformer.Broker;
import chequerUCM.Chequer;

public class TransformerSimulator {

	public String callChequerUCM(String path) {
		Chequer c = new Chequer(path);
		return c.isValid();
	}

	public Boolean callTransformer(String inputPath, String outputPath) {
		Broker broker = new Broker();
		try {
			broker.generateSource(inputPath, outputPath);
			return true;
		} catch (JClassAlreadyExistsException | IOException e) {
			return false;
		}
	}

	public Boolean callSimulator() {
		try {
			SimEnvironment model = new SimEnvironment("prueba");
			DevsSuiteFacade fachada = new DevsSuiteFacade(model);
			fachada.simulate(0, "sip", new entity("start"));
			fachada.simulate(2000000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
