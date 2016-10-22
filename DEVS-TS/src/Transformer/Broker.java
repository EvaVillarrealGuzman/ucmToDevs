package Transformer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTree;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;

import Transformer.Domain.StartPoint;

public class Broker {
	private JCodeModel codeModel;
	private JTree xmlTree;
	private StartPoint startPoint;

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
		codeModel.build(new File(outputPath+ "/Simulator/src"));
		
		//compile class created
		/* JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		 
		 File dirDynamicClass = new File(outputPath + "/Simulator/src/SimEnvironment/SAModel/SystemTemp");
		
		 File[] fileNames = dirDynamicClass.listFiles();
	      for (int i = 0; i < fileNames.length; i++) {
	    	  System.out.println(compiler.run(null, null, null, fileNames[i].getPath()));
	      }*/
	      
		//Create jar
		OutputStream output = new FileOutputStream(outputPath + "/simulator.jar");
		JarUtil.jar(new File(outputPath + "/Simulator/bin"), output,false);
	}

	public JCodeModel getCodeModel() {
		return codeModel;
	}

	public void setCodeModel(JCodeModel codeModel) {
		this.codeModel = codeModel;
	}

	public JTree getXmlTree() {
		return xmlTree;
	}

	public void setXmlTree(JTree xmlTree) {
		this.xmlTree = xmlTree;
	}

	public StartPoint getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(StartPoint startPoint) {
		this.startPoint = startPoint;
	}

}
