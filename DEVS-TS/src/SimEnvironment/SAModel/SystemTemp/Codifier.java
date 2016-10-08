
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class Codifier
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public Codifier() {
        super("Codifier");
        construct();
    }

    public Codifier(String name) {
        super(name);
        construct();
    }

    public void construct() {
        addInport("peip5");
        addOutport("seop6");
        addOutport("taop");
        addOutport("dtop");
        addOutport("rtop");
        addOutport("failop");
        SimEnvironment.SAModel.Library.CPXResponsibility r4 = new SimEnvironment.SAModel.Library.CPXResponsibility(89, "r4", 0.008, 0.0, 0.4, 3.6);
        add(r4);
        SimEnvironment.SAModel.Library.CPXResponsibility r3 = new SimEnvironment.SAModel.Library.CPXResponsibility(87, "r3", 0.015, 0.0, 0.36, 28.8);
        add(r3);
        initialize();
        addCoupling(this,"peip5",r3,"prip");
        addCoupling(r4,"srop",this,"seop6");
        addCoupling(r3,"srop",r4,"prip");
        addCoupling(r3,"taop",this,"taop");
        addCoupling(r3,"dtop",this,"dtop");
        addCoupling(r3,"rtop",this,"rtop");
        addCoupling(r3,"failop",this,"failop");
        addCoupling(r4,"taop",this,"taop");
        addCoupling(r4,"dtop",this,"dtop");
        addCoupling(r4,"rtop",this,"rtop");
        addCoupling(r4,"failop",this,"failop");
    }

}
