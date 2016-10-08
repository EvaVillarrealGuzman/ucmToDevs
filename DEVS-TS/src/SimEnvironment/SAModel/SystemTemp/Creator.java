
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class Creator
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public Creator() {
        super("Creator");
        construct();
    }

    public Creator(String name) {
        super(name);
        construct();
    }

    public void construct() {
        addInport("peip0");
        addOutport("seop5");
        addOutport("taop");
        addOutport("dtop");
        addOutport("rtop");
        addOutport("failop");
        SimEnvironment.SAModel.Library.CPXResponsibility r2 = new SimEnvironment.SAModel.Library.CPXResponsibility(83, "r2", 0.01, 0.0, 0.4, 10.8);
        add(r2);
        SimEnvironment.SAModel.Library.CPXResponsibility r1 = new SimEnvironment.SAModel.Library.CPXResponsibility(79, "r1", 0.005, 0.0, 0.3, 3.6);
        add(r1);
        initialize();
        addCoupling(this,"peip0",r1,"prip");
        addCoupling(r2,"srop",this,"seop5");
        addCoupling(r1,"srop",r2,"prip");
        addCoupling(r2,"taop",this,"taop");
        addCoupling(r2,"dtop",this,"dtop");
        addCoupling(r2,"rtop",this,"rtop");
        addCoupling(r2,"failop",this,"failop");
        addCoupling(r1,"taop",this,"taop");
        addCoupling(r1,"dtop",this,"dtop");
        addCoupling(r1,"rtop",this,"rtop");
        addCoupling(r1,"failop",this,"failop");
    }

}
