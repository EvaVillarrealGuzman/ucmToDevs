
package SimEnvironment.SAModel.Library;

import GenCol.entity;

public class Failure
    extends entity
{

    private int failureId;
    private Double downtime;

    public Failure() {
        this("failure",0, 10.0);
    }

    public Failure(String name, int failureId, Double downtime) {
        super(name);
        this.setFailureId(failureId);
        this.setDowntime(downtime);
    }

    public int getFailureId() {
        return failureId;
    }

    public void setFailureId(int pfailureId) {
        this.failureId = pfailureId;
    }

    public Double getDowntime() {
        return downtime;
    }

    public void setDowntime(Double pdowntime) {
        this.downtime = pdowntime;
    }

    public Boolean equal(entity entityToCmp) {
        Failure failureEntity = (Failure)entityToCmp;
        return this.getFailureId() == failureEntity.getFailureId();
    }

    public Boolean greaterThan(entity entityToCmp) {
        Failure failureEntity = (Failure)entityToCmp;
        return this.getFailureId() < failureEntity.getFailureId();
    }

    public void print() {
        System.out.println("Failure: "+ this.getName() + " id: " + this.getFailureId());
    }

}
