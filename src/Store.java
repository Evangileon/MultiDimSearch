import java.util.HashMap;

/**
 * Created by Jun Yu on 10/23/14.
 */
public class Store {
    HashMap<Long, Item> itemMap;
    HashMap<Long, RedBlackBST<Double, ItemListHead>> namePriceMap;

    public Store() {
        itemMap = new HashMap<Long, Item>();
        namePriceMap = new HashMap<Long, RedBlackBST<Double, ItemListHead>>();
    }

    public int insert(long id, double price, long[] name) {
        Item item = itemMap.get(id);

        if (item == null) {
            itemMap.put(id, new Item(id, price, name));
            // put int name price map
            updateNamePriceMap(item);
            return 1;
        }

        item.setPrice(price);
        item.detachFromAllLists();
        // update name price map
        updateNamePriceMap(item);

        return 0;
    }

    private void updateNamePriceMap(Item item) {
        for (long partName : item.name) {
            RedBlackBST<Double, ItemListHead> priceMap = namePriceMap.get(partName);
            if (priceMap == null) {
                priceMap = new RedBlackBST<Double, ItemListHead>();
                namePriceMap.put(partName, priceMap);
            }

            // already existed
            ItemListHead head = priceMap.get(item.price);
            if (head == null) {
                head = new ItemListHead();
                priceMap.put(item.price, head);
            }

            head.addFirst(item, partName);
        }
    }
}
