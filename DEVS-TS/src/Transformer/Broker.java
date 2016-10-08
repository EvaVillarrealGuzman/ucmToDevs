package Transformer;

import java.io.File;
import java.io.IOException;

import javax.swing.JTree;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;

import Transformer.Domain.StartPoint;

public class Broker {
	JCodeModel codeModel;
	JTree xmlTree;
	StartPoint startPoint;

	public void generateSource(String inputPath, String outputPath) throws JClassAlreadyExistsException, IOException {
		// Instantiate an instance of the JCodeModel class
		codeModel = new JCodeModel();

		Object[] result;

		// Crea primero las clases estáticas
		/*
		 * StaticClass stc = new StaticClass(codeModel);
		 * stc.createFailureClass(); stc.createFailureGeneratorClass();
		 * stc.createIntervalClass(); stc.createMeasureMsgClass();
		 * stc.createRequestClass();
		 * stc.createResponsabilityDEVSClass(stc.getRequestClass());
		 * stc.createCPXResponsabilityClass(stc.getResponsabilityDEVSClass(),
		 * stc.getFailureGeneratorClass());
		 * 
		 * ExperimentalFrame ef = new ExperimentalFrame(codeModel);
		 * ef.createSAAvailabilityStatClass(); ef.createSAEAcceptorClass();
		 * ef.createSAEGeneratorClass(); ef.createSAEExperimentalClass();
		 * ef.createSAPerformanceStatClass();
		 * ef.createSARealiabilityStatClass();
		 */

		/* obtiene el objeto xml que debe crear */
		ConvertUCM2JTree xmlObject = new ConvertUCM2JTree();
		result = xmlObject.convertToTree(inputPath);

		xmlTree = (JTree) result[0];

		startPoint = (StartPoint) result[1];

		DynamicClass dc = new DynamicClass();
		dc.createSaViewDEVS(xmlTree, codeModel);

		// This will generate the code in the specified file path.
		codeModel.build(new File(outputPath));
	}

}
