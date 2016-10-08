/**
 * Facade connection: View provides the VIEW functionalities
 * Integrated the Timeview and Simview into the original tracking environment
 * 
 * @modified Sungung Kim
 * @date 5/12/2008
 */

package view;


//Facade Connections
import facade.simulation.*;

//Standard API Imports
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import controller.ControllerInterface;
import util.WindowUtil;
import view.simView.*;

public class View extends JFrame implements ViewInterface
{
    public static JFrame PARENT_FRAME;
    private JToolBar DEVSToolBar;
    
    private int consoleTabIndex = 0;
    
    private ConsoleComponent console;
    
    //private ModelTrackingComponent tracking;
    private TrackingControl tracking;
    
    private FModelView modelView;          //Tree structured model viewer
    private FSimulatorView simulatorView;  //Controller viewer
    private FSimulatorSCView simSCView;
    private ControllerInterface controller;
    private SimView sim;
    private SplashScreen splashScreen;    
    private JMenuItem[] modelMenus;
    private JMenuItem[] controlMenus;
    
    public static JButton[] ButtonControls;
    
    //Panel component
    //viewOptions = simPane + consolePane
    //splitPane   = left + viewOptions
    private JSplitPane viewOptions, splitPane;
    private JPanel simPane, consolePane, leftPane;
    
    private String modelName, curPackages;
    private String lastModelViewed;
	private String currentDirectory;
	private String modelsPath;
    private String sourcePath;
		
	//The flags for the selection of view options
	public static boolean isSimView  = false;
	public static boolean isTracking = false;
	public static boolean isBreakout = false;
	public static JTabbedPane tabbedPane;
    
    public View(ControllerInterface controller) 
    {
        super(ViewUtils.FRAME_TITLE);
        
        //Start screen
        splashScreen = new SplashScreen();
        splashScreen.setSplashImage(ViewUtils.loadFullImage(ViewUtils.SPLASH_SCREEN_ICON));
        splashScreen.startSplashScreen();
        
        setSize(ViewUtils.FRAME_DIM);
        
        //Center of the Frame
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize(); 
        setLocation(new Point((screenDim.width  - ViewUtils.FRAME_DIM.width) / 2,
                              (screenDim.height - ViewUtils.FRAME_DIM.height) / 2));
        
        PARENT_FRAME = this;
        this.controller = controller;
               
        //Construct UI for the DevsSuite
        UIconstruct(); 	               
        
        final ControllerInterface c = controller;
        
        addWindowListener(new java.awt.event.WindowAdapter() 
        {
            @Override
			public void windowClosing(WindowEvent evt) 
            {
            	if(isSimView)
            		sim.saveModelLayout();
            	c.systemExitGesture();
            }
        });
              
        splashScreen.endSplashScreen(this);
    }
    
    
    /**
     * this method create the UI for the DEVSSuite
     */
    private void UIconstruct(){   
    	
    	/*Added Logo for DEVS-Suite*/
		this.setIconImage(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.LOGO)).getImage());
    	
    	//console panel & tracking control
    	console        = new ConsoleComponent();        
    	tracking       = new TrackingControl();
    	console.redirectOutAndErrStreams();
    	tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    	
    	//Facade Components
        modelView      = new FModelView(controller, tracking);
        sim            = new SimView();
        simulatorView  = new FSimulatorView(controller);
        simSCView      = new FSimulatorSCView();        
    	
    	//The animation panel & console panel
        simPane        = new JPanel(new BorderLayout());    //panel for the animation
        
        consolePane    = new JPanel(new BorderLayout());
        consolePane.add(console, BorderLayout.CENTER);
        
        tabbedPane.add(consolePane, consoleTabIndex);
        tabbedPane.setTitleAt(consoleTabIndex, "Console");
        
        
        
        //View panel
        viewOptions    = new JSplitPane(JSplitPane.VERTICAL_SPLIT, simPane, tabbedPane);
        viewOptions.setDividerLocation(400);
        viewOptions.setOneTouchExpandable(true);
        
        //Left panel including model tree and controller
        leftPane       = new JPanel();        
        leftPane.setLayout(new BorderLayout());
        leftPane.add(modelView);
        leftPane.add(simulatorView,BorderLayout.SOUTH);
        
        //Main structure : Left(leftPane) and Right(viewOptions)
        splitPane      = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			                       leftPane, viewOptions);
        splitPane.setDividerLocation(260);
        splitPane.setOneTouchExpandable(true);
        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);
	
        setJMenuBar(CreateMenuBar());        
        getContentPane().add(CreateToolBar(), java.awt.BorderLayout.NORTH);          
            
        console.setVisible(true);
    }
    
    /**
     * This method will create tool bar for the application
     * @return ToolBar
     */
    private JToolBar CreateToolBar(){
    	DEVSToolBar = new JToolBar();
  	   
    	//New simulation
  	   JButton button = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.NEW_MENU)));
  	   button.setToolTipText("New");
  	   DEVSToolBar.add(button);
  	   
  	   button.addActionListener(new ActionListener() {
             @Override
			public void actionPerformed(ActionEvent e) {
            	 //if no model is selected, then call LoadModel()
            	 //else reload a model
            	 if(curPackages == null && modelName == null)
            		 new LoadModel();
            	 else
            		 reloadModelAction();        
             }
         });
  	   
  	   //load a model
  	   button = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.OPEN_MENU)));
  	   button.setToolTipText("Load Model");
  	   DEVSToolBar.add(button);
  	   
  	   button.addActionListener(new ActionListener() {
             @Override
			public void actionPerformed(ActionEvent e) {
          	  new LoadModel();          
             }
         });
  	   
  	   //Separator
  	   DEVSToolBar.addSeparator();
  	   
  	   //save console log
  	   button = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.SAVE_MENU)));
  	   button.setToolTipText("Save Console Log");
  	   DEVSToolBar.add(button);
  	   
  	   button.addActionListener(new ActionListener() {
             @Override
			public void actionPerformed(ActionEvent e) {
          	   getConsoleLog();         
             }
         }); 	   
  	   
  	   //Clean console
  	   button = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.CLEAN_MENU)));
  	   button.setToolTipText("Clean Console");
  	   DEVSToolBar.add(button);
  	   
  	   button.addActionListener(new ActionListener() {
             @Override
			public void actionPerformed(ActionEvent e) {
            	 clearConsoleAction();	         
             }
         });
  	   
  	   //Console Setting
  	   button = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.SETTING_MENU)));
  	   button.setToolTipText("Console Setting");
  	   DEVSToolBar.add(button);
  	   
  	   button.addActionListener(new ActionListener() {
             @Override
			public void actionPerformed(ActionEvent e) {
          	  console.customizeComponent(PARENT_FRAME);         
             }
         });  	   
  	   
  	   //Separator
  	   DEVSToolBar.addSeparator();
  	   
  	   //About
  	   button = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.HELP_MENU)));
  	   button.setToolTipText("About");
  	   DEVSToolBar.add(button);
  	   
  	   button.addActionListener(new ActionListener() {
             @Override
			public void actionPerformed(ActionEvent e) {
            	 showAboutBox();        
             }
         });  	   	   
  	   
  	   //Separator
  	   DEVSToolBar.addSeparator();  	  
  	   
  	   DEVSToolBar.add(Box.createRigidArea(new Dimension(250,10)));
  	   
  	   creatController();
  	   
  	   DEVSToolBar.setFloatable(false);
  	   
  	   return DEVSToolBar;
    }
    
    private void creatController(){
    	
    	ButtonControls = new JButton[5];
    	
    	ButtonControls[0] = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.STEP)));
    	ButtonControls[0].setToolTipText("Step");
 	   	DEVSToolBar.add(ButtonControls[0]);
 	   	
 	   ButtonControls[0].addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e) {
        	   controller.userGesture(ControllerInterface.SIM_STEP_GESTURE,null);           
           }
       });
 	   
    	ButtonControls[1] = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.STEPN)));
    	ButtonControls[1].setToolTipText("Step(n)");
 	   	DEVSToolBar.add(ButtonControls[1]);
 	   
 	   ButtonControls[1].addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e) {
        	   String val = JOptionPane.showInputDialog(View.PARENT_FRAME,"Number of steps to iterate: ");
               if (val != null)
                   try
                   {
                       Integer i = new Integer(val);
                       controller.userGesture(ControllerInterface.SIM_STEPN_GESTURE,i);
                   }
                   catch (Exception exp){System.err.println(exp);}          
           }
       });
 	   	
    	ButtonControls[2] = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.RUN)));
    	ButtonControls[2].setToolTipText("Run");
 	   	DEVSToolBar.add(ButtonControls[2]);
   	   
 	   ButtonControls[2].addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e) {
        	   controller.userGesture(ControllerInterface.SIM_RUN_GESTURE,null);         
           }
       });
 	   	
    	ButtonControls[3] = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.PAUSE)));
    	ButtonControls[3].setToolTipText("Pause");
 	   	DEVSToolBar.add(ButtonControls[3]);
 	   
 	   ButtonControls[3].addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e) {
        	   controller.userGesture(ControllerInterface.SIM_PAUSE_GESTURE,null);          
           }
       });
 	   	
    	ButtonControls[4] = new JButton(new ImageIcon(ViewUtils.loadFullImage(ViewUtils.RSET)));
    	ButtonControls[4].setToolTipText("Reset");
 	   	DEVSToolBar.add(ButtonControls[4]);   	
 	   	
 	   ButtonControls[4].addActionListener(new ActionListener() {
           @Override
		public void actionPerformed(ActionEvent e) {
        	   String msg = "Reset this Model?\n";
               msg += "All Tracking Data Will Be Lost";
               int option = JOptionPane.showConfirmDialog(View.PARENT_FRAME,msg,
                            "Reset Model?",JOptionPane.YES_NO_OPTION);
               if (option == JOptionPane.YES_OPTION)
                   controller.userGesture(ControllerInterface.SIM_RESET_GESTURE,null);        
           }
       });
 	   	
 	   	for(int i=0; i<ButtonControls.length; i++){
 	   		ButtonControls[i].setVisible(false);
 	   	}
    	
    }
    
    /**
	 * This function creates the menubar
	 * @return menubar
	 */
    private JMenuBar CreateMenuBar(){	
		
		JMenuBar menuBar = new JMenuBar();		
        
        MenuActionListener menuListener = new MenuActionListener();

        modelMenus = new JMenuItem[7];
        modelMenus[0] = new JMenuItem("Reload Model",'R');
        modelMenus[1] = new JMenuItem("Save Tracking Log...",'T');
        modelMenus[2] = new JMenuItem("Tracking Log Settings...",'T');
        modelMenus[3] = new JMenuItem("Export to CSV...",'C');
        modelMenus[4] = new JMenuItem("Export to Encoded CSV...",'E');
        modelMenus[5] = new JMenuItem("Refresh Tracking Log",'R');
        modelMenus[6] = new JMenuItem("Save Console Log...",'S');       
        
        for (int i = 0; i < modelMenus.length-1; i++)
            modelMenus[i].setEnabled(false);
        
        controlMenus    = new JMenuItem[5];
        controlMenus[0] = new JMenuItem("Step",'S');
        controlMenus[1] = new JMenuItem("Step(n)",'N');
        controlMenus[2] = new JMenuItem("Run", 'R');
        controlMenus[3] = new JMenuItem("Pause", 'P');
        controlMenus[4] = new JMenuItem("Reset", 'E');
       
        
        for (int i = 0; i < controlMenus.length; i++)
        	controlMenus[i].setEnabled(false);
        
        // Create File Menu
        JMenu fileMenu = new JMenu("File");
        
        fileMenu.setMnemonic('F');
        
        menuBar.add(ViewUtils.makeMenu(fileMenu,
                               new Object[]
                               {new JMenuItem("Load Model...",'L'),
                                null,
                                modelMenus[6],
                                modelMenus[1],
                                null,
                                modelMenus[3],
                                modelMenus[4],
                                null,
                                new JMenuItem("Exit", 'x')
                               },menuListener));
        
        //Create Option Menu
        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic('O');
        menuBar.add(ViewUtils.makeMenu(optionsMenu,
                               new Object[] 
                               {         
                                new JMenuItem("Clear Console",'C'),                                
                                new JMenuItem("Console Settings...",'S'),
                                null,
                                modelMenus[5],
        						modelMenus[2],
                               },menuListener));
       
        //Create Control Menu
        JMenu controlsMenu = new JMenu("Controls");
        controlsMenu.setMnemonic('C');
        menuBar.add(ViewUtils.makeMenu(controlsMenu,
                               new Object[] 
                               {         
        						controlMenus[0],
        						controlMenus[1],
        						controlMenus[2],
        						controlMenus[3],
                                null,
                                controlMenus[4],
                               },menuListener));
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        menuBar.add(ViewUtils.makeMenu(helpMenu, 
                               new Object[] 
                              {new JMenuItem("About", 'A')},
                               menuListener));
        
        return menuBar;
    }
	
	 private class MenuActionListener implements ActionListener
	 {
	        @Override
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) 
	        {
	            String cmd = actionEvent.getActionCommand();
	            if (cmd.equalsIgnoreCase("Save Tracking Log..."))
	               saveTrackingReportAction();
	            else if (cmd.equalsIgnoreCase("Save Console Log..."))
	               getConsoleLog();
	            else if (cmd.equalsIgnoreCase("Export to CSV..."))
	               exportCSVAction();
	            else if (cmd.equalsIgnoreCase("Export to Encoded CSV..."))
	               exportEncodedCSVAction();
	            else if (cmd.equalsIgnoreCase("Console Settings..."))
	                console.customizeComponent(PARENT_FRAME);
	            else if (cmd.equalsIgnoreCase("Clear Console"))
	            	clearConsoleAction();	               
	            else if (cmd.equalsIgnoreCase("Tracking Log Settings..."))
	                tracking.trackingLogOption(PARENT_FRAME, cmd);
	            else if (cmd.equalsIgnoreCase("Load Model..."))
	            	new LoadModel();
	            else if (cmd.equalsIgnoreCase("Refresh Tracking Log"))
	            	tracking.trackingLogOption(PARENT_FRAME, cmd);
	            else if (cmd.equalsIgnoreCase("Step"))
	            	controller.userGesture(ControllerInterface.SIM_STEP_GESTURE,null);               
	            else if (cmd.equalsIgnoreCase("Step(n)")){
	            	String val = JOptionPane.showInputDialog(View.PARENT_FRAME,"Number of steps to iterate: ");
	                if (val != null)
	                    try
	                    {
	                        Integer i = new Integer(val);
	                        controller.userGesture(ControllerInterface.SIM_STEPN_GESTURE,i);
	                    }
	                    catch (Exception exp){System.err.println(exp);}
	            }
	            else if (cmd.equalsIgnoreCase("Run"))
	            	controller.userGesture(ControllerInterface.SIM_RUN_GESTURE,null);
	            else if (cmd.equalsIgnoreCase("Pause"))
	            	controller.userGesture(ControllerInterface.SIM_PAUSE_GESTURE,null);
	            else if (cmd.equalsIgnoreCase("Reset")){
	            	String msg = "Reset this Model?\n";
	                msg += "All Tracking Data Will Be Lost";
	                int option = JOptionPane.showConfirmDialog(View.PARENT_FRAME,msg,
	                             "Reset Model?",JOptionPane.YES_NO_OPTION);
	                if (option == JOptionPane.YES_OPTION)
	                    controller.userGesture(ControllerInterface.SIM_RESET_GESTURE,null);
	            }
	            else if (cmd.equalsIgnoreCase("About"))
	            	showAboutBox();
	            else if (cmd.equalsIgnoreCase("Exit")){
	            	sim.saveModelLayout();
	            	System.exit(0);
	            }	           
	        }           
	}	 
        
    private void showAboutBox()
    {
        splashScreen.showAsDialog(this,"About");
    }
    
    private void showSCAction()
    {
        try
        {
            if (simSCView.isIcon())
                simSCView.setIcon(false);
        }
        catch (Exception e){}
        simSCView.setVisible(true);
        simSCView.moveToFront();
    }
    
    private void clearConsoleAction()
    {
        int option = JOptionPane.showConfirmDialog(this,
        "Clear All Console Data?","Confirm Clear...",JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
            console.clearConsole();
    }
    
    /**
     * Load Model
     */
    private void loadModelAction()
    {
            String[] val   = {curPackages, modelName}; 
            controller.userGesture(ControllerInterface.LOAD_MODEL_GESTURE,val);
    }
    
    /**
     * Reload a model
     */
    private void reloadModelAction()
    {        
        int option = JOptionPane.showConfirmDialog(this,"Reload current model? (All log data will be lost)",
                                                   "Reload Model...", 
                                                   JOptionPane.OK_CANCEL_OPTION,
                                                   JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.OK_OPTION)
        {
        	String[] val   = {curPackages, modelName};
            controller.userGesture(ControllerInterface.LOAD_MODEL_GESTURE,val);
        }
        
    }
    
    private void setGridBagComponent(int x, int y, GridBagLayout gbl, GridBagConstraints gbc,
                                     JPanel panel, Component component)
    {
        gbc.gridx = x;
        gbc.gridy = y;
        gbl.setConstraints(component, gbc);
        panel.add(component);
    }

    private void exportCSVAction()
    {
        if (modelView.getSelectedModel() == null)
        {
            JOptionPane.showMessageDialog(this,
            "Cannot Export, No Model Selected.",
            "Please choose a model first...",JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setDialogTitle("Export CSV Tracking Data (.csv)");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {   
            currentDirectory = chooser.getCurrentDirectory().getAbsolutePath();
            String path = chooser.getSelectedFile().getAbsolutePath();
            String tst = path.toLowerCase();
            if (!(tst.endsWith(".csv")))
                path = path + ".csv";
            controller.userGesture(ControllerInterface.EXPORT_TO_CSV_GESTURE,path);
        }
    }
    
    private void exportEncodedCSVAction()
    {
        if (modelView.getSelectedModel() == null)
        {
            JOptionPane.showMessageDialog(this,
            "Cannot Export, No Model Selected.",
            "Please choose a model first...",JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setDialogTitle("Export Encoded CSV Tracking Data (.csv)");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {   
            currentDirectory = chooser.getCurrentDirectory().getAbsolutePath();
            String path = chooser.getSelectedFile().getAbsolutePath();
            String tst = path.toLowerCase();
            
            String[] exportPaths = new String[2];
            
            if (tst.endsWith(".htm") || tst.endsWith(".html"))
                exportPaths[0] = path;
            else
                exportPaths[0] = path + ".html";
           
            if (tst.endsWith(".csv"))
                exportPaths[1] = path;
            else
                exportPaths[1] = path + ".csv";
            
            controller.userGesture(ControllerInterface.EXPORT_TO_ENCODED_CSV_GESTURE,exportPaths);
        }
    }
    
    private void saveTrackingReportAction()
    {
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setDialogTitle("Save Tracking Log (.html)");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {   
            currentDirectory = chooser.getCurrentDirectory().getAbsolutePath();
            String path = chooser.getSelectedFile().getAbsolutePath();
            String tst = path.toLowerCase();
            if (!(tst.endsWith(".htm") || tst.endsWith(".html")))
                path = path + ".html";
            controller.userGesture(ControllerInterface.SAVE_TRACKING_LOG_GESTURE,path);
        }
    }
    
    private void saveConsoleLogAction()
    {
        JFileChooser chooser = new JFileChooser(currentDirectory);
        chooser.setDialogTitle("Save Console Log (.txt)");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {   
            currentDirectory = chooser.getCurrentDirectory().getAbsolutePath();
            String path = chooser.getSelectedFile().getAbsolutePath();
            String tst = path.toLowerCase();
            if (!tst.endsWith(".txt"))
                path = path + ".txt";
            controller.userGesture(ControllerInterface.SAVE_CONSOLE_LOG_GESTURE,path);
        }
        
    }
    
    /**
     * Add Tracking Column based on the output from the simulator
     */
    @Override
	public void addTrackingColumn(double currentTime) 
    {
    		tracking.addTracking(currentTime);
    }
    
    /**
     * Load Simulator
     */
    @Override
	public void loadSimulator(FSimulator simulator) 
    {
        simulatorView.loadSimulator(simulator);
        simSCView.loadSimulator(simulator);
        modelView.loadModel(simulator.getRootModel());
        simPane.removeAll();
        simPane.repaint();
        sim.useModelClass(simulator.getRootModel(), sourcePath);
        
        if(isSimView)
        	simPane.add(sim.getSimView());
       
        if(isTracking)
        	tracking.loadSimModel(simulator.getRootModel());
        
        for(int i=0; i<ButtonControls.length; i++){
  	   		ButtonControls[i].setVisible(true);
    	}
        for (int i = 0; i < modelMenus.length; i++)
           modelMenus[i].setEnabled(true);
        for (int i = 0; i < controlMenus.length; i++)
        	controlMenus[i].setEnabled(true);
    }
    
    @Override
	public void synchronizeView() 
    {
        modelView.synchronizeView();
        simulatorView.synchronizeView();
        simSCView.synchronizeView();
    }
    
    @Override
	public String getConsoleLog() 
    {
        return console.getTextString();
    }
    
    @Override
	public String getHTMLTrackingLog() 
    {
        return tracking.getHTML();
    }
    
    @Override
	public String[] getEncodedCSVExport() 
    {
        return tracking.getEncodedCSV(modelView.getSelectedModel());
    }
    
    @Override
	public String getCSVExport() 
    {
        return tracking.getCSV(modelView.getSelectedModel());
    }
    
    @Override
	public JPanel getConsole(){
    	return consolePane;
    }
    
    @Override
	public void clearConsole()
    {
        console.clearConsole();
    }
    
    @Override
	public void simlationControl(String gesture){
    	if(isTracking)
    		tracking.controlTimeView(gesture);
    	if(isSimView)
    		if (gesture.equals(ControllerInterface.SIM_RUN_GESTURE))
                SimView.modelView.runToOccur();
            else if (gesture.equals(ControllerInterface.SIM_STEP_GESTURE))
            	SimView.modelView.stepToBeTaken();                
    }
   
    @Override
	public SimView getSim(){
    	return sim;
    }
	
    /**
	 * this class pop up the menu to select a model, basically it is extracted from the SimView
	 * @author Sungung Kim
	 */
	private class LoadModel
	{   
		protected JFrame mainFrame;
		final protected String settingsFileName = "SimView_settings";
	    
	    protected List modelPackages;
	    protected ConfigureDialog configureDialog;

	    protected JComboBox packagesBox;
	    protected JComboBox modelsBox;	    
	    protected String modelsPackage;	    
	   
	    public LoadModel()
	    {
	    	isSimView  = false;
			isTracking = false;
			isBreakout=false;
			
	    	loadSettings();
	        constructUI();

	        // load the last model viewed from the previous program execution
	        if (curPackages != null) {
	            packagesBox.setSelectedItem(curPackages);
	            
	        }
	        if (lastModelViewed != null) {
	            modelsBox.setSelectedItem(lastModelViewed);
	        }

	        // show the app frame; remember the Swing rule that all further code
	        // that manipulates or queries Swing components must run on the
	        // event handling thread, otherwise lockups may occur
	        mainFrame.setVisible(true);	        
	    }

	    /**
	     * Constructs the UI of the main window of this application.
	     */
	    protected void constructUI()
	    {
	        // create the app frame
	        final JFrame frame = mainFrame = new JFrame("Load Model");
	        
	        frame.setResizable(false);
	        frame.setSize(300, 200);
	        Container pane = frame.getContentPane();
	        pane.setBackground(Color.white);
	        
	        JPanel main = new JPanel();
	        main.setLayout(new BorderLayout()); 
	        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	        
	        pane.add(main);
	        
	        JPanel panel = new JPanel();
	        panel.setOpaque(false);
	        panel.setLayout(new BorderLayout());
	        
	        main.add(panel);

	        JPanel center = new JPanel();
	        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
	        
	        center.add(Box.createRigidArea(new Dimension(10,10)));
	        
	        JPanel selection = new JPanel();
	        selection.setLayout(new BoxLayout(selection, BoxLayout.X_AXIS));
	        selection.add(new JLabel("Package: "));
	        
	        
	        JComboBox combo = packagesBox = new JComboBox();
	        populatePackagesBox(combo);
	        selection.add(combo);

	        // this keeps the combo-box from taking up extra height
	        // unnecessarily under JRE 1.4
	        combo.setMaximumSize(new Dimension(combo.getMaximumSize().width,
	            combo.getMinimumSize().height));

	        // when the a choice is selected from the packages combo-box
	        packagesBox.addActionListener(new ActionListener() {
	            @Override
				public void actionPerformed(ActionEvent e) {
	                // ignore the first choice when it is selected, as it is
	                // just an instructions string; also, ignore when no choice
	                // is selected
	                if (packagesBox.getSelectedIndex() <= 0) return;
	                // make the selected item the current model package name
	                curPackages = (String)packagesBox.getSelectedItem();           	
	               
	                populateModelsBox(modelsBox);
	            }
	        });
	        
	        
	        center.add(selection);
	        center.add(Box.createRigidArea(new Dimension(10,10)));

	        selection = new JPanel();
	        selection.setLayout(new BoxLayout(selection, BoxLayout.X_AXIS));
	        selection.add(Box.createRigidArea(new Dimension(15,1)));
	        selection.add(new JLabel("Model: "));
	        
	        // add the models combo-box
	        combo = modelsBox = new JComboBox();
	        selection.add(combo);

	        // this keeps the combo-box from taking up extra height
	        // unnecessarily under JRE 1.4
	        combo.setMaximumSize(new Dimension(combo.getMaximumSize().width,
	            combo.getMinimumSize().height));

	        // when the a choice is selected from the models combo-box
	        modelsBox.addActionListener(new ActionListener() {
	            @Override
				public void actionPerformed(ActionEvent e) {
	                // ignore the first choice when it is selected, as it is
	                // just an instructions string; also, ignore when no choice
	                // is selected
	                if (modelsBox.getSelectedIndex() <= 0) return;

	                modelName = (String)modelsBox.getSelectedItem();
	            }
	        });
	        
	        center.add(selection);
	      
	        selection = new JPanel(new GridLayout(1,2));
	        JCheckBox isSimViewSelected = new JCheckBox("SimView", isSimView);
	        isSimViewSelected.setHorizontalAlignment(SwingConstants.CENTER);
	        JCheckBox isTrackingSelected = new JCheckBox("Tracking", isTracking);
	        isTrackingSelected.setHorizontalAlignment(SwingConstants.CENTER);
	        
	        selection.add(isSimViewSelected);
	        selection.add(isTrackingSelected);
	        center.add(selection); 

	        JPanel selection2 = new JPanel(new GridLayout(1,1));
	        JCheckBox isBreakoutSelected = new JCheckBox("Time Views in Separate Windows");
	        isBreakoutSelected.setHorizontalAlignment(SwingConstants.CENTER);
	        selection2.add(isBreakoutSelected);
	        center.add(selection2);

	        
	        isSimViewSelected.addItemListener(
	        		new ItemListener() {
	        			@Override
						public void itemStateChanged(ItemEvent e) {
	        	            if (e.getStateChange() == ItemEvent.SELECTED)
	        	            	isSimView = true;
	        	            else
	        	            	isSimView = false;
	        			}
	        		});
	       
	        isTrackingSelected.addItemListener(
	        		new ItemListener() {
	        			@Override
						public void itemStateChanged(ItemEvent e) {
	        	            if (e.getStateChange() == ItemEvent.SELECTED)
	        	            	isTracking= true;
	        	            else
	        	            	isTracking = false;
	        	        }
	        		});
	       	
	        isBreakoutSelected.addItemListener(
	        		new ItemListener() {
	        			@Override
						public void itemStateChanged(ItemEvent e) {
	        				if (e.getStateChange() == ItemEvent.SELECTED)
	        					isBreakout=true;
	        				else
	        					isBreakout=false;
	        				
	        			}
	        		});
	       
	        selection = new JPanel();
	        selection.setLayout(new BoxLayout(selection, BoxLayout.X_AXIS));
	              
	        //add the configure button
	        JButton button = new JButton("configure");
	        button.setFont(new Font("SansSerif", Font.BOLD, 12));
	        
	        selection.add(button);
	        selection.add(Box.createRigidArea(new Dimension(60,1)));

	        // when the configure button is clicked
	        button.addActionListener(new ActionListener() {
	            @Override
				public void actionPerformed(ActionEvent e) {
	                // if the configure dialog hasn't yet been created
	                if (configureDialog == null) {
	                    // create it
	                    configureDialog = new ConfigureDialog(frame);
	                }

	                // display the configure dialog
	                configureDialog.setVisible(true);
	            }
	        });
	        
	        button = new JButton("Ok");
	        button.setFont(new Font("SansSerif", Font.BOLD, 12));
	       
	        selection.add(button);

	        // when the ok button is clicked
	        button.addActionListener(new ActionListener() {
	            @Override
				public void actionPerformed(ActionEvent e) {
	            	//if(TrackingControl.modelColumn != null)
	            		//tracking.disposalFrames();
	    	    	loadModelAction();	            	
	            	lastModelViewed = modelName;
	            	saveSettings();	            	
	                frame.dispose();
	            }
	        });
	        
	        selection.add(Box.createRigidArea(new Dimension(10,1)));
	        button = new JButton("Cancel");
	        button.setFont(new Font("SansSerif", Font.BOLD, 12));
	       
	        selection.add(button);

	        // when the ok button is clicked
	        button.addActionListener(new ActionListener() {
	            @Override
				public void actionPerformed(ActionEvent e) {
	            	frame.dispose();
	            }
	        });        
	        
	        center.add(selection);	        
	        center.add(Box.createRigidArea(new Dimension(10,5)));
	        panel.add(center, BorderLayout.CENTER);
	        
	        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize(); 
	 	   	Dimension frameDim = mainFrame.getSize();
	 		
	 	   	int xpos=(int)(screenDim.getWidth()/2 - mainFrame.getWidth()/2);
	 	   	int ypos=(int)(screenDim.getHeight()/2 - frameDim.getHeight()/2);
	 		
	 	   	mainFrame.setLocation(xpos, ypos);	
	    }
	    	   	   
	    /**
	     * Adds the names of all java class files (which are assumed to
	     * be compiled devs java models) in the models-path to
	     * the given combo-box.
	     *
	     * @param   box     The combo-box to which to add the model names.
	     */
	    protected void populateModelsBox(JComboBox box)
	    {
	    	
	        // create a filename filter (to be used below) that will
	        // match against ".class" files (and ignore inner classes)
	        final String extension = ".class";
	        FilenameFilter filter =  new FilenameFilter() {
	            @Override
				public boolean accept(File dir, String name) {
	                return name.endsWith(extension) && name.indexOf('$') == -1;
	            }
	        };
	        
	        // find all java class files (that aren't inner classes) in the
	        // models-path
	        File path = new File(modelsPath + "/" + curPackages.replace('.', '/'));
	        File[] files = path.listFiles(filter);

	        
	        // if the models-path doesn't exist
	        if (files == null) {
	            JOptionPane.showMessageDialog(mainFrame,
	                "The selected models package does not appear to be available for loading.  " +
	                "Please select another.");
	            return;
	        }

	        // sort the names of the java class files found above
	        TreeSet sortedFiles = new TreeSet(Arrays.asList(files));

	        box.removeAllItems();

	        box.addItem("Select a model");

	        // for each java class file in the models-path (we are assuming
	        // each such file to be a compiled devs java model)
	        Iterator i = sortedFiles.iterator();
	        while (i.hasNext()) {
	            // add this class file's name (minus its extension) to the box
	            String name = ((File)i.next()).getName();
	            box.addItem(name.substring(0,
	                name.length() - extension.length()));
	        }
	    }


	    /**
	     * Adds the names of all the user-specified java packages containing
	     * models to the given combo-box.
	     *
	     * @param   box     The combo-box to which to add the model names.
	     */
	    protected void populatePackagesBox(JComboBox box)
	    {
	        box.removeAllItems();

	        box.addItem("Select a package");
	      	      
	        // if the user has specified a list of model packages
	        if (modelPackages != null) {
	            // for each model package in the list
	            for (int i = 0; i < modelPackages.size(); i++) {
	                // add this package's name to the given combo-box
	                box.addItem(modelPackages.get(i));
	            }
	        }
	    }

	    /**
	     * A dialog in which the user may specify various program-wide settings.
	     */
	    protected class ConfigureDialog extends JDialog
	    {
	        /**
	         * Constructs a configure-dialog.
	         *
	         * @param   owner       The parent frame of this dialog.
	         */
	        public ConfigureDialog(Frame owner)
	        {
	            super(owner, "Configure", true);

	            setSize(400, 300);
	            WindowUtil.centerWindow(this);

	            constructUI();
	        }

	        /**
	         * Constructs the UI of this dialog.
	         */
	        protected void constructUI()
	        {
	            // have the close-window button do nothing for this dialog
	            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

	            // add the main panel
	            Container pane = getContentPane();
	            JPanel main = new JPanel();
	            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
	            main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	            pane.add(main);

	            // add the class path label
	            JLabel label = new JLabel("Path to packages of model classes (from current folder)");
	            label.setAlignmentX(0.0f);
	            main.add(label);

	            // add the class path field
	            final JTextField classPathField = new JTextField(modelsPath);
	            JTextField field = classPathField;
	            field.setAlignmentX(0.0f);
	            main.add(field);

	            // limit the height of the class path field
	            field.setMaximumSize(new Dimension(1000,
	                (int)(1.5 * getFontMetrics(field.getFont()).getHeight())));

	            main.add(Box.createRigidArea(new Dimension(0, 10)));

	            // add the source path label
	            label = new JLabel("Path to packages of model source files (from current folder)");
	            label.setAlignmentX(0.0f);
	            main.add(label);

	            // add the source path field
	            final JTextField sourcePathField = new JTextField(sourcePath);
	            field = sourcePathField;
	            field.setAlignmentX(0.0f);
	            main.add(field);

	            // limit the height of the source path field
	            field.setMaximumSize(new Dimension(1000,
	                (int)(1.5 * getFontMetrics(field.getFont()).getHeight())));

	            main.add(Box.createRigidArea(new Dimension(0, 10)));

	            // add the package names label
	            label = new JLabel("Model package names (one per line)");
	            label.setAlignmentX(0.0f);
	            main.add(label);

	            // add the package names text area
	            final JTextArea packagesArea = new JTextArea(modelsPath);
	            JTextArea area = packagesArea;
	            JScrollPane scrollPane = new JScrollPane(area);
	            scrollPane.setAlignmentX(0.0f);
	            main.add(scrollPane);

	            // for each entry in our model-packages list
	            String text = "";
	            for (int i = 0; i < modelPackages.size(); i++) {
	                // add this entry as a line to the text area's text
	                text += ((String)modelPackages.get(i)) + "\n";
	            }
	            area.setText(text);

	            // have ctrl-insert do a copy action in the text area
	            Keymap keymap = JTextComponent.addKeymap(null, area.getKeymap());
	            KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,
	                Event.CTRL_MASK);
	            keymap.addActionForKeyStroke(key, new DefaultEditorKit.CopyAction());

	            // have shift-insert do a paste action in the text area
	            key = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, Event.SHIFT_MASK);
	            keymap.addActionForKeyStroke(key,
	                new DefaultEditorKit.PasteAction());
	            area.setKeymap(keymap);

	            main.add(Box.createRigidArea(new Dimension(0, 10)));

	            JPanel panel = new JPanel();
	            panel.setOpaque(false);
	            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	            panel.setAlignmentX(0.0f);
	            main.add(panel);

	            panel.add(Box.createHorizontalGlue());

	            // add the ok button
	            JButton button = new JButton("Ok");
	            button.setAlignmentX(0.0f);
	            panel.add(button);

	            // when the ok button is clicked
	            button.addActionListener(new ActionListener() {
	                @Override
					public void actionPerformed(ActionEvent e) {
	                    // store the class path field entry
	                    modelsPath = classPathField.getText();
	                    
	                    
	                    
	                    if (modelsPath.equals("")) {
	                        modelsPath = ".";
	                    }

	                    // if there is a trailing slash on the class path entry
	                    if (modelsPath.endsWith("/")) {
	                        // remove it
	                        modelsPath = modelsPath.substring(0,
	                            modelsPath.length() - 1);
	                    }

	                    // store the source path field entry
	                    sourcePath = sourcePathField.getText();
	                    if (sourcePath.equals("")) {
	                        sourcePath = ".";
	                    }

	                    // if there is a trailing slash on the source path entry
	                    if (sourcePath.endsWith("/")) {
	                        // remove it
	                        sourcePath = sourcePath.substring(0,
	                            sourcePath.length() - 1);
	                    }

	                    // keep doing this
	                    modelPackages = new ArrayList();
	                    StringReader stringReader =
	                        new StringReader(packagesArea.getText());
	                    BufferedReader reader = new BufferedReader(stringReader);
	                    while (true) {
	                        // read the next line specified in the packages text
	                        // area
	                        String line = null;
	                        try {
	                            line = reader.readLine();
	                        } catch (IOException ex) {ex.printStackTrace(); continue;}

	                        // if there are no more lines
	                        if (line == null) break;

	                        // if this line is blank, skip it
	                        line = line.trim();
	                        if (line.equals("")) continue;

	                        // add this line's package name to our list of package
	                        // names
	                        modelPackages.add(line);
	                    }

	                    populatePackagesBox(packagesBox);
	                    
	                    saveSettings();

	                    setVisible(false);
	                }
	            });
	        }
	    }

	    

	    /**
	     * Loads this program's settings from its settings files.
	    */
	    protected void loadSettings()
	    {
	        try {
	            // read in the settings from the settings file
	            InputStream in = new FileInputStream(settingsFileName);
	            ObjectInputStream s = new ObjectInputStream(in);
	            modelsPath = (String)s.readObject();
	            modelPackages = (List)s.readObject();            
	            curPackages = (String)s.readObject();	            
	            lastModelViewed = (String)s.readObject();
	            sourcePath = (String)s.readObject();
	        } catch (Exception e) {
	            System.out.println("Couldn't read settings from file.");
	            if (modelsPath == null) modelsPath = ".";
	            if (sourcePath == null) sourcePath = ".";
	            if (modelPackages == null) modelPackages = new ArrayList();
	        }
	    }

	    /**
	     * Saves this program's settings to its settings files.
	     */
	    protected void saveSettings()
	    {
	        try {
	            // write out the current settings to the settings file
	            FileOutputStream out = new FileOutputStream(settingsFileName);
	            ObjectOutputStream s = new ObjectOutputStream(out);
	            s.writeObject(modelsPath);
	            s.writeObject(modelPackages);
	            s.writeObject(curPackages);
	            s.writeObject(lastModelViewed);
	            s.writeObject(sourcePath);
	            s.flush();
	        } catch (IOException e) {e.printStackTrace();}
	    }

	}
        
    private void writeString(String path, String stringToWrite)
    {
            try
            {
                FileWriter fw = new FileWriter(path);
                fw.write(stringToWrite);
                fw.close();
            }
            catch (Exception e)
            {
                System.err.println("An Error Occured While Writing: " + path);
                System.err.println(e);
            }
     }
    
    @Override
	public void removeExternalWindows()
    {
    	tracking.clearWindows();
    }
}
