/**
 * @author José Marinho
 */

package pt.isec.pd.exer15;

public class RequestToWorker
{
    protected int id;
    protected int nWorkers;
    protected long nIntervals;
    
    public RequestToWorker(int id, int nWorkers, long nIntervals)
    {
        this.id = id;
        this.nWorkers = nWorkers;
        this.nIntervals = nIntervals; 
    }

    public int getId() {
        return id;
    }

    public long getnIntervals() {
        return nIntervals;
    }

    public int getnWorkers() {
        return nWorkers;
    }
        
}