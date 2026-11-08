package hotel.structures;

import hotel.people.Guest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NameTrie {
    private Node root = new Node();

    private static class Node {
        private Map<Character, Node> children = new TreeMap<>();
        private List<Guest> guests = new ArrayList<>();
    }

    public void insert(Guest guest) {
        for (String word : guest.getName().toLowerCase().split("\\s+")) {
            insertWord(word, guest);
        }
        insertWord(guest.getName().toLowerCase(), guest);
    }

    private void insertWord(String word, Guest guest) {
        Node node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new Node());
        }
        if (!node.guests.contains(guest)) {
            node.guests.add(guest);
        }
    }

    public List<Guest> startsWith(String prefix) {
        List<Guest> result = new ArrayList<>();
        Node node = root;
        for (char c : prefix.toLowerCase().trim().toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return result;
            }
        }
        collect(node, result);
        return result;
    }

    private void collect(Node node, List<Guest> result) {
        for (Guest guest : node.guests) {
            if (!result.contains(guest)) {
                result.add(guest);
            }
        }
        for (Node child : node.children.values()) {
            collect(child, result);
        }
    }

}
