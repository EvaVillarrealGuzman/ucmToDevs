
package SimEnvironment.SAModel.SystemTemp;

import java.util.ArrayList;
import view.modeling.ViewableDigraph;

public class SAViewDEVS
    extends ViewableDigraph
{

    private ArrayList<Double> arrayListDouble;

    public SAViewDEVS() {
        super("LMSystem - SA View");
        saViewConstruct();
    }

    public SAViewDEVS(String name) {
        super(name);
        saViewConstruct();
    }

    public void saViewConstruct() {
        addInport("erip");
        addOutport("esop");
        addOutport("rtaop");
        addOutport("rdtop");
        addOutport("rrtop");
        addOutport("rfailop");
        ViewableDigraph server = new Server();
        add(server);
        ViewableDigraph client = new Client();
        add(client);
        initialize();
        addCoupling(this,"erip",server, "peip0");
        addCoupling(client,"seop0" ,this,"esop");
        addCoupling(server,"seop9",client,"peip9");
        addCoupling(server,"taop",this,"rtaop");
        addCoupling(server,"dtop",this,"rdtop");
        addCoupling(server,"rtop",this,"rrtop");
        addCoupling(server,"failop",this,"rfailop");
        addCoupling(client,"taop",this,"rtaop");
        addCoupling(client,"dtop",this,"rdtop");
        addCoupling(client,"rtop",this,"rrtop");
        addCoupling(client,"failop",this,"rfailop");
    }

}
