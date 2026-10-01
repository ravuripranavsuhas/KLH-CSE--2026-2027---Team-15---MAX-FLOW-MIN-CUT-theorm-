
import java.util.*;

public class Network {
    static int[][] capacity;
    static int[][] flow;
    static int n;

    // Find an augmenting path using BFS
    static boolean bfs(int source, int sink, int[] parent) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();

        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < n; v++) {
                int residual = capacity[u][v] - flow[u][v];

                if (!visited[v] && residual > 0) {
                    parent[v] = u;
                    visited[v] = true;
                    queue.add(v);

                    if (v == sink) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Calculate maximum flow
    static int maxFlow(int source, int sink) {
        int[] parent = new int[n];
        int maxFlow = 0;
        int pathNumber = 1;

        while (bfs(source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;

            // Find the minimum residual capacity on the path
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow,
                        capacity[u][v] - flow[u][v]);
            }

            // Display augmenting path
            List<Integer> path = new ArrayList<>();
            for (int v = sink; v != -1; v = parent[v]) {
                path.add(v);
            }
            Collections.reverse(path);

            System.out.print("Augmenting Path " + pathNumber++ + ": ");
            for (int i = 0; i < path.size(); i++) {
                System.out.print(path.get(i));
                if (i < path.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println(" | Additional flow: " + pathFlow);

            // Update residual flow
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                flow[u][v] += pathFlow;
                flow[v][u] -= pathFlow;
            }

            maxFlow += pathFlow;
            System.out.println("Current maximum flow: " + maxFlow);
            System.out.println();
        }

        return maxFlow;
    }

    // Find the nodes reachable from source in residual network
    static boolean[] findReachable(int source) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();

        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < n; v++) {
                int residual = capacity[u][v] - flow[u][v];

                if (!visited[v] && residual > 0) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
        return visited;
    }

    static void displayResidualNetwork() {
        System.out.println("\nResidual Network:");
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                int residual = capacity[u][v] - flow[u][v];

                if (residual > 0) {
                    System.out.println(u + " -> " + v
                            + " : " + residual);
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of nodes: ");
        n = sc.nextInt();

        capacity = new int[n][n];
        flow = new int[n][n];

        System.out.print("Enter number of edges: ");
        int m = sc.nextInt();

        System.out.println("Enter each edge: from to capacity");
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int c = sc.nextInt();

            capacity[u][v] = c;
        }

        System.out.print("Enter source node: ");
        int source = sc.nextInt();

        System.out.print("Enter destination node: ");
        int sink = sc.nextInt();

        if (source < 0 || source >= n || sink < 0
                || sink >= n || source == sink) {
            System.out.println("Invalid source or destination.");
            sc.close();
            return;
        }

        int result = maxFlow(source, sink);

        System.out.println("Maximum Flow Value: " + result);

        displayResidualNetwork();

        // Minimum cut
        boolean[] reachable = findReachable(source);
        int cutCapacity = 0;

        System.out.println("\nMinimum Cut Edges:");
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (reachable[u] && !reachable[v]
                        && capacity[u][v] > 0) {
                    System.out.println(u + " -> " + v
                            + " : " + capacity[u][v]);
                    cutCapacity += capacity[u][v];
                }
            }
        }

        System.out.println("Minimum Cut Capacity: " + cutCapacity);
        sc.close();
    }
}