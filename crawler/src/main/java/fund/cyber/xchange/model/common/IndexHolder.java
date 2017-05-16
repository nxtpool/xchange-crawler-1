package fund.cyber.xchange.model.common;

/**
 * Auxiliary class to hold index to process collections element async one by one
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public class IndexHolder {

    private int length;
    private int index = 0;

    public void setLength(int length) {
        this.length = length;
    }

    public int getIndex() {
        return index;
    }

    public synchronized void increaseIndex() {
        index = (index < length - 1) ? index + 1 : 0;
    }
}
