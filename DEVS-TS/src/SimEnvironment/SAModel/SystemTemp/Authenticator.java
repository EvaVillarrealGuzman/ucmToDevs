
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class Authenticator
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public Authenticator() {
        super("Authenticator");
        construct();
    }

    public Authenticator(String name) {
        super(name);
        construct();
    }

    public void construct() {
        addInport("peip7");
        addOutport("seop8");
        addOutport("taop");
        addOutport("dtop");
        addOutport("rtop");
        addOutport("failop");
        SimEnvironment.SAModel.Library.CPXResponsibility r9 = new SimEnvironment.SAModel.Library.CPXResponsibility(111, "r9", 0.005, 0.0, 0.3, 3.6);
        add(r9);
        SimEnvironment.SAModel.Library.CPXResponsibility r8 = new SimEnvironment.SAModel.Library.CPXResponsibility(109, "r8", 0.01, 0.0, 0.4, 10.8);
        add(r8);
        initialize();
        addCoupling(this,"peip7",r8,"prip");
        addCoupling(r9,"srop",this,"seop8");
        addCoupling(r8,"srop",r9,"prip");
        addCoupling(r8,"taop",this,"taop");
        addCoupling(r8,"dtop",this,"dtop");
        addCoupling(r8,"rtop",this,"rtop");
        addCoupling(r8,"failop",this,"failop");
        addCoupling(r9,"taop",this,"taop");
        addCoupling(r9,"dtop",this,"dtop");
        addCoupling(r9,"rtop",this,"rtop");
        addCoupling(r9,"failop",this,"failop");
    }

}
