import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Jun Yu on 10/23/14.
 */
public class Store {
    // for search by id efficiently
    HashMap<Long, Item> itemMap;
    // two level structure, first is to store all items with same partial name,
    // key is partial name, value is a RBTree whose key is price, and value is
    // internal doubly linked list of items.
    HashMap<Long, RedBlackBST<Long, ItemListHead>> namePriceMap;
    // for search id range
    RedBlackBST<Long, Item> itemTree;

    public Store() {
        itemMap = new HashMap<Long, Item>();
        namePriceMap = new HashMap<Long, RedBlackBST<Long, ItemListHead>>();
        itemTree = new RedBlackBST<Long, Item>();
        priceFormat.setRoundingMode(RoundingMode.DOWN);
        pricePrecision.setRoundingMode(RoundingMode.HALF_UP);
    }

    /**
     * Insert the item into store
     * @param id long, unique, non negative
     * @param price equals the price times 100
     * @param name long array, length determined by input
     * @return 1 if new, otherwise 0
     */
    public int insert(long id, long price, long[] name) {
        Item item = itemMap.get(id);

        if (item == null) {
            item = new Item(id, price, name);
            itemMap.put(id, item);
            // put int name price map
            updateNamePriceMap(item);
            itemTree.put(id, item);
            return 1;
        }

        if (name != null) {
            // if already existed and need to replace name
            delete(id);
            insert(id, price, name);
            return 0;
        }

        // no need to replace name, just update price
        long oldPrice = item.price;
        item.setPrice(price);
        // update name price map
        clearAndUpdateNamePriceMap(item, oldPrice, false);

        return 0;
    }

    /**
     * Insert item with string represented price. Say, "123.45" to 12345L
     * @param id unique, non negative
     * @param priceStr string represented price, must has a dot, and two decimals after dot
     * @param name name array
     * @return 1 if new, otherwise 0
     */
    public int insert(long id, String priceStr, long[] name) {
        long price = Item.priceStrToLong(priceStr);
        return insert(id, price, name);
    }

    /**
     * Find an item with specified id
     * @param id unique, non negative
     * @return the long represented price if found, 0 otherwise
     */
    public long find(long id) {
        Item item = itemMap.get(id);
        if (item == null) {
            return 0;
        }
        return item.price;
    }

    /**
     * Delete the item with specified id
     * @param id unique, non negative
     * @return the sum of name array if found, 0 otherwise
     */
    public long delete(long id) {
        Item item = itemMap.remove(id);
        if (item == null) {
            return 0;
        }

        long oldPrice = item.price;
        // because I have a cross two references, from name to price to node
        // and node to node with same partial name, same price
        clearAndUpdateNamePriceMap(item, oldPrice, true);

        itemTree.delete(id);

        long sum = 0;
        for (long one : item.name) {
            sum += one;
        }
        return sum;
    }

    /**
     * Find the item with maximum price that has the same partial name with given
     * @param n given partial name
     * @return the price of item if found, 0 not exists
     */
    public long findMinPrice(long n) {
        RedBlackBST<Long, ItemListHead> priceMap = namePriceMap.get(n);
        if (priceMap == null) {
            return 0;
        }

        // log n, O(1) try later
        Long result = priceMap.min();
        if (result == null) {
            return 0;
        }
        return result;
    }

    /**
     * Find the item with minimum price that has the same partial name with given
     * @param n given partial name
     * @return the price of item if found, 0 not exists
     */
    public long findMaxPrice(long n) {
        RedBlackBST<Long, ItemListHead> priceMap = namePriceMap.get(n);
        if (priceMap == null) {
            return 0;
        }

        Long result = priceMap.max();
        if (result == null) {
            return 0;
        }
        return result;
    }

    /**
     * Find all items with given partial name whose price on range [low, high]
     * @param n with given partial name
     * @param low lower bound, inclusive
     * @param high upper bound, inclusive
     * @return the number of items satisfy all conditions
     */
    public int findPriceRange(long n, long low, long high) {
        RedBlackBST<Long, ItemListHead> priceMap = namePriceMap.get(n);
        if (priceMap == null) {
            return 0;
        }

        LinkedList<Node<Long, ItemListHead>> nodeList = priceMap.getNodesOnRange(low, high);
        int sum = 0;
        for (Node<Long, ItemListHead> node : nodeList) {
            sum += node.val.size;
        }

        return sum;
    }

    DecimalFormat priceFormat = new DecimalFormat("##.##");
    DecimalFormat pricePrecision = new DecimalFormat("##.##");

    /**
     * Increase the price of every product, whose id is in the range [l,h], by r%
     * @param l lower bound, inclusive
     * @param h upper bound, inclusive
     * @param r increase rate times 100
     * @return the sum of the net increases of the prices
     */
    public long priceHike(long l, long h, int r) {
        if (r <= 0 || r > 100) {
            return 0;
        }

        LinkedList<Node<Long, Item>> itemsOnRange = itemTree.getNodesOnRange(l, h);
        long increase = 0;

        for (Node<Long, Item> node : itemsOnRange) {
            Item item = node.val;
            long oldPrice = item.price;

            long incre = (item.price * r) / 100;
            item.price += incre;
            //item.detachFromAllLists();
            clearAndUpdateNamePriceMap(item, oldPrice, false);

            increase += incre;
        }

        return increase;
    }

    /**
     * Very important helper function.
     * 1. Because inside the item, there are next and prev arrays of item pointer, that pointer to other items
     * with same partial name and price. The length of array equals to the length of name.
     * If update operation(insert, delete, priceHike) occurs, the item need to attach to or detach from other
     * 2. Item also stored in name price map. In the second level of namePriceMap, if one list store in RBTree
     * is empty, we need to remove the list. If the RBTree itself is empty, we need to remove it from
     * first level Hash Map
     * @param item item just be updated
     * @param oldPrice record the old price to find the list to detach
     * @param isDelete indicator, if false, need to update. Delete is easier than update
     */
    private void clearAndUpdateNamePriceMap(Item item, long oldPrice, boolean isDelete) {
        int[] sizes = new int[item.name.length];
        // fora each partial name
        for (int i = 0; i < item.name.length; i++) {
            long pName = item.name[i];
            // detach from the internal list in item
            int size = item.detachFromList(pName);
            // if the length of list is zero, that means we need to remove list head
            // from second level structure of namePriceMap
            sizes[i] = size;
        }

        // if record size of item list is zero, then remove them from second level
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] == 0) {
                long pName = item.name[i];
                RedBlackBST<Long, ItemListHead> priceMap = namePriceMap.get(pName);
                // remove from second RBTree
                priceMap.delete(oldPrice);

                // remove spots from top hash map
                if (priceMap.isEmpty()) {
                    namePriceMap.remove(priceMap);
                }
            }
        }

        // update name price map
        if (!isDelete) {
            updateNamePriceMap(item);
        }
    }


    /**
     * Helper function for clearAndUpdateNamePriceMap
     * Used to find proper item list to attach
     * @param item already detached from item doubly linked list
     */
    private void updateNamePriceMap(Item item) {
        for (long partName : item.name) {
            RedBlackBST<Long, ItemListHead> priceMap = namePriceMap.get(partName);

            if (priceMap == null) {
                priceMap = new RedBlackBST<Long, ItemListHead>();
                namePriceMap.put(partName, priceMap);
            }


            // already existed
            ItemListHead head = priceMap.get(item.price);
            if (head == null) {
                head = new ItemListHead();
                priceMap.put(item.price, head);
            }

            // add this item after head, constant time
            head.addFirst(item, partName);
        }
    }

    public static void main(String[] args) {
        BufferedReader reader;

        try {
            if (args.length > 0) {
                reader = new BufferedReader(new FileReader(args[0]));
            } else {
                reader = new BufferedReader(new InputStreamReader(System.in));
            }

            double output = 0;

            Store store = new Store();

            String line;
            DecimalFormat outputFormat = new DecimalFormat("##.##");
            outputFormat.setRoundingMode(RoundingMode.HALF_UP);

            while ((line = reader.readLine()) != null && !line.equals("")) {
                if (line.startsWith("#")) {
                    continue;
                }

                line = line.trim();
                String[] params = line.split("\\s+");
                if (params.length < 1) {
                    System.out.println("Error in data");
                    System.exit(0);
                }

                String cmd = params[0];

                if (cmd.equals("Insert")) {
                    int result;
                    long id = Long.valueOf(params[1]);
                    //double price = Double.valueOf(params[2]);
                    String priceStr = params[2];
                    int nameLength = params.length - 4;

                    if (nameLength == 0) {
                        // update price
                        result = store.insert(id, priceStr, null);
                    } else {
                        long[] name = new long[nameLength];
                        for (int i = 0; i < nameLength; i++) {
                            name[i] = Long.valueOf(params[i + 3]);
                        }
                        result = store.insert(id, priceStr, name);
                    }

                    output += result;
//                    System.out.println(line);
//                    System.out.println("# " + result);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("Find")) {
                    long result;
                    long id = Long.valueOf(params[1]);
                    result = store.find(id);
                    double temp = Double.valueOf(Item.priceLongToStr(result));
                    output += temp;
//                    System.out.println(line);
//                    System.out.println("# " + temp);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("Delete")) {
                    long result;
                    long id = Long.valueOf(params[1]);
                    result = store.delete(id);

                    output += result;
//                    System.out.println(line);
//                    System.out.println("# " + result);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("FindMinPrice")) {
                    long result;
                    long partName = Long.valueOf(params[1]);
                    result = store.findMinPrice(partName);

                    double temp = Double.valueOf(Item.priceLongToStr(result));
                    output += temp;
//                    System.out.println(line);
//                    System.out.println("# " + temp);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("FindMaxPrice")) {
                    long result;
                    long partName = Long.valueOf(params[1]);
                    result = store.findMaxPrice(partName);

                    double temp = Double.valueOf(Item.priceLongToStr(result));
                    output += temp;
//                    System.out.println(line);
//                    System.out.println("# " + temp);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("FindPriceRange")) {
                    int result;
                    long partName = Long.valueOf(params[1]);
                    long low = Item.priceStrToLong(params[2]);
                    long high = Item.priceStrToLong(params[3]);

                    result = store.findPriceRange(partName, low, high);

                    output += result;
//                    System.out.println(line);
//                    System.out.println("# " + result);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("PriceHike")) {
                    long result;
                    long l = Long.valueOf(params[1]);
                    long h = Long.valueOf(params[2]);
                    int r = Integer.valueOf(params[3]);

                    result = store.priceHike(l, h, r);

                    double temp = Double.valueOf(Item.priceLongToStr(result));
                    output += temp;
//                    System.out.println(line);
//                    System.out.println("# " + temp);
                    //output = Double.valueOf(outputFormat.format(output));
                }
            }


            output = Double.valueOf(outputFormat.format(output));
            System.out.println(Double.toString(output));

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
