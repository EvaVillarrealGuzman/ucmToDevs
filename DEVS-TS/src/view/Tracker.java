/* 
 * Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 
package view;

/**
 * Facade Connections
 * Tracker: tracks data from the facade layer
 */
import facade.modeling.*;
import view.timeView.Event;


//Standard API Imports
import java.awt.*;

import javax.swing.*;

import view.timeView.*;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

public class Tracker
{
    private boolean trackPhase;
    private boolean trackSigma;
    private boolean trackTL;
    private boolean trackTN;
    
    private boolean isTimeViewSelected;
    private boolean istrackinglogselected;   //to make one tracking log panel
    
    private int modelNum;
    private Event e;
    private double time;
    private double TN;
    
    private boolean atLeastOneInputTracked;
    private boolean atLeastOneOutputTracked;
    private boolean[] trackInputPorts;
    private boolean[] trackOutputPorts;
    private List timeViewData;
    private List[] dataStorage;
    private String[] dataHeaderList;
    private boolean isAtomic;
    private int uniqueID;
   
    private TrackingControl trackingControl;   
    private FModel model;    
    
    public Tracker (FModel model, int num)
    {
        this.model = model;
        modelNum = num;      //model number for this tracker
        TN = 0;
        
        trackPhase = false;
        trackSigma = false;
        trackTL    = false;
        trackTN    = false;
        atLeastOneInputTracked = false;
        atLeastOneOutputTracked = false;
        trackInputPorts  = new boolean[model.getInputPortNames().size()];
        trackOutputPorts = new boolean[model.getOutputPortNames().size()];        
        isTimeViewSelected = false;
        istrackinglogselected = false;
        
        for (int i = 0; i < trackInputPorts.length; i++)
            trackInputPorts[i] = false;
        for (int i = 0; i < trackOutputPorts.length; i++)
            trackOutputPorts[i] = false;
        
        uniqueID = 0;
        isAtomic = model instanceof FAtomicModel;
                
        trackingControl = new TrackingControl();       
        
        ArrayList dataStore  = new ArrayList(10);
        ArrayList dataHeader = new ArrayList(10); 
        dataStore.add(new LinkedList()); //Phase
        dataHeader.add("Phase");
        dataStore.add(new LinkedList()); //Sigma
        dataHeader.add("Sigma");
        
        dataStore.add(new LinkedList()); //TL
        dataHeader.add("TL");
        dataStore.add(new LinkedList()); //TN
        dataHeader.add("TN");
        
        List inputPortNames = model.getInputPortNames();
        for (int i = 0; i < inputPortNames.size(); i++)
        {
            dataStore.add(new LinkedList()); //Next InputPort
            dataHeader.add(inputPortNames.get(i));
        }
        
        List outputPortNames = model.getOutputPortNames();
        for (int i = 0; i < outputPortNames.size(); i++)
        {
            dataStore.add(new LinkedList()); //Next OutputPort
            dataHeader.add(outputPortNames.get(i));
        }
        
        dataStorage = (List[])dataStore.toArray(new LinkedList[0]);
        dataHeaderList = (String[])dataHeader.toArray(new String[0]);
    }
    
    public void showDialogPanel()
    {
    	ArrayList graphs = new ArrayList();
    	
        JPanel panel = new JPanel(new GridLayout(2,4));
        
        JPanel modelPanel = new JPanel(new GridLayout(0,1));
        
        modelPanel.setBorder(BorderFactory.createTitledBorder("States/Unit"));
        
        JTextField phaseUnit = new JTextField();
        JTextField sigmaUnit = new JTextField();
        JTextField tlUnit = new JTextField();
        JTextField tnUnit = new JTextField();
        
        JPanel unitPane = new JPanel(new GridLayout(0,2));
        
        JCheckBox phase = new JCheckBox("Phase",trackPhase);
        JCheckBox sigma = new JCheckBox("Sigma",trackSigma);
        JCheckBox tl = new JCheckBox("TL",trackTL);
        JCheckBox tn = new JCheckBox("TN",trackTN);
        
        if (model instanceof FAtomicModel)
        {
        	
        	unitPane.add(phase); 
        	unitPane.add(phaseUnit);
        	modelPanel.add(unitPane);
        	
        	unitPane = new JPanel(new GridLayout(0,2));
        	unitPane.add(sigma); 
        	unitPane.add(sigmaUnit);
        	modelPanel.add(unitPane);
        }
        
        unitPane = new JPanel(new GridLayout(0,2));
    	unitPane.add(tl); 
    	unitPane.add(tlUnit);
    	modelPanel.add(unitPane);
        
    	unitPane = new JPanel(new GridLayout(0,2));
    	unitPane.add(tn); 
    	unitPane.add(tnUnit);
    	modelPanel.add(unitPane);
    	
        modelPanel.add(Box.createHorizontalStrut(100));
        
        panel.add(modelPanel);
        
        JPanel inputPanel = new JPanel(new GridLayout(0,1));
        
        List inputPortNames = model.getInputPortNames();
        JCheckBox[] inputPorts = new JCheckBox[inputPortNames.size()];
        JTextField[] inputUnits = new JTextField[inputPortNames.size()];
        
        for (int i = 0; i < inputPorts.length; i++)
        {
            inputPorts[i] = new JCheckBox((String)inputPortNames.get(i),trackInputPorts[i]);
            inputUnits[i] = new JTextField();
            unitPane = new JPanel(new GridLayout(0,2));
        	unitPane.add(inputPorts[i]); 
        	unitPane.add(inputUnits[i]);
        	inputPanel.add(unitPane);
        }
        
        inputPanel.add(Box.createHorizontalStrut(100));
        
        JPanel inset1 = new JPanel();
        inset1.add(inputPanel);
        JScrollPane inputScrollPane = new JScrollPane(inset1);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("Input Ports/Unit"));
        panel.add(inputScrollPane);
        
        JPanel outputPanel = new JPanel(new GridLayout(0,1));
        
        List outputPortNames = model.getOutputPortNames();
        JCheckBox[] outputPorts = new JCheckBox[outputPortNames.size()];
        JTextField[] outputUnits = new JTextField[outputPortNames.size()];
        
        for (int j= 0; j < outputPorts.length; j++)
        {
            outputPorts[j] = new JCheckBox((String)outputPortNames.get(j),trackOutputPorts[j]);
            outputUnits[j] = new JTextField();
            unitPane = new JPanel(new GridLayout(0,2));
        	unitPane.add(outputPorts[j]); 
        	unitPane.add(outputUnits[j]);
        	outputPanel.add(unitPane);
        }
        outputPanel.add(Box.createHorizontalStrut(100));
        JPanel inset2 = new JPanel();
        inset2.add(outputPanel);
        JScrollPane outputScrollPane = new JScrollPane(inset2);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Output Ports/Unit"));
        panel.add(outputScrollPane);
        
        //X-axis Unit
        JTextField xUnit = new JTextField("sec");
        xUnit.setHorizontalAlignment(SwingConstants.RIGHT);
        JPanel axisPanel = new JPanel(new GridLayout(0,1));                
        JLabel xaxis = new JLabel("X-Axis");
        unitPane = new JPanel(new GridLayout(0,2));
    	unitPane.add(xaxis); 
    	unitPane.add(xUnit);
        axisPanel.add(unitPane);
      
        //Time Increment
        JTextField timeIncre = new JTextField("10");
        timeIncre.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel time = new JLabel("Increment");
        unitPane = new JPanel(new GridLayout(0,2));
    	unitPane.add(time); 
    	unitPane.add(timeIncre);        
    	axisPanel.add(unitPane);
        
        axisPanel.add(Box.createHorizontalStrut(100));
        
        JPanel axisPanel2 = new JPanel();
        axisPanel2.add(axisPanel);
        JScrollPane timeViewScrollPane = new JScrollPane(axisPanel2);
        timeViewScrollPane.setBorder(BorderFactory.createTitledBorder("X-Axis/Unit"));
        panel.add(timeViewScrollPane);
       

        JPanel timeViewPanel = new JPanel(new GridLayout(0,1));                
        JCheckBox timeView = new JCheckBox("TimeView",isTimeViewSelected);
        JCheckBox htmltracking = new JCheckBox("Tracking Log", istrackinglogselected);
        timeViewPanel.add(timeView);        
        timeViewPanel.add(htmltracking);        
        timeViewPanel.add(Box.createHorizontalStrut(100));
        
        JPanel timeViewPanel2 = new JPanel();
        timeViewPanel2.add(timeViewPanel);
        JScrollPane timeViewScrollPane1 = new JScrollPane(timeViewPanel2);
        timeViewScrollPane1.setBorder(BorderFactory.createTitledBorder("View Options"));
        panel.add(timeViewScrollPane1);
       
        
        int ok = JOptionPane.showConfirmDialog(View.PARENT_FRAME,panel,"Set Tracking Options: "+model.getName(),
                                               JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION)
        {
            this.trackPhase = phase.isSelected();
            this.trackSigma = sigma.isSelected();
            this.trackTL    = tl.isSelected();
            this.trackTN    = tn.isSelected();
            
            atLeastOneInputTracked = false;
            
            for (int i = 0; i < inputPorts.length; i++)
            {
                trackInputPorts[i] = inputPorts[i].isSelected();
                if (trackInputPorts[i])
                    atLeastOneInputTracked = true;
               
                if(inputPorts[i].isSelected())
               	 graphs.add(new Graph((inputPorts[i].getLabel()), Graph.INPUT, Graph.STRING, inputUnits[i].getText()));
               
            }
            
            atLeastOneOutputTracked = false;
            
            for (int j = 0; j < outputPorts.length; j++)
            {
                trackOutputPorts[j] = outputPorts[j].isSelected();
                if (trackOutputPorts[j])
                    atLeastOneOutputTracked = true;
                
                if(outputPorts[j].isSelected())
                	graphs.add(new Graph((outputPorts[j].getLabel()), Graph.OUTPUT, Graph.STRING, outputUnits[j].getText()));
            
            }           
            
            if (!istrackinglogselected)
            {
            	istrackinglogselected = htmltracking.isSelected();            	
            	if(istrackinglogselected){
                	trackingControl.registerTrackingLog();                	
            	}
            }                     
            
            if(tl.isSelected())
            	graphs.add(new Graph("tL",Graph.STATEVARIABLE,Graph.STRING, tlUnit.getText()));
            if(tn.isSelected())
            	graphs.add(new Graph("tN",Graph.STATEVARIABLE,Graph.STRING, tnUnit.getText()));
            if(phase.isSelected())
            	graphs.add(new Graph("Phase",Graph.STATE,Graph.STRING, phaseUnit.getText()));
            if(sigma.isSelected())
            	graphs.add(new Graph("Sigma",Graph.STATE,Graph.NUMBER, sigmaUnit.getText()));     
            
            if (!isTimeViewSelected)
            {
            	isTimeViewSelected = timeView.isSelected(); 
            	if(isTimeViewSelected)
            		trackingControl.registerTimeView(graphs, modelNum, xUnit.getText(), timeIncre.getText());       
            }
        }
    }
    
    @Override
	public String toString()
    {
        return model.getName();
    }
    
    public FModel getAttachedModel()
    {
        return model;
    }
    
    public List[] getDataStorage()
    {
        return dataStorage;
    }
    
    public String[] getDataHeaders()
    {
        return dataHeaderList;
    }
    
    public void saveCurrentTrackingState(double currentTime)
    {    	
    	//time = currentTime;
    	int offset = 4;
        if (isAtomic)
        {
            FAtomicModel atomic = (FAtomicModel)model;
            dataStorage[0].add((trackPhase) ? atomic.getPhase() : null);
            dataStorage[1].add((trackSigma) ? ""+atomic.getSigma() : null);            
            
        }
        else
        {
            dataStorage[0].add(null);
            dataStorage[1].add(null);
        }
        dataStorage[2].add((trackTL) ? ""+model.getTimeOfLastEvent() : null);
        dataStorage[3].add((trackTN) ? ""+model.getTimeOfLastEvent() : null);
        
        List inputPorts = model.getInputPortNames();
        for (int i = 0; i < inputPorts.size(); i++)
        {
            if (trackInputPorts[i])
            {
                String tmp = "";
                Iterator it = model.getInputPortContents((String)inputPorts.get(i)).iterator();
                while (it.hasNext())
                    tmp+="{"+it.next()+"} ";
                dataStorage[offset++].add((tmp.length() == 0) ? null : tmp);
            }
            else
                dataStorage[offset++].add(null);
        }
        
        List outputPorts = model.getOutputPortNames();
        for (int i = 0; i < outputPorts.size(); i++)
        {
            if (trackOutputPorts[i])
            {
                String tmp = "";
                Iterator it = model.getOutputPortContents((String)outputPorts.get(i)).iterator();
                while (it.hasNext())
                    tmp+="{"+it.next()+"} ";
                dataStorage[offset++].add((tmp.length() == 0) ? null : tmp);
            }
            else
                dataStorage[offset++].add(null);
        }
    }
    
    public String getCurrentTrackingHTMLString()
    {
        String html = "";
        if (isAtomic)
        {
            FAtomicModel atomic = (FAtomicModel)model;
            if (trackPhase)
                html+="<B>Phase:</B> "+atomic.getPhase()+"<BR>";                
            
            if (trackSigma)
                html+="<B>Sigma:</B> "+atomic.getSigma()+"<BR>";                           
        }     
        
        if (trackTL)
            html+="<B>TL:</B> "+model.getTimeOfLastEvent()+"<BR>";          
        
        if (trackTN)
            html+="<B>TN:</B> "+model.getTimeOfNextEvent()+"<BR>";
        if (atLeastOneInputTracked)
        {
            html+="<B>Input Ports:</B><BR> ";
            List inputPorts = model.getInputPortNames();
            for (int i =0; i < inputPorts.size(); i++)
            {
                if (trackInputPorts[i])
                {
                    html+=inputPorts.get(i)+": ";
                    Iterator it = model.getInputPortContents((String)inputPorts.get(i)).iterator();
                    while (it.hasNext())
                        html+="{"+it.next()+"} ";
                    html+="<BR>";
                }
            }
        }
        if (atLeastOneOutputTracked)
        {
            html+="<B>Output Ports:</B> <BR>";
            List outputPorts = model.getOutputPortNames();
            for (int i =0; i < outputPorts.size(); i++)
            {
                if (trackOutputPorts[i])
                {
                    html+=outputPorts.get(i)+": ";
                    Iterator it = model.getOutputPortContents((String)outputPorts.get(i)).iterator();
                    while (it.hasNext())
                        html+="{"+it.next()+"} ";
                    html+="<BR>";
                }
            }
        }
        
        
        return html; 
    }
    
    public List getCurrentTimeViewData(double currentTime){
    	timeViewData = new ArrayList(1);
    	time = currentTime;
    	if (isAtomic)
        {
            FAtomicModel atomic = (FAtomicModel)model;
            if (trackPhase){
                e = new Event("Phase",Graph.STATE,time, atomic.getPhase()); 
                timeViewData.add(e);                
            }
            if (trackSigma){
                e = new Event("Sigma",Graph.STATE,time, String.valueOf(atomic.getSigma())); 
                timeViewData.add(e);               
            }                
        }     
        
        if (trackTL){            
            e = new Event("tL",Graph.STATEVARIABLE,time, String.valueOf(model.getTimeOfLastEvent())); 
            timeViewData.add(e);          
        }
        
        if (trackTN){
        	        	
        	e = new Event("tN",Graph.STATEVARIABLE,time, String.valueOf(model.getTimeOfNextEvent())); 
        	timeViewData.add(e);   
        }
            
        if (atLeastOneInputTracked)
        {
            List inputPorts = model.getInputPortNames();
            for (int i =0; i < inputPorts.size(); i++)
            {
                if (trackInputPorts[i])
                {
                	Iterator it = model.getInputPortContents((String)inputPorts.get(i)).iterator();
                	if(it.hasNext())
                	{
                		e = new Event(inputPorts.get(i).toString(),Graph.STATEVARIABLE, time, it.next().toString()); 
                		timeViewData.add(e);
                	}                   
                }
            }
        }
        
        if (atLeastOneOutputTracked)
        {
            
            List outputPorts = model.getOutputPortNames();
            for (int i =0; i < outputPorts.size(); i++)
            {
                if (trackOutputPorts[i])
                {
                	Iterator it = model.getOutputPortContents((String)outputPorts.get(i)).iterator();
                   	
                	if(it.hasNext())
                	{
                		e = new Event(outputPorts.get(i).toString(),Graph.STATEVARIABLE, time, it.next().toString()); 
                		timeViewData.add(e);
                	}                      
                }
            }
        } 
        
        return timeViewData;
    }
    
    public boolean isTrackingSelected(){
    	return istrackinglogselected;
    }
    
    public boolean isTimeViewSelected(){
    	return isTimeViewSelected;
    }   
}