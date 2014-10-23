import java.util.LinkedList;

/**
 * Created by Jun Yu on 10/22/14.
 */
public class ExtendedRedBlackBST<Key extends Comparable<Key>, Value> extends RedBlackBST<Key, Value> {

    private Node maxNode;
    private Node minNode;

    private Node recentlyAccessedNode; // really tricky

    /**
     * Use in-order traversal
     * @param root root of subtree
     * @param leftBound inclusive
     * @param rightBound inclusive
     * @param list store list, all elements stored in order
     */
    private void doGetKeysOnRange(Node root, Key leftBound, Key rightBound, LinkedList<Node> list) {
        if (root == null) {
            return;
        }

        // if root >= leftBound not holds, the range must not cover the left descendant of root
        if (root.key.compareTo(leftBound) >= 0) {
            doGetKeysOnRange(root.left, leftBound, rightBound, list);
        }

        // leftBound <= root <= rightBound, then add root to list
        if (leftBound.compareTo(root.key) <= 0 && root.key.compareTo(rightBound) <= 0) {
            list.add(root);
        }

        // if root <= rightBound not holds, the range must not cover the right descendant of root
        if (root.key.compareTo(rightBound) <= 0) {
            doGetKeysOnRange(root.right, leftBound, rightBound, list);
        }
    }

    /**
     * Get all key with leftBound <= key <= rightBound
     * @param leftBound inclusive
     * @param rightBound inclusive
     * @return list of all ordered elements that satisfy the relation
     */
    public LinkedList<Node> getKeysOnRange(Key leftBound, Key rightBound) {
        if (leftBound == null || rightBound == null) {
            return null;
        }

        LinkedList<Node> list = new LinkedList<Node>();
        if (leftBound.compareTo(rightBound) > 0) {
            return list;
        }

        doGetKeysOnRange(super.root, leftBound, rightBound, list);

        return list;
    }


    /**
     * Extend put method of base class, so that it can update the minNode and maxNode
     * @param key comparable
     * @param val value
     */
    @Override
    public void put(Key key, Value val) {
        root = put(root, key, val);
        root.color = super.BLACK;
        // assert check();
    }

    // insert the key-value pair in the subtree rooted at h
    @Override
    protected Node put(Node h, Key key, Value val) {
        if (h == null) {
            Node newNode = new Node(key, val, super.RED, 1);
            if (maxNode == null || newNode.key.compareTo(maxNode.key) > 0) {
                maxNode = newNode;
            } else if (minNode == null || newNode.key.compareTo(minNode.key) < 0) {
                minNode = newNode;
            }

            recentlyAccessedNode = newNode;
            return newNode;
        }

        int cmp = key.compareTo(h.key);
        if      (cmp < 0) h.left  = put(h.left,  key, val);
        else if (cmp > 0) h.right = put(h.right, key, val);
        else              h.val   = val; // update the value

        // fix-up any right-leaning links
        if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
        if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left)  &&  isRed(h.right))     flipColors(h);
        h.N = size(h.left) + size(h.right) + 1;

        return h;
    }

    @Override
    public void delete(Key key) {
        super.delete(key);
    }

    /**
     * @deprecated To implement fine max/min in this tree on O(1), deprecated this method
     */
    @Override
    @Deprecated
    public void deleteMax() {
        //super.deleteMax();
    }

    /**
     * @deprecated To implement fine max/min in this tree on O(1), deprecated this method
     */
    @Override
    @Deprecated
    public void deleteMin() {
        //super.deleteMin();
    }

    @Deprecated
    @Override
    protected Node deleteMax(Node h) {
        return null;
    }
}
