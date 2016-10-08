
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class Recorder
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public Recorder() {
        super("Recorder");
        construct();
    }

    public Recorder(String name) {
        super(name);
        construct();
    }

    public void construct() {
        addInport("peip8");
        addOutport("seop0");
        addOutport("taop");
        addOutport("dtop");
        addOutport("rtop");
        addOutport("failop");
        SimEnvironment.SAModel.Library.CPXResponsibility r11 = new SimEnvironment.SAModel.Library.CPXResponsibility(118, "r11", 0.01, 0.0, 0.4, 10.8);
        add(r11);
        SimEnvironment.SAModel.Library.CPXResponsibility r10 = new SimEnvironment.SAModel.Library.CPXResponsibility(116, "r10", 0.005, 0.0, 0.3, 3.6);
        add(r10);
        initialize();
        addCoupling(this,"peip8",r10,"prip");
        addCoupling(r11,"srop",this,"seop0");
        addCoupling(r10,"srop",r11,"prip");
        addCoupling(r10,"taop",this,"taop");
        addCoupling(r10,"dtop",this,"dtop");
        addCoupling(r10,"rtop",this,"rtop");
        addCoupling(r10,"failop",this,"failop");
        addCoupling(r11,"taop",this,"taop");
        addCoupling(r11,"dtop",this,"dtop");
        addCoupling(r11,"rtop",this,"rtop");
        addCoupling(r11,"failop",this,"failop");
    }

}
