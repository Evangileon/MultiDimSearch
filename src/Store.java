import java.util.HashMap;
import java.util.LinkedList;

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

    public double find(long id) {
        Item item = itemMap.get(id);
        if (item == null) {
            return 0;
        }
        return item.price;
    }

    public long delete(long id) {
        Item item = itemMap.remove(id);
        if (item == null) {
            return 0;
        }
        item.detachFromAllLists();

        long sum = 0;
        for (long one : item.name) {
            sum += one;
        }
        return sum;
    }

    public double findMinPrice(long n) {
        RedBlackBST<Double, ItemListHead> priceMap = namePriceMap.get(n);
        if (priceMap == null) {
            return 0;
        }

        return priceMap.getMinKey();
    }

    public double findMaxPrice(long n) {
        RedBlackBST<Double, ItemListHead> priceMap = namePriceMap.get(n);
        if (priceMap == null) {
            return 0;
        }

        return priceMap.getMaxKey();
    }

    public int findPriceRange(long n, double low, double high) {
        RedBlackBST<Double, ItemListHead> priceMap = namePriceMap.get(n);
        if (priceMap == null) {
            return 0;
        }

        LinkedList<Node<Double, ItemListHead>> nodeList = priceMap.getNodesOnRange(low, high);
        int sum = 0;
        for (Node<Double, ItemListHead> node : nodeList) {
            sum += node.val.size;
        }

        return sum;
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
