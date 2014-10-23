/**
 * Created by evangileon on 10/23/14.
 */
public class ItemListHead extends Item {

    int size;
    Item next;
    Item prev;

    public ItemListHead() {
        super();
        size = 0;
    }

    public void addFirst(Item item, long partName) {

        int aisle = item.whichAisle(partName);

        if (aisle < 0) {
            return;
        }

        // for the old next
        if (this.next != null) {
            int nextAisle = this.next.whichAisle(partName);
            if (nextAisle < 0) {
                System.out.println("Logical error");
                System.exit(0);
            }

            this.next.prev[nextAisle] = item;
        }

        // for the item
        item.next[aisle] = this.next;
        item.prev[aisle] = this;

        // for head
        this.next = item;

        // head of list
        item.head[aisle] = this;

        // size
        size++;
    }

    @Override
    public void setNext(int aisle, Item item) {
        this.next = item;
    }

    @Override
    public void setNext(long partName, Item item) {
        this.next = item;
    }

    @Override
    public void setPrev(int aisle, Item item) {
        this.prev = item;
    }

    @Override
    public void setPrev(long partName, Item item) {
        this.prev = item;
    }

    @Override
    public void decrementSize() {
        this.size--;
    }
}
