/**
 * Created by evangileon on 10/23/14.
 */ // BST helper node data type
class Node<Key extends Comparable<Key>, Value> {
    protected Key key;           // key
    protected Value val;         // associated data
    protected Node<Key, Value> left, right;  // links to left and right subtrees
    protected boolean color;     // color of parent link
    protected int N;             // subtree count

    public Node(Key key, Value val, boolean color, int N) {
        this.key = key;
        this.val = val;
        this.color = color;
        this.N = N;
    }

    public Node() {
    }
}
