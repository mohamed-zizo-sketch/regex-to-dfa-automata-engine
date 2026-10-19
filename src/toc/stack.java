package toc;

/**
 * Custom LIFO Stack implementation backed by a singly linked list.
 */
public class stack {
    Node top;
    int size;

    public stack() {
        this.top = null;
        this.size = 0;
    }

    public void push(String x) {
        Node n = new Node(x);
        n.next = top;
        top = n;
        size++;
    }

    public String pop() {
        if (isEmpty()) {
            return null;
        }
        String x = top.data;
        top = top.next;
        size--;
        return x;
    }

    public boolean isEmpty() {
        return top == null;
    }

    // Retained for backward-compatibility
    public boolean IsEmpty() {
        return isEmpty();
    }

    public String checkTop() {
        return (top != null) ? top.data : null;
    }

    public String peek() {
        return checkTop();
    }

    public int size() {
        return size;
    }
}
