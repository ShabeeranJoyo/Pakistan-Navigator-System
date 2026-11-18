import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Comparator;

public class Navigation_System{
    static class MyList<T> implements Iterable<T> {                 
        class Node { T data; Node next; Node(T d) { data = d; } }
        Node head, tail; int size;
        void addFirst(T x) { Node n = new Node(x); n.next = head; head = n; if (tail == null) tail = n; size++; }
        void addLast(T x) { Node n = new Node(x); if (tail == null) head = tail = n; else { tail.next = n; tail = n; } size++; }
        public java.util.Iterator<T> iterator() { return new java.util.Iterator<T>() { Node cur = head;
            public boolean hasNext() { return cur != null; }
            public T next() { T d = cur.data; cur = cur.next; return d; } }; }
    }
    static class MyStack<T> {                                  
        Object[] a = new Object[8]; int top;
        void push(T x) { if (top == a.length) a = Arrays.copyOf(a, top * 2); a[top++] = x; }
        @SuppressWarnings("unchecked") T pop() { T x = (T) a[--top]; a[top] = null; return x; }
        @SuppressWarnings("unchecked") T peek() { return (T) a[top - 1]; }
        boolean isEmpty() { return top == 0; }
    }
    static class MyQueue<T> implements Iterable<T> {                
        Object[] a = new Object[8]; int front, size;
        void enqueue(T x) {
            if (size == a.length) {
                Object[] b = new Object[size * 2];
                for (int i = 0; i < size; i++) b[i] = a[(front + i) % a.length];
                a = b; front = 0;
            }
            a[(front + size++) % a.length] = x;
        }
        @SuppressWarnings("unchecked") T dequeue() { T x = (T) a[front]; front = (front + 1) % a.length; size--; return x; }
        public java.util.Iterator<T> iterator() { return new java.util.Iterator<T>() { int i = 0;
            public boolean hasNext() { return i < size; }
            @SuppressWarnings("unchecked") public T next() { return (T) a[(front + i++) % a.length]; } }; }
    }
    static class MinHeap {                                           // priority queue used by Dijkstra
        int[] item = new int[16]; double[] key = new double[16]; int size;
        void insert(int v, double k) {
            if (size == item.length) { item = Arrays.copyOf(item, size * 2); key = Arrays.copyOf(key, size * 2); }
            item[size] = v; key[size] = k;
            for (int i = size++; i > 0 && key[(i - 1) / 2] > key[i]; i = (i - 1) / 2) swap(i, (i - 1) / 2);
        }
        int extractMin() {
            int min = item[0]; size--; item[0] = item[size]; key[0] = key[size];
            for (int i = 0; ; ) {
                int l = 2 * i + 1, r = l + 1, s = i;
                if (l < size && key[l] < key[s]) s = l;  if (r < size && key[r] < key[s]) s = r;
                if (s == i) return min;
                swap(i, s); i = s;
            }
        }
        void swap(int x, int y) { int t = item[x]; item[x] = item[y]; item[y] = t; double d = key[x]; key[x] = key[y]; key[y] = d; }
    }
    static class Edge { final int to; final double km; Edge(int to, double km) { this.to = to; this.km = km; } }
    static class Route {                                             // result of a search
        final int from, to, explored; final String text, algo; final MyList<Integer> path; final double km, eff;
        Route(int from, int to, String text, String algo, MyList<Integer> path, double km, double eff, int explored) { this.eff = eff; this.from = from; this.to = to; this.text = text; this.algo = algo; this.path = path; this.km = km; this.explored = explored; }
        String time() { int m = (int) Math.round(eff / 70 * 60); return (m / 60) + " h " + (m % 60) + " min"; }   // 70 km/h average
        public String toString() { return text + "  (" + Math.round(km) + " km)"; }
    }
    static class Graph {                                             // array of adjacency lists
        City[] city = new City[40]; @SuppressWarnings("unchecked") MyList<Edge>[] adj = new MyList[40]; int n; int[][] traffic = new int[40][40];                // 0 normal, 1 heavy, 2 blocked
        double f(int a, int b) { return traffic[a][b] == 1 ? 1.5 : 1; }   // heavy traffic = 50% slower
        void addCity(City c) { c.id = n; city[n] = c; adj[n++] = new MyList<>(); }
        void addRoad(int a, int b, double km) { adj[a].addLast(new Edge(b, km)); adj[b].addLast(new Edge(a, km)); }
        int indexOf(String name) { for (int i = 0; i < n; i++) if (city[i].name.equals(name)) return i; return -1; }
        double leg(int a, int b) { for (Edge e : adj[a]) if (e.to == b) return e.km; return 0; }
        Route dijkstra(int s, int t) {                               // shortest distance (Min-Heap)
            double[] d = new double[n]; int[] prev = new int[n]; boolean[] done = new boolean[n]; int count = 0;
            Arrays.fill(d, Double.MAX_VALUE); Arrays.fill(prev, -1);
            MinHeap pq = new MinHeap(); d[s] = 0; pq.insert(s, 0);
            while (pq.size > 0) {
                int u = pq.extractMin();
                if (done[u]) continue;
                done[u] = true; count++;
                if (u == t) break;
                for (Edge e : adj[u]) { double w = e.km * f(u, e.to); if (traffic[u][e.to] < 2 && d[u] + w < d[e.to]) { d[e.to] = d[u] + w; prev[e.to] = u; pq.insert(e.to, d[e.to]); } }
            }
            return build(s, t, prev, done[t], "Fastest route, traffic aware", count);
        }
        Route bfs(int s, int t) {                                    // fewest roads (Queue)
            int[] prev = new int[n]; boolean[] seen = new boolean[n]; int count = 0; Arrays.fill(prev, -1);
            MyQueue<Integer> q = new MyQueue<>(); q.enqueue(s); seen[s] = true;
            while (q.size > 0) {
                int u = q.dequeue(); count++;
                if (u == t) break;
                for (Edge e : adj[u]) if (!seen[e.to] && traffic[u][e.to] < 2) { seen[e.to] = true; prev[e.to] = u; q.enqueue(e.to); }
            }
            return build(s, t, prev, seen[t], "Fewest roads", count);
        }
        Route dfs(int s, int t) {                                    // any route (Stack)
            int[] prev = new int[n]; boolean[] seen = new boolean[n]; int count = 0; Arrays.fill(prev, -1);
            MyStack<Integer> st = new MyStack<>(); st.push(s);
            while (!st.isEmpty()) {
                int u = st.pop();
                if (seen[u]) continue;
                seen[u] = true; count++;
                if (u == t) break;
                for (Edge e : adj[u]) if (!seen[e.to] && traffic[u][e.to] < 2) { prev[e.to] = u; st.push(e.to); }
            }
            return build(s, t, prev, seen[t], "Any route", count);
        }
        Route build(int s, int t, int[] prev, boolean ok, String algo, int count) {   // walk back from t to s
            MyList<Integer> path = new MyList<>(); double km = 0, eff = 0;
            if (ok) { for (int v = t; v != -1; v = prev[v]) path.addFirst(v); int b = -1; for (int v : path) { if (b != -1) { km += leg(b, v); eff += leg(b, v) * f(b, v); } b = v; } }
            return new Route(s, t, city[s].name + "  →  " + city[t].name, algo, path, km, eff, count);
        }
    }
    static class Place {
        final String category, name; Place(String c, String n) { category = c; name = n; }  public String toString() { return name + "  (" + category + ")"; }
    }
    static class City {
        int id; final String name, province, label; final double lon, lat; final Place[] places;
        City(String name, String province, double lon, double lat, String label, String... p) {   // p = "Category|Place name"
            this.name = name; this.province = province; this.lon = lon; this.lat = lat; this.label = label; places = new Place[p.length];
            for (int i = 0; i < p.length; i++) places[i] = new Place(p[i].split("\\|")[0], p[i].split("\\|")[1]);
        }
        public String toString() { return name; }
    }
    static Graph buildMap() {
        Graph g = new Graph();
        g.addCity(new City("Karachi", "Sindh", 67.01, 24.86, "R", "Landmark|Quaid-e-Azam Mausoleum", "Beach|Clifton Beach", "Museum|Mohatta Palace", "Shopping|Dolmen Mall", "Restaurant|Kolachi Restaurant"));
        g.addCity(new City("Hyderabad", "Sindh", 68.37, 25.39, "R", "Park|Rani Bagh", "Landmark|Pakka Qila", "Shopping|Resham Gali Bazaar", "Restaurant|Latifabad Food Street"));
        g.addCity(new City("Dadu", "Sindh", 67.78, 26.73, "L", "Lake|Manchar Lake", "Landmark|Ranikot Fort", "Park|Kirthar National Park", "Landmark|Khudabad Jamia Mosque", "Restaurant|Ali Baba Restaurant", "Restaurant|D Pizza", "Restaurant|Dadu Food Street"));
        g.addCity(new City("Nawabshah", "Sindh", 68.40, 26.25, "R", "Park|City Park", "Shopping|Main Bazaar", "Restaurant|Desi Food Street"));
        g.addCity(new City("Larkana", "Sindh", 68.21, 27.56, "L", "Landmark|Mohenjo-daro", "Landmark|Bhutto Mausoleum", "Shopping|Shahi Bazaar"));
        g.addCity(new City("Sukkur", "Sindh", 68.86, 27.70, "R", "Landmark|Lansdowne Bridge", "Landmark|Masoom Shah Minaret", "Park|Sadhu Bela Island", "Restaurant|Indus Riverside Cafe"));
        g.addCity(new City("Quetta", "Balochistan", 66.99, 30.18, "L", "Lake|Hanna Lake", "Park|Hazarganji Chiltan National Park", "Shopping|Liaquat Bazaar", "Restaurant|Cafe Parizaad"));
        g.addCity(new City("Khuzdar", "Balochistan", 66.61, 27.81, "L", "Shopping|Khuzdar Bazaar", "Park|City Park", "Restaurant|Sajji House"));
        g.addCity(new City("Gwadar", "Balochistan", 62.32, 25.12, "T", "Landmark|Gwadar Deep Sea Port", "Landmark|Hammerhead Cliff", "Beach|Marine Drive", "Beach|Padi Zirr Beach"));
        g.addCity(new City("Bahawalpur", "Punjab", 71.68, 29.40, "L", "Landmark|Noor Mahal", "Park|Lal Suhanra National Park", "Landmark|Derawar Fort", "Park|Bahawalpur Zoo"));
        g.addCity(new City("Multan", "Punjab", 71.47, 30.20, "L", "Landmark|Shrine of Shah Rukn-e-Alam", "Park|Qasim Bagh", "Shopping|Hussain Agahi Bazaar", "Restaurant|Hafiz Ka Sohan Halwa"));
        g.addCity(new City("Faisalabad", "Punjab", 73.08, 31.42, "L", "Landmark|Clock Tower (Ghanta Ghar)", "Park|Jinnah Garden", "Shopping|Eight Bazaars", "Restaurant|Faisalabad Food Street"));
        g.addCity(new City("Lahore", "Punjab", 74.34, 31.55, "B", "Landmark|Badshahi Mosque", "Landmark|Lahore Fort", "Park|Shalimar Gardens", "Shopping|Anarkali Bazaar", "Restaurant|Gawalmandi Food Street"));
        g.addCity(new City("Islamabad", "Islamabad Capital Territory", 73.05, 33.68, "L", "Landmark|Faisal Mosque", "Park|Daman-e-Koh", "Landmark|Pakistan Monument", "Museum|Lok Virsa Museum", "Restaurant|Monal Restaurant"));
        g.addCity(new City("Abbottabad", "Khyber Pakhtunkhwa", 73.21, 34.15, "R", "Park|Shimla Hill", "Park|Thandiani Hills", "Landmark|Ilyasi Mosque"));
        g.addCity(new City("Peshawar", "Khyber Pakhtunkhwa", 71.58, 34.01, "L", "Shopping|Qissa Khwani Bazaar", "Landmark|Bala Hisar Fort", "Museum|Peshawar Museum", "Restaurant|Namak Mandi"));
        g.addCity(new City("Muzaffarabad", "Azad Kashmir", 73.47, 34.37, "R", "Landmark|Red Fort (Lal Qila)", "Park|Pir Chinasi", "Landmark|Neelum River Bank"));
        g.addCity(new City("Gilgit", "Gilgit-Baltistan", 74.31, 35.92, "L", "Landmark|Kargah Buddha", "Landmark|Gilgit Suspension Bridge", "Park|Naltar Valley"));
        g.addCity(new City("Skardu", "Gilgit-Baltistan", 75.64, 35.30, "R", "Park|Deosai National Park", "Lake|Lower Kachura Lake", "Landmark|Kharpocho Fort"));
        String[] roads = {"Karachi|Hyderabad|165", "Hyderabad|Nawabshah|150", "Hyderabad|Dadu|215", "Nawabshah|Dadu|135", "Nawabshah|Sukkur|220", "Dadu|Larkana|85",
            "Larkana|Sukkur|75", "Sukkur|Bahawalpur|350", "Karachi|Khuzdar|440", "Khuzdar|Quetta|300", "Dadu|Khuzdar|280", "Karachi|Gwadar|630", "Bahawalpur|Multan|100",
            "Multan|Faisalabad|240", "Multan|Lahore|340", "Faisalabad|Lahore|150", "Lahore|Islamabad|375", "Faisalabad|Islamabad|300", "Islamabad|Peshawar|175",
            "Islamabad|Abbottabad|120", "Abbottabad|Muzaffarabad|75", "Islamabad|Gilgit|520", "Gilgit|Skardu|230"};
        for (String r : roads) { String[] p = r.split("\\|"); g.addRoad(g.indexOf(p[0]), g.indexOf(p[1]), Double.parseDouble(p[2])); }
        return g;
    }
    static class CityBST {                                           // Binary Search Tree keyed by city name
        class Node { String key; City city; Node left, right; Node(String k, City c) { key = k; city = c; } }
        Node root;
        void insert(City c) { root = insert(root, c.name.toLowerCase(), c); }
        Node insert(Node n, String k, City c) {
            if (n == null) return new Node(k, c);
            if (k.compareTo(n.key) < 0) n.left = insert(n.left, k, c); else n.right = insert(n.right, k, c);  return n;
        }
        City find(String name) {                                     // exact search
            Node n = root; String k = name.trim().toLowerCase();
            while (n != null && !k.equals(n.key)) n = k.compareTo(n.key) < 0 ? n.left : n.right;
            return n == null ? null : n.city;
        }
        MyList<City> startingWith(String prefix) { MyList<City> out = new MyList<>(); walk(root, prefix.trim().toLowerCase(), out); return out; }   // "" = all cities A-Z
        void walk(Node n, String p, MyList<City> out) {              // in-order traversal gives alphabetical order
            if (n == null) return;
            walk(n.left, p, out);
            if (n.key.startsWith(p)) out.addLast(n.city);
            walk(n.right, p, out);
        }
    }
    static <T> void mergeSort(T[] a, T[] tmp, int lo, int hi, Comparator<? super T> c) {
        if (lo >= hi) return;
        int mid = (lo + hi) / 2, i = lo, j = mid + 1, k = lo;
        mergeSort(a, tmp, lo, mid, c); mergeSort(a, tmp, mid + 1, hi, c);
        while (i <= mid && j <= hi) tmp[k++] = c.compare(a[i], a[j]) <= 0 ? a[i++] : a[j++];
        while (i <= mid) tmp[k++] = a[i++];  while (j <= hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, lo, a, lo, hi - lo + 1);
    }
    static <T> void quickSort(T[] a, int lo, int hi, Comparator<? super T> c) {
        if (lo >= hi) return;
        T pivot = a[(lo + hi) / 2]; int i = lo, j = hi;
        while (i <= j) {
            while (c.compare(a[i], pivot) < 0) i++;  while (c.compare(a[j], pivot) > 0) j--;
            if (i <= j) { T t = a[i]; a[i] = a[j]; a[j] = t; i++; j--; }
        }
        quickSort(a, lo, j, c); quickSort(a, i, hi, c);
    }
    static final Color GREEN = new Color(0x01411C), ACCENT = new Color(0x2B8A3E), ORANGE = new Color(0xE8590C);
    static class MapPanel extends JPanel {                           // draws map, roads, cities and the route
        static final double[][] BORDER = {{61.6,25.2},{64.5,25.2},{66.7,24.8},{67.5,24.1},{68.0,23.7},{68.2,24.4},{70.0,24.2},{70.6,24.9},{71.1,24.4},{71.1,25.7},
            {70.0,26.6},{70.3,28.0},{71.9,29.0},{73.0,29.9},{74.6,31.0},{74.7,31.9},{75.4,32.3},{74.2,33.0},{74.5,34.0},{75.8,35.0},{77.0,35.4},{76.5,36.0},
            {75.8,36.7},{74.0,36.9},{72.6,36.8},{71.2,36.0},{71.0,34.6},{69.9,33.7},{70.0,33.0},{69.3,31.9},{68.0,31.7},{66.4,30.9},{66.3,29.9},{64.2,29.5},
            {61.7,29.8},{60.9,29.0},{61.5,28.0},{62.8,27.2},{62.8,26.6},{61.7,26.2}};
        final Graph g; Route route; int from = -1, to = -1, selected = -1; java.util.function.IntConsumer onClick;
        MapPanel(Graph g) {
            this.g = g; setBackground(new Color(0xE6F0F7));
            addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { for (int i = 0; i < g.n; i++) if (pos(g.city[i]).distance(e.getX(), e.getY()) <= 9) onClick.accept(i); } });
        }
        double s() { return Math.min((getWidth() - 40) / 17.2, (getHeight() - 40) / 14.1); }
        Point pos(City c) { return new Point((int) ((getWidth() - s() * 17.2) / 2 + (c.lon - 60.6) * s()), (int) ((getHeight() - s() * 14.1) / 2 + (37.3 - c.lat) * s())); }
        boolean onRoute(int id) { if (route != null) for (int v : route.path) if (v == id) return true; return false; }
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D d = (Graphics2D) g0;
            d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Path2D.Double land = new Path2D.Double();
            for (int i = 0; i < BORDER.length; i++) {
                Point p = pos(new City("", "", BORDER[i][0], BORDER[i][1], ""));
                if (i == 0) land.moveTo(p.x, p.y); else land.lineTo(p.x, p.y);
            }
            land.closePath(); d.setColor(new Color(0xE9F1E4)); d.fill(land); d.setColor(new Color(0x9DB596)); d.setStroke(new BasicStroke(2f)); d.draw(land);
            for (int i = 0; i < g.n; i++) for (Edge e : g.adj[i]) if (e.to > i) {      // roads coloured by traffic
                int t = g.traffic[i][e.to]; Point a = pos(g.city[i]), b = pos(g.city[e.to]);
                d.setStroke(new BasicStroke(t == 0 ? 1.6f : 3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, t == 2 ? new float[]{6, 6} : null, 0f));
                d.setColor(t == 0 ? new Color(0xB9C2C9) : t == 1 ? new Color(0xF59F00) : new Color(0xC92A2A)); d.drawLine(a.x, a.y, b.x, b.y); }
            if (route != null) {                                     // highlighted route
                d.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); d.setColor(ORANGE);
                int b = -1;
                for (int v : route.path) { if (b != -1) { Point p = pos(g.city[b]), q = pos(g.city[v]); d.drawLine(p.x, p.y, q.x, q.y); } b = v; }
            }
            for (int i = 0; i < g.n; i++) {
                City c = g.city[i]; Point p = pos(c); boolean bold = i == from || i == to || onRoute(i) || i == selected; int r = bold ? 7 : 5;
                if (i == selected) { d.setColor(new Color(0x1C7ED6)); d.setStroke(new BasicStroke(2f)); d.drawOval(p.x - r - 4, p.y - r - 4, 2 * r + 8, 2 * r + 8); }
                d.setColor(Color.WHITE); d.fillOval(p.x - r - 2, p.y - r - 2, 2 * r + 4, 2 * r + 4);
                d.setColor(i == from ? ACCENT : i == to ? new Color(0xC92A2A) : onRoute(i) ? ORANGE : GREEN); d.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);
                d.setFont(getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN, 11.5f));
                FontMetrics fm = d.getFontMetrics(); int w = fm.stringWidth(c.name);
                int tx = c.label.equals("L") ? p.x - r - 6 - w : c.label.equals("R") ? p.x + r + 6 : p.x - w / 2;
                int ty = c.label.equals("T") ? p.y - r - 6 : c.label.equals("B") ? p.y + r + fm.getAscent() + 2 : p.y + 4;
                d.setColor(new Color(0x212529)); d.drawString(c.name, tx, ty);
            }
        }
    }
    static class Road { final int a, b; final String label; Road(int a, int b, String l) { this.a = a; this.b = b; label = l; } public String toString() { return label; } }

    static class Gui extends JFrame {
        final Graph g = buildMap(); final CityBST bst = new CityBST(); final MapPanel map = new MapPanel(g);
        final MyStack<Route> history = new MyStack<>();              // Stack  -> Back button
        final MyQueue<Route> recent = new MyQueue<>();               // Queue  -> last 6 searches
        final JComboBox<City> fromBox = new JComboBox<>(), toBox = new JComboBox<>();
        final JRadioButton rbD = new JRadioButton("Fastest route", true), rbB = new JRadioButton("Fewest roads"), rbF = new JRadioButton("Any route");
        final JTextField search = new JTextField(); final DefaultListModel<City> suggest = new DefaultListModel<>();
        final JEditorPane routePane = new JEditorPane("text/html", ""); final JTabbedPane tabs = new JTabbedPane();
        final JLabel title = new JLabel("Select a city"), sub = new JLabel("Click a city on the map to see its places.");
        final JComboBox<String> sortBox = new JComboBox<>(new String[]{"Original order", "Name A-Z", "Category"});
        final DefaultListModel<Place> placeModel = new DefaultListModel<>(); final DefaultListModel<Route> recentModel = new DefaultListModel<>();
        final JComboBox<String> filterBox = new JComboBox<>(new String[]{"All places", "Restaurant", "Park", "Landmark", "Shopping"});
        final JLabel status = new JLabel(" "); int sel = -1;
        Gui() {
            super("Pakistan Navigator");
            for (int i = 0; i < g.n; i++) bst.insert(g.city[i]);
            for (City c : bst.startingWith("")) { fromBox.addItem(c); toBox.addItem(c); }          // A-Z via BST
            fromBox.setSelectedItem(bst.find("Karachi")); toBox.setSelectedItem(bst.find("Lahore"));
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            JLabel head = new JLabel("  Pakistan Navigator  -  " + g.n + " cities, smart route planner");
            head.setOpaque(true); head.setBackground(GREEN); head.setForeground(Color.WHITE); head.setFont(head.getFont().deriveFont(Font.BOLD, 18f)); head.setBorder(new EmptyBorder(14, 10, 14, 10));
            status.setBorder(new EmptyBorder(6, 16, 6, 16));
            add(head, BorderLayout.NORTH); add(controls(), BorderLayout.WEST); add(map, BorderLayout.CENTER); add(rightTabs(), BorderLayout.EAST); add(status, BorderLayout.SOUTH);
            map.onClick = this::selectCity; clearRoute(); setSize(1300, 760); setLocationRelativeTo(null);
        }
        JPanel controls() {
            JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setBorder(new EmptyBorder(14, 16, 14, 16)); p.setPreferredSize(new Dimension(250, 0));
            search.addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent e) { suggestions(); } });   // autocomplete with BST
            JList<City> sl = new JList<>(suggest); JScrollPane sp = new JScrollPane(sl); sp.setPreferredSize(new Dimension(200, 80)); sp.setMaximumSize(new Dimension(999, 80));
            sl.addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && sl.getSelectedValue() != null) selectCity(sl.getSelectedValue().id); });
            JButton find = new JButton("Find Route"), back = new JButton("◀ Back"), clear = new JButton("Clear");
            find.setBackground(ACCENT); find.setForeground(Color.WHITE); find.addActionListener(e -> findRoute());
            back.addActionListener(e -> goBack()); clear.addActionListener(e -> clearRoute());
            JPanel row = new JPanel(new GridLayout(1, 2, 8, 0)); row.add(back); row.add(clear);
            ButtonGroup bg = new ButtonGroup(); bg.add(rbD); bg.add(rbB); bg.add(rbF);
            Component[] items = {new JLabel("SEARCH CITY"), search, sp, Box.createVerticalStrut(14), new JLabel("FROM"), fromBox, Box.createVerticalStrut(8), new JLabel("TO"), toBox,
                Box.createVerticalStrut(14), new JLabel("ROUTE TYPE"), rbD, rbB, rbF, Box.createVerticalStrut(16), find, Box.createVerticalStrut(8), row};
            for (Component c : items) { ((JComponent) c).setAlignmentX(0f); if (!(c instanceof JScrollPane)) c.setMaximumSize(new Dimension(999, 34)); p.add(c); }
            return p;
        }
        JTabbedPane rightTabs() {
            tabs.setPreferredSize(new Dimension(330, 0)); routePane.setEditable(false); tabs.addTab("Route", new JScrollPane(routePane));
            JPanel pl = new JPanel(new BorderLayout(0, 8)); pl.setBorder(new EmptyBorder(12, 12, 12, 12));
            JPanel top = new JPanel(new GridLayout(3, 1)), combos = new JPanel(new GridLayout(1, 2, 6, 0)); title.setFont(title.getFont().deriveFont(Font.BOLD, 19f)); title.setForeground(GREEN);
            sortBox.addActionListener(e -> refreshPlaces()); filterBox.addActionListener(e -> refreshPlaces()); combos.add(filterBox); combos.add(sortBox); top.add(title); top.add(sub); top.add(combos);
            JButton sf = new JButton("Set as From"), st = new JButton("Set as To"); JPanel btn = new JPanel(new GridLayout(1, 2, 8, 0)); btn.add(sf); btn.add(st);
            sf.addActionListener(e -> { if (sel >= 0) fromBox.setSelectedItem(g.city[sel]); }); st.addActionListener(e -> { if (sel >= 0) toBox.setSelectedItem(g.city[sel]); });
            JList<Place> placeList = new JList<>(placeModel); placeList.setFixedCellHeight(26);
            pl.add(top, BorderLayout.NORTH); pl.add(new JScrollPane(placeList)); pl.add(btn, BorderLayout.SOUTH); tabs.addTab("City & Places", pl);
            JList<Route> rl = new JList<>(recentModel); rl.setFixedCellHeight(28);
            rl.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2 && rl.getSelectedValue() != null) showRoute(rl.getSelectedValue()); } });
            JPanel rc = new JPanel(new BorderLayout(0, 8)); rc.setBorder(new EmptyBorder(12, 12, 12, 12));
            rc.add(new JLabel("<html><b>Recent searches</b> (double-click to show again)</html>"), BorderLayout.NORTH); rc.add(new JScrollPane(rl)); tabs.addTab("Recent", rc);
            tabs.addTab("Admin", adminTab()); return tabs;
        }

        JPanel adminTab() {                                          // Admin: set road traffic (password: admin123)
            JButton login = new JButton("Admin login"), apply = new JButton("Apply to road"), reset = new JButton("Reset all roads to normal");
            JComboBox<Road> roadBox = new JComboBox<>(); JComboBox<String> statusBox = new JComboBox<>(new String[]{"Normal", "Heavy traffic", "Blocked"});
            for (int i = 0; i < g.n; i++) for (Edge e : g.adj[i]) if (e.to > i) roadBox.addItem(new Road(i, e.to, g.city[i].name + " \u2194 " + g.city[e.to].name));
            Component[] locked = {roadBox, statusBox, apply, reset}; for (Component c : locked) c.setEnabled(false);
            login.addActionListener(e -> {
                JPasswordField pw = new JPasswordField();
                if (JOptionPane.showConfirmDialog(this, pw, "Admin password", JOptionPane.OK_CANCEL_OPTION) == 0 && new String(pw.getPassword()).equals("admin123")) { for (Component c : locked) c.setEnabled(true); login.setText("Logged in as Admin"); }
                else status.setText("Wrong password or cancelled.");
            });
            apply.addActionListener(e -> { Road r = (Road) roadBox.getSelectedItem(); g.traffic[r.a][r.b] = g.traffic[r.b][r.a] = statusBox.getSelectedIndex(); map.repaint(); status.setText(r + ": " + statusBox.getSelectedItem()); if (map.route != null) findRoute(); });
            reset.addActionListener(e -> { for (int[] t : g.traffic) Arrays.fill(t, 0); map.repaint(); if (map.route != null) findRoute(); });
            JPanel p = new JPanel(new GridLayout(0, 1, 0, 6)), wrap = new JPanel(new BorderLayout()); p.setBorder(new EmptyBorder(12, 12, 12, 12));
            for (Component c : new Component[]{new JLabel("<html>Map: <b style='color:#F59F00'>yellow</b> = heavy traffic, <b style='color:#C92A2A'>red dashed</b> = blocked</html>"), login, new JLabel("Road"), roadBox, new JLabel("Traffic status"), statusBox, apply, reset}) p.add(c);
            wrap.add(p, BorderLayout.NORTH); return wrap;
        }
        void suggestions() { suggest.clear(); if (!search.getText().trim().isEmpty()) for (City c : bst.startingWith(search.getText())) suggest.addElement(c); }
        void selectCity(int id) {                                    // shows the places of the clicked city
            sel = id; map.selected = id; map.repaint(); title.setText(g.city[id].name); sub.setText(g.city[id].province + "  •  " + g.city[id].places.length + " places");
            refreshPlaces(); tabs.setSelectedIndex(1); status.setText("Selected: " + g.city[id].name);
        }
        void refreshPlaces() {
            placeModel.clear(); if (sel < 0) return;
            Place[] list = Arrays.stream(g.city[sel].places).filter(p -> filterBox.getSelectedIndex() == 0 || p.category.equals(filterBox.getSelectedItem())).toArray(Place[]::new);
            if (sortBox.getSelectedIndex() == 1) mergeSort(list, Arrays.copyOf(list, list.length), 0, list.length - 1, Comparator.comparing((Place p) -> p.name));
            if (sortBox.getSelectedIndex() == 2) quickSort(list, 0, list.length - 1, Comparator.comparing((Place p) -> p.category).thenComparing(p -> p.name));
            for (Place p : list) placeModel.addElement(p);
        }
        void findRoute() {
            City a = (City) fromBox.getSelectedItem(), b = (City) toBox.getSelectedItem();
            if (a.id == b.id) { JOptionPane.showMessageDialog(this, "Please choose two different cities."); return; }
            Route r = rbB.isSelected() ? g.bfs(a.id, b.id) : rbF.isSelected() ? g.dfs(a.id, b.id) : g.dijkstra(a.id, b.id);
            if (r.path.size == 0) { map.route = null; map.repaint(); JOptionPane.showMessageDialog(this, "No route available - roads in between are blocked."); return; }
            history.push(r); recent.enqueue(r);
            if (recent.size > 6) recent.dequeue();
            recentModel.clear(); for (Route x : recent) recentModel.add(0, x);
            showRoute(r);
        }
        void goBack() { if (history.isEmpty()) return; history.pop(); if (history.isEmpty()) clearRoute(); else showRoute(history.peek()); }
        void clearRoute() {
            while (!history.isEmpty()) history.pop();
            map.route = null; map.from = map.to = -1; map.repaint(); status.setText("Choose two cities and press Find Route.");
            routePane.setText("<html><body style='font-family:sans-serif;margin:6px'><h2 style='color:#01411C'>Plan your journey</h2><p>1. Choose <b>From</b> and <b>To</b>.<br>2. Pick a route type.<br>3. Press <b>Find Route</b>.</p></body></html>");
        }
        String food(City c) {                                        // restaurants of a city
            String t = ""; for (Place p : c.places) if (p.category.equals("Restaurant")) t += (t.isEmpty() ? "" : ", ") + p.name;
            return t.isEmpty() ? "" : "<br><span style='color:gray'>Eat: " + t + "</span>";
        }

        void showRoute(Route r) {
            fromBox.setSelectedItem(g.city[r.from]); toBox.setSelectedItem(g.city[r.to]); map.route = r; map.from = r.from; map.to = r.to; map.repaint();
            String big = "<b style='color:#E8590C;font-size:15px'>";
            StringBuilder h = new StringBuilder("<html><body style='font-family:sans-serif;margin:6px'><h2 style='color:#01411C'>" + r.text + "</h2><p style='color:gray'>" + r.algo + "</p><p>"
                + big + Math.round(r.km) + " km</b> &nbsp; " + big + r.time() + "</b> &nbsp; " + big + (r.path.size - 1) + " roads</b></p><h3>Directions (with restaurants on the way)</h3><table cellpadding='3'>");
            int b = -1;
            for (int v : r.path) { h.append(b == -1 ? "<tr><td><b>Start</b></td><td><b>" : "<tr><td>&#8594;</td><td>").append(g.city[v].name)
                .append(b == -1 ? "" : " (" + Math.round(g.leg(b, v)) + " km)" + (g.traffic[b][v] == 1 ? " <b style='color:#F59F00'>heavy traffic</b>" : "")).append(food(g.city[v])).append("</td></tr>"); b = v; }
            routePane.setText(h + "</table></body></html>");
            routePane.setCaretPosition(0); tabs.setSelectedIndex(0); status.setText(r.text + ":  " + Math.round(r.km) + " km,  about " + r.time());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { for (UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) if (i.getName().equals("Nimbus")) UIManager.setLookAndFeel(i.getClassName()); } catch (Exception ignored) { }
            new Gui().setVisible(true);
        });
    }
}