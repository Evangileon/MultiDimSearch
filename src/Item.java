/**
 * Created by Jun Yu on 10/23/14.
 */
public class Item {
    long id;
    double price; // ##.##
    long[] name;

    Item[] next; // next item with same price respective to name
    Item[] prev; // prev
    Item[] head;

    public Item() {
        next = new Item[1];
        prev = new Item[1];
        head = new Item[1];
    }

    public Item(long id, double price, long[] name) {
        this.id = id;
        this.price = price;
        this.name = name;

        next = new Item[this.name.length];
        prev = new Item[this.name.length];
        head = new Item[this.name.length];
    }

    public double getPrice() {
        return price;
    }

    /**
     * The partial name belongs to which aisle
     *
     * @param partName partial name
     * @return index if found, otherwise -1
     */
    public int whichAisle(long partName) {
        for (int i = 0; i < name.length; i++) {
            if (name[i] == partName) {
                return i;
            }
        }
        return -1;
    }

    public void setNext(int aisle, Item item) {
        if (aisle < 0 || aisle >= this.name.length) {
            return;
        }

        this.next[aisle] = item;
    }

    public void setNext(long partName, Item item) {
        setNext(whichAisle(partName), item);
    }

    public void setPrev(int aisle, Item item) {
        if (aisle < 0 || aisle >= this.name.length) {
            return;
        }

        this.prev[aisle] = item;
    }

    public void setPrev(long partName, Item item) {
        setPrev(whichAisle(partName), item);
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void detachFromList(long partName) {
        int aisle = this.whichAisle(partName);
        if (aisle < 0) {
            System.out.println("Logical error");
            return;
        }

        if (this.prev[aisle] == null) {
            return; // Because of head, it is impossible
        }

        // for prev
        this.prev[aisle].setNext(partName, this.next[aisle]);
        // for next
        if (this.next[aisle] != null) {
            this.next[aisle].setPrev(partName, this.prev[aisle]);
        }

        // for size
        this.head[aisle].decrementSize();
    }

    public void detachFromAllLists() {
        for (long partName : name) {
            detachFromList(partName);
        }
    }

    public void decrementSize() {

    }
}
