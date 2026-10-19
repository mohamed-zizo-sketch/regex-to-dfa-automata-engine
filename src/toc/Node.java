
package toc;

public class Node {
    String data;
    Node next;
    Node left;
    Node right;
    public Node (String x){
        data = x;
        next = null;
        this.left = this.right = null;
    }
}
