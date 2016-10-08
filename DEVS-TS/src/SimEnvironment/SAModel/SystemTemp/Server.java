
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class Server
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public Server() {
        super("Server");
        construct();
    }

    public Server(String name) {
        super(name);
        construct();
    }

    public void construct() {
        addInport("peip0");
        addOutport("seop9");
        addOutport("taop");
        addOutport("dtop");
        addOutport("rtop");
        addOutport("failop");
        ViewableDigraph creator = new Creator();
        add(creator);
        ViewableDigraph codifier = new Codifier();
        add(codifier);
        ViewableDigraph encryptor = new Encryptor();
        add(encryptor);
        initialize();
        addCoupling(this,"peip0",creator, "peip0");
        addCoupling(encryptor,"seop9" ,this,"seop9");
        addCoupling(creator,"seop5",codifier,"peip5");
        addCoupling(codifier,"seop6",encryptor,"peip6");
        addCoupling(encryptor,"taop",this,"taop");
        addCoupling(encryptor,"dtop",this,"dtop");
        addCoupling(encryptor,"rtop",this,"rtop");
        addCoupling(encryptor,"failop",this,"failop");
        addCoupling(creator,"taop",this,"taop");
        addCoupling(creator,"dtop",this,"dtop");
        addCoupling(creator,"rtop",this,"rtop");
        addCoupling(creator,"failop",this,"failop");
        addCoupling(codifier,"taop",this,"taop");
        addCoupling(codifier,"dtop",this,"dtop");
        addCoupling(codifier,"rtop",this,"rtop");
        addCoupling(codifier,"failop",this,"failop");
    }

}
