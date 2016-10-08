
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class Encryptor
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public Encryptor() {
        super("Encryptor");
        construct();
    }

    public Encryptor(String name) {
        super(name);
        construct();
    }

    public void construct() {
        addInport("peip6");
        addOutport("seop9");
        addOutport("taop");
        addOutport("dtop");
        addOutport("rtop");
        addOutport("failop");
        SimEnvironment.SAModel.Library.CPXResponsibility r5 = new SimEnvironment.SAModel.Library.CPXResponsibility(94, "r5", 0.03, 0.0, 0.36, 57.6);
        add(r5);
        initialize();
        addCoupling(this,"peip6",r5,"prip");
        addCoupling(r5,"srop",this,"seop9");
        addCoupling(r5,"taop",this,"taop");
        addCoupling(r5,"dtop",this,"dtop");
        addCoupling(r5,"rtop",this,"rtop");
        addCoupling(r5,"failop",this,"failop");
    }

}
