import java.util.*;

public class UPTMMarketingOptimization
{
  static int[][] costMatrix = {
      {0,15,25,35},
      {15,0,30,28},
      {25,30,0,20},
      {35,28,20,0}
  };
  
  static String[] locations = {"UPTM", "City B", "City C", "City D"};
      public static String greedyMCOP(int[][] dist){
          int n = dist.length;
          boolean[] visited = new boolean[n];
          StringBuilder route = new StringBuilder();
          int cur = 0;
          route.append(locations[cur]);
          visited[cur] = true;
          int totalCost = 0;
          
          for (int step = 1; step < n; step++){
              int next = 1;
              int best = Integer.MAX_VALUE;
              for (int j = 0; j < n; j++){
                  if (!visited[j] && dist[cur][j] < best){
                      best = dist[cur][j];
                      next = j;
                  }
              }
              if (next == -1) break;
              visited[next] = true;
              totalCost += dist[cur][next];
              route.append(" -> ").append(locations[next]);
              cur = next;
          }
          totalCost += dist[cur][0];
          route.append(" -> ").append(locations[0]);
          return "Greedy Route:" + route.toString() + " | Total Cost: " + totalCost;
      }
      
      public static String dynamicProgrammingMCOP(int[][]dist){
          int n = dist.length;
          int VISITED_ALL = (1 << n)-1;
          int [][] memo = new int[1 << n][n];
          String [][] paths = new String[1 << n][n];
          
          for (int i = 0; i < (1 << n); i++) Arrays.fill(memo[i],-1);
          int minCost = dynamicProgrammingMCOPHelper(0,1,dist,memo,VISITED_ALL,paths);
          
          StringBuilder route = new StringBuilder();
          int mask = 1;
          int pos = 0;
          route.append(locations[0]);
          while (mask != VISITED_ALL){
              String nextStr = paths[mask][pos];
              if (nextStr == null || nextStr.equals(""))break;
              int next = Integer.parseInt(nextStr);
              route.append(" -> ").append(locations[next]);
              pos = next;
              mask |= (1<<pos);
          }
          route.append(" -> ").append(locations[0]);
          
          return "Dynamic Programming Route: " + route.toString() + " | Total Cost: " + minCost;
      }
      
      private static int dynamicProgrammingMCOPHelper(int pos, int mask,int [][] dist, int[][] memo, int VISITED_ALL, String[][] paths){
          int n = dist.length;
          if (mask == VISITED_ALL){
              return dist[pos][0];
          }
          if (memo[mask][pos]!=-1){
              return memo[mask][pos];
          }
          int ans = Integer.MAX_VALUE;
          int bestNext = -1;
          for (int city = 0; city < n; city++){
              if ((mask & (1 << city)) == 0){
                  int newCost = dist[pos][city] + dynamicProgrammingMCOPHelper(city,mask | (1 << city), dist, memo,VISITED_ALL, paths);
                  if (newCost < ans){
                      ans = newCost;
                      bestNext = city;
                  }
              }
          }
          memo[mask][pos] = ans;
          paths[mask][pos] = (bestNext == -1)?"": Integer.toString(bestNext);
          return ans;
      }
      static int bestCostBacktracking = Integer.MAX_VALUE;
      static String bestPathBacktracking  = "";
      
      public static String backtrackingMCOP(int[][] dist) {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        StringBuilder path = new StringBuilder();
        visited[0] = true;
        path.append(locations[0]);
        bestCostBacktracking = Integer.MAX_VALUE;
        bestPathBacktracking = "";
        mcopBacktracking(0, dist, visited, n, 1, 0, path);
        return "Backtracking Route: " + bestPathBacktracking + "  | Total Cost: " + bestCostBacktracking;
    }

    private static int mcopBacktracking(int pos, int[][] dist, boolean[] visited, int n, int count, int cost, StringBuilder path) {
        if (count == n) {
            int total = cost + dist[pos][0]; // return to start
            String fullPath = path.toString() + " -> " + locations[0];
            if (total < bestCostBacktracking) {
                bestCostBacktracking = total;
                bestPathBacktracking = fullPath;
            }
            return total;
        }

        for (int city = 0; city < n; city++) {
            if (!visited[city]) {
                visited[city] = true;
                int lenBefore = path.length();
                path.append(" -> ").append(locations[city]);
                mcopBacktracking(city, dist, visited, n, count + 1, cost + dist[pos][city], path);
                path.setLength(lenBefore);
                visited[city] = false;
            }
        }
        return bestCostBacktracking;
    }

    // ---------- Divide and Conquer (Recursive + Branch-and-Bound) ----------
    static int bestCostDivide = Integer.MAX_VALUE;
    static String bestPathDivide = "";

    public static String divideAndConquerMCOP(int[][] dist) {
        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true;
        bestCostDivide = Integer.MAX_VALUE;
        bestPathDivide = "";
        divideAndConquerHelper(0, visited, 0, dist, n, new StringBuilder(locations[0]));
        return "Divide & Conquer Route: " + bestPathDivide + "  | Total Cost: " + bestCostDivide;
    }

    private static int divideAndConquerHelper(int pos, boolean[] visited, int currentCost, int[][] dist, int n, StringBuilder path) {
        // Branch-and-bound style recursion (pruning)
        if (currentCost >= bestCostDivide) return Integer.MAX_VALUE; // prune
        boolean allVisited = true;
        for (int i = 0; i < n; i++) if (!visited[i]) { allVisited = false; break; }
        if (allVisited) {
            int total = currentCost + dist[pos][0];
            String full = path.toString() + " -> " + locations[0];
            if (total < bestCostDivide) {
                bestCostDivide = total;
                bestPathDivide = full;
            }
            return total;
        }

        for (int city = 0; city < n; city++) {
            if (!visited[city]) {
                visited[city] = true;
                int lenBefore = path.length();
                path.append(" -> ").append(locations[city]);
                divideAndConquerHelper(city, visited, currentCost + dist[pos][city], dist, n, path);
                path.setLength(lenBefore);
                visited[city] = false;
            }
        }
        return bestCostDivide;
    }

    // ---------- Utility: allVisited (unused) ----------
    private static boolean allVisited(boolean[] visited) {
        for (boolean b : visited) if (!b) return false;
        return true;
    }

    // ---------- Insertion Sort ----------
    public static String insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
        return "Insertion sort done";
    }

    // ---------- Binary Search (returns index) ----------
    public static String binarySearch(int[] arr, int target) {
        int l = 0, r = arr.length - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (arr[mid] == target) return Integer.toString(mid);
            if (arr[mid] < target) l = mid + 1;
            else r = mid - 1;
        }
        return "-1";
    }
    static class MinHeap {
        private PriorityQueue<Integer> pq;

        MinHeap() {
            pq = new PriorityQueue<>();
        }

        void insert(int x) {
            pq.add(x);
        }

        int extractMin() {
            Integer v = pq.poll();
            return (v == null) ? Integer.MIN_VALUE : v;
        }

        // For demonstration
        Integer peek() {
            return pq.peek();
        }
    }

    // ---------- Splay Tree ----------
    static class SplayTree {
        private class Node {
            int key;
            Node left, right, parent;
            Node(int k) { key = k; left = right = parent = null; }
        }

        private Node root = null;

        // Right rotation
        private void rightRotate(Node x) {
            Node y = x.parent;
            if (y == null) return;
            y.left = x.right;
            if (x.right != null) x.right.parent = y;
            x.right = y;
            x.parent = y.parent;
            if (y.parent != null) {
                if (y == y.parent.left) y.parent.left = x; else y.parent.right = x;
            } else {
                root = x;
            }
            y.parent = x;
        }

        // Left rotation
        private void leftRotate(Node x) {
            Node y = x.parent;
            if (y == null) return;
            y.right = x.left;
            if (x.left != null) x.left.parent = y;
            x.left = y;
            x.parent = y.parent;
            if (y.parent != null) {
                if (y == y.parent.left) y.parent.left = x; else y.parent.right = x;
            } else {
                root = x;
            }
            y.parent = x;
        }

        // Splay node to root
        private void splay(Node x) {
            if (x == null) return;
            while (x.parent != null) {
                Node p = x.parent;
                Node g = p.parent;
                if (g == null) {
                    // Zig
                    if (x == p.left) rightRotate(x); else leftRotate(x);
                } else if (x == p.left && p == g.left) {
                    // Zig-Zig
                    rightRotate(p);
                    rightRotate(x);
                } else if (x == p.right && p == g.right) {
                    // Zig-Zig
                    leftRotate(p);
                    leftRotate(x);
                } else if (x == p.right && p == g.left) {
                    // Zig-Zag
                    leftRotate(x);
                    rightRotate(x);
                } else {
                    // Zig-Zag
                    rightRotate(x);
                    leftRotate(x);
                }
            }
        }

        public void insert(int key) {
            if (root == null) {
                root = new Node(key);
                return;
            }
            Node cur = root, p = null;
            while (cur != null) {
                p = cur;
                if (key < cur.key) cur = cur.left;
                else cur = cur.right;
            }
            Node node = new Node(key);
            node.parent = p;
            if (key < p.key) p.left = node; else p.right = node;
            splay(node);
        }

        public boolean search(int key) {
            Node cur = root;
            while (cur != null) {
                if (key == cur.key) {
                    splay(cur);
                    return true;
                } else if (key < cur.key) cur = cur.left;
                else cur = cur.right;
            }
            return false;
        }
    }

    // Driver method
    public static void main(String[] args) {
        System.out.println(greedyMCOP(costMatrix));
        System.out.println(dynamicProgrammingMCOP(costMatrix));
        System.out.println(backtrackingMCOP(costMatrix));
        System.out.println(divideAndConquerMCOP(costMatrix));
        
        // Sorting and Searching
        int[] arr = {8, 3, 5, 1, 9, 2};
        insertionSort(arr);
        System.out.println("Sorted Array: " + Arrays.toString(arr));
        System.out.println("Binary Search (5 found at index): " + binarySearch(arr, 5));

        // Min-Heap Test
        MinHeap heap = new MinHeap();
        heap.insert(10);
        heap.insert(3);
        heap.insert(15);
        System.out.println("Min-Heap Extract Min: " + heap.extractMin());

        // Splay Tree Test
        SplayTree tree = new SplayTree();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        System.out.println("Splay Tree Search (10 found): " + tree.search(10));
    }

}