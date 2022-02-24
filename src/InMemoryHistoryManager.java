import Data.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private HandMadeLinkedList<Task> historyList = new HandMadeLinkedList<>();


    @Override
    public void add(Task task) {
        historyList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        historyList.removeDuplicates(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }


    class HandMadeLinkedList<T extends Task> {
        public Node<T> head;
        public Node<T> tail;
        private int size;
        Map<Integer, Node<T>> nodeHashMap;

        HandMadeLinkedList() {
            this.size = 0;
            this.nodeHashMap = new HashMap<>();
        }

        void linkLast(T task) {
            Node<T> oldTail = tail;
            Node<T> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.setNext(newNode);
            size++;

            if (nodeHashMap.containsKey(task.getItemID())) {
                remove(task.getItemID());
                size--;
            }
            nodeHashMap.put(task.getItemID(), newNode);
        }

        List<Task> getTasks() {
            List<Task> array = new ArrayList<>();
            Node<T> nextNode = head;

            while (nextNode != null) {
                array.add(nextNode.getData());
                nextNode = nextNode.getNext();
            }
            return array;
        }

        private void removeNode(Node<T> node) {
            Node<T> next = node.getNext();
            Node<T> prev = node.getPrev();
            if (!(prev == null) && !(next == null)) { //общий случай
                next.setPrev(prev);
                prev.setNext(next);
            } else if (prev == null && !(next == null)) { // если блок head
                next.setPrev(null);
                head = next;
            } else if (!(node.getPrev() == null) && node.getNext() == null) { // если блок tail
                prev.setNext(null);
                tail = prev;
            } else { //если блок единственный
                head = null;
                tail = null;
            }
        }

        void removeDuplicates(int id) {
            removeNode(nodeHashMap.get(id));
            nodeHashMap.remove(id);
        }
    }
}
