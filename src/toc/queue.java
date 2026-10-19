package toc;

/**
 * Custom FIFO Queue implementation backed by a singly linked list.
 */
public class queue {
    Node front;
    Node rear;
    int size;

    public queue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public void enqueue(String x) {
        Node n = new Node(x);
        if (rear == null) {
            front = rear = n;
        } else {
            rear.next = n;
            rear = n;
        }
        size++;
    }

    public String dequeue() {
        if (front == null) {
            return null;
        }
        Node n = front;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return n.data;
    }

    public String checkFront() {
        return (front != null) ? front.data : null;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    public void printQueue() {
        Node temp = front;
        System.out.print("[");
        while (temp != null) {
            System.out.print(temp.data);
            if (temp.next != null) {
                System.out.print(", ");
            }
            temp = temp.next;
        }
        System.out.println("]");
    }

    public String toExpressionString() {
        Node temp = front;
        StringBuilder sb = new StringBuilder();
        while (temp != null) {
            sb.append(temp.data).append(" ");
            temp = temp.next;
        }
        return sb.toString().trim();
    }

    public queue copy() {
        queue cloned = new queue();
        Node temp = front;
        while (temp != null) {
            cloned.enqueue(temp.data);
            temp = temp.next;
        }
        return cloned;
    }
}
