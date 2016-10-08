
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class Client
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public Client() {
        super("Client");
        construct();
    }

    public Client(String name) {
        super(name);
        construct();
    }

    public void construct() {
        addInport("peip9");
        addOutport("seop0");
        addOutport("taop");
        addOutport("dtop");
        addOutport("rtop");
        addOutport("failop");
        ViewableDigraph decryptor = new Decryptor();
        add(decryptor);
        ViewableDigraph authenticator = new Authenticator();
        add(authenticator);
        ViewableDigraph recorder = new Recorder();
        add(recorder);
        initialize();
        addCoupling(this,"peip9",decryptor, "peip9");
        addCoupling(recorder,"seop0" ,this,"seop0");
        addCoupling(decryptor,"seop7",authenticator,"peip7");
        addCoupling(authenticator,"seop8",recorder,"peip8");
        addCoupling(decryptor,"taop",this,"taop");
        addCoupling(decryptor,"dtop",this,"dtop");
        addCoupling(decryptor,"rtop",this,"rtop");
        addCoupling(decryptor,"failop",this,"failop");
        addCoupling(recorder,"taop",this,"taop");
        addCoupling(recorder,"dtop",this,"dtop");
        addCoupling(recorder,"rtop",this,"rtop");
        addCoupling(recorder,"failop",this,"failop");
        addCoupling(authenticator,"taop",this,"taop");
        addCoupling(authenticator,"dtop",this,"dtop");
        addCoupling(authenticator,"rtop",this,"rtop");
        addCoupling(authenticator,"failop",this,"failop");
    }

}
