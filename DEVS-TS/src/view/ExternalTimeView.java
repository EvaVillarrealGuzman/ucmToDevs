package view;

import java.awt.BorderLayout;

import javax.swing.*;

public class ExternalTimeView implements Runnable
{
	private JFrame frame;
	private String name;
	private JScrollPane TG;
	public ExternalTimeView(String _name, JScrollPane _TG)
	{
		name=_name;
		TG=_TG;
	}
	@Override
	public void run()
	{
    	frame=new JFrame(name);
    	frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    	frame.getContentPane().add(TG, BorderLayout.CENTER);
    	frame.pack();
    	frame.setVisible(true);
	}
	public void dispose()
	{
		frame.dispose();
	}
}
