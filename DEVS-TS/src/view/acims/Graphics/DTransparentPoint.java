/* 
 * Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 
package view.acims.Graphics;

import java.awt.*;

import view.acims.Math.*;
import view.acims.diagrams.DComponent;


public class DTransparentPoint extends DComponent {
	
	protected boolean showL = true;
	protected Point labelPos = new Point(0,-5);

	public DTransparentPoint(){
		type = "Transparent Point";
	}
	
	public DTransparentPoint(Color txtColor){
		type = "Transparent Point";
		textColor = txtColor;
	}
	
	public DTransparentPoint(Color txtColor, String typ){
		type = typ;
		textColor = txtColor;
	}
	
	public DTransparentPoint(Color txtColor, String typ, String name){
		type = typ;
		textColor = txtColor;
		label = name;
	}
	
	@Override
	public void drawDComponent(Graphics2D g2D) {
		if(showL){
			g2D.setColor(textColor);
			Point lPos = Math2D.addVectors(start, labelPos);
			g2D.drawString(label,lPos.x,lPos.y);
		}
	}

	@Override
	public Point getCenter() {
		return new Point(0,0);
	}

	@Override
	public Point getClosestPointOnDComp(Point p) {
		return start;
	}

	@Override
	public Point getDimensions() {
		return new Point(1,1);
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	public Point getLabelPos(){
		return labelPos;
	}
	
	public boolean getLabelVisible(){
		return showL;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Point getPosition() {
		return start;
	}

	@Override
	public boolean isOnDComp(Point p) {
		return (Math2D.distancePointToPoint(p,start) < 2);
	}

	@Override
	public void setDimensions(Point dim) {
	}

	@Override
	public void setDCompPosition(Point nPos) {
		start = nPos;
	}

	@Override
	public void setShape(Point pos, int shape, String text) {
		start = pos;
		label = text;
	}
	
	public void setLabelPosition(Point relPosition){
		labelPos = relPosition;
	}
	
	public void setLabelVisible(boolean visible){
		showL = visible;
	}

	@Override
	public void setType(String compType) {
		type = compType;
	}
}
