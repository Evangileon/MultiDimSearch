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

    /**
     * Add the item just after the head, because we can't be sure that which proper pointer of item is.
     * we need to search on partial name
     * @param item to be added
     * @param partName same price, same partial name
     */
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

    /**
     * Decrement of the size of list
     * @return new size
     */
    @Override
    public int decrementSize() {
        this.size--;
        return this.size;
    }
}
