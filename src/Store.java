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
    HashMap<Long, Item> itemMap;
    HashMap<Long, RedBlackBST<Long, ItemListHead>> namePriceMap;
    RedBlackBST<Long, Item> itemTree;

    public Store() {
        itemMap = new HashMap<Long, Item>();
        namePriceMap = new HashMap<Long, RedBlackBST<Long, ItemListHead>>();
        itemTree = new RedBlackBST<Long, Item>();
        priceFormat.setRoundingMode(RoundingMode.DOWN);
        pricePrecision.setRoundingMode(RoundingMode.HALF_UP);
    }

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
            delete(id);
            insert(id, price, name);
            return 0;
        }

        long oldPrice = item.price;
        item.setPrice(price);
        //item.detachFromAllLists();

        clearAndUpdateNamePriceMap(item, oldPrice, false);
        /*int[] sizes = new int[item.name.length];
        for (int i = 0; i < item.name.length; i++) {
            long pName = item.name[i];
            int size = item.detachFromList(pName);
            sizes[i] = size;
        }
        // update name price map
        updateNamePriceMap(item);

        for (int i = 0; i < sizes.length; i++) {
            long pName = item.name[i];
            namePriceMap.get(pName).delete(item.price);
        }*/


        return 0;
    }

    public int insert(long id, String priceStr, long[] name) {

        long price = Item.priceStrToLong(priceStr);

        return insert(id, price, name);
    }

    public long find(long id) {
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
        //item.detachFromAllLists();
        long oldPrice = item.price;

        clearAndUpdateNamePriceMap(item, oldPrice, true);

        itemTree.delete(id);

        long sum = 0;
        for (long one : item.name) {
            sum += one;
        }
        return sum;
    }

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

    public long priceHike(long l, long h, int r) {
        if (r <= 0 || r > 100) {
            return 0;
        }

        LinkedList<Node<Long, Item>> itemsOnRange = itemTree.getNodesOnRange(l, h);
        long increase = 0;

        for (Node<Long, Item> node : itemsOnRange) {
            Item item = node.val;
            long oldPrice = item.price;

            //long incre = Double.valueOf(priceFormat.format(ratio * item.price));
            long incre = (item.price * r) / 100;
            item.price += incre;
            //item.detachFromAllLists();
            clearAndUpdateNamePriceMap(item, oldPrice, false);

            increase += incre;
        }

        return increase;
    }

    private void clearAndUpdateNamePriceMap(Item item, long oldPrice, boolean isDelete) {
        int[] sizes = new int[item.name.length];
        for (int i = 0; i < item.name.length; i++) {
            long pName = item.name[i];
            int size = item.detachFromList(pName);
            sizes[i] = size;
        }


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
                    System.out.println(line);
                    System.out.println("# " + result);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("Find")) {
                    long result;
                    long id = Long.valueOf(params[1]);
                    result = store.find(id);
                    double temp = Double.valueOf(Item.priceLongToStr(result));
                    output += temp;
                    System.out.println(line);
                    System.out.println("# " + temp);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("Delete")) {
                    long result;
                    long id = Long.valueOf(params[1]);
                    result = store.delete(id);

                    output += result;
                    System.out.println(line);
                    System.out.println("# " + result);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("FindMinPrice")) {
                    long result;
                    long partName = Long.valueOf(params[1]);
                    result = store.findMinPrice(partName);

                    double temp = Double.valueOf(Item.priceLongToStr(result));
                    output += temp;
                    System.out.println(line);
                    System.out.println("# " + temp);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("FindMaxPrice")) {
                    long result;
                    long partName = Long.valueOf(params[1]);
                    result = store.findMaxPrice(partName);

                    double temp = Double.valueOf(Item.priceLongToStr(result));
                    output += temp;
                    System.out.println(line);
                    System.out.println("# " + temp);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("FindPriceRange")) {
                    int result;
                    long partName = Long.valueOf(params[1]);
                    long low = Item.priceStrToLong(params[2]);
                    long high = Item.priceStrToLong(params[3]);

                    result = store.findPriceRange(partName, low, high);

                    output += result;
                    System.out.println(line);
                    System.out.println("# " + result);
                    //output = Double.valueOf(outputFormat.format(output));

                } else if (cmd.equals("PriceHike")) {
                    long result;
                    long l = Long.valueOf(params[1]);
                    long h = Long.valueOf(params[2]);
                    int r = Integer.valueOf(params[3]);

                    result = store.priceHike(l, h, r);

                    double temp = Double.valueOf(Item.priceLongToStr(result));
                    output += temp;
                    System.out.println(line);
                    System.out.println("# " + temp);
                    //output = Double.valueOf(outputFormat.format(output));
                }
            }


            output = Double.valueOf(outputFormat.format(output));
            System.out.println(output);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
