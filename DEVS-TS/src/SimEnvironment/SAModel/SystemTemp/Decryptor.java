
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class Decryptor
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public Decryptor() {
        super("Decryptor");
        construct();
    }

    public Decryptor(String name) {
        super(name);
        construct();
    }

    public void construct() {
        addInport("peip9");
        addOutport("seop7");
        addOutport("taop");
        addOutport("dtop");
        addOutport("rtop");
        addOutport("failop");
        SimEnvironment.SAModel.Library.CPXResponsibility r7 = new SimEnvironment.SAModel.Library.CPXResponsibility(105, "r7", 0.02, 0.0, 0.4, 28.8);
        add(r7);
        SimEnvironment.SAModel.Library.CPXResponsibility r6 = new SimEnvironment.SAModel.Library.CPXResponsibility(96, "r6", 0.005, 0.0, 0.3, 3.6);
        add(r6);
        initialize();
        addCoupling(this,"peip9",r6,"prip");
        addCoupling(r7,"srop",this,"seop7");
        addCoupling(r6,"srop",r7,"prip");
        addCoupling(r6,"taop",this,"taop");
        addCoupling(r6,"dtop",this,"dtop");
        addCoupling(r6,"rtop",this,"rtop");
        addCoupling(r6,"failop",this,"failop");
        addCoupling(r7,"taop",this,"taop");
        addCoupling(r7,"dtop",this,"dtop");
        addCoupling(r7,"rtop",this,"rtop");
        addCoupling(r7,"failop",this,"failop");
    }

}
