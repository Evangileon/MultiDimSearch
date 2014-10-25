/**
 * Created by Jun Yu on 10/23/14.
 */
public class Item {
    long id;
    long price; // ##.##
    long[] name;

    Item[] next; // next item with same price respective to name
    Item[] prev; // prev
    Item[] head;

    public Item() {
        next = new Item[1];
        prev = new Item[1];
        head = new Item[1];
    }

    public Item(long id, long price, long[] name) {
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

    public String getPriceStr() {
        return priceLongToStr(this.price);
    }

    public static String priceLongToStr(long price) {
        String str = Long.toString(price);
        if (price < 100) {
            // only pennies
            str = "0." + str;
            return str;
        }

        StringBuffer buffer = new StringBuffer(str);
        buffer.insert(str.length() - 2, ".");
        return buffer.toString();
    }

    public static long priceStrToLong(String priceStr) {
        String[] priceParam = priceStr.split("\\.");
        if (priceParam.length < 2) {
            return 0;
        }

        if (priceParam[1].length() < 2) {
            priceParam[1] = priceParam[1] + "0";
        }
        long result = Long.valueOf(priceParam[0]) * 100;
        result += Long.valueOf(priceParam[1]);

        return result;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int detachFromList(long partName) {
        int aisle = this.whichAisle(partName);
        if (aisle < 0) {
            System.out.println("Logical error");
            return 0;
        }

        if (this.prev[aisle] == null) {
            return 0; // Because of head, it is impossible
        }

        // for prev
        this.prev[aisle].setNext(partName, this.next[aisle]);
        // for next
        if (this.next[aisle] != null) {
            this.next[aisle].setPrev(partName, this.prev[aisle]);
        }

        // for size
        return this.head[aisle].decrementSize();
    }
//
//    @Deprecated
//    public void detachFromAllLists() {
//        for (long partName : name) {
//            detachFromList(partName);
//        }
//    }

    public int decrementSize() {
        return 0;
    }

    public static void main(String[] args) {
        Item item = new Item();
        item.price = 67;
        System.out.println(item.getPriceStr());
        System.out.println(Item.priceLongToStr(34));
        System.out.println(Item.priceStrToLong("0.12"));
    }
}
