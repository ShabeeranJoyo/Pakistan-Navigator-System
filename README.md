# Pakistan Navigator System

## Project Overview

Pakistan Navigator System is a Java Swing-based desktop application designed to help users explore selected cities of Pakistan, discover tourist attractions, and find routes between different cities.

The system provides an interactive map where users can select cities, explore available places, search for destinations, and calculate routes using different pathfinding algorithms.

This project demonstrates the practical implementation of Data Structures and Algorithms (DSA) through a real-world navigation application.

## Project Objectives

- To provide an interactive way to explore cities of Pakistan.
- To help users find routes between selected cities.
- To implement different pathfinding algorithms.
- To demonstrate practical applications of Data Structures and Algorithms.
- To provide city information and tourist attractions through a graphical interface.

## Main Features

### 1. Interactive Map
- Displays selected cities of Pakistan.
- Shows roads connecting different cities.
- Allows users to select cities directly from the map.
- Highlights the selected route.

### 2. Smart Route Planning
Users can select starting and destination cities and choose from three route options:

- **Fastest Route:** Uses Dijkstra's Algorithm with traffic-aware distance weights.
- **Fewest Roads:** Uses Breadth-First Search (BFS) to find a route with the fewest road connections.
- **Any Available Route:** Uses Depth-First Search (DFS) to find an available route.

### 3. City Search
- Search for cities by name.
- Provides autocomplete suggestions.
- Uses a Binary Search Tree (BST) for city searching.

### 4. City and Tourist Places
- Displays city names and provinces.
- Shows available tourist attractions and places.
- Includes parks, restaurants, landmarks, shopping areas, beaches, museums, and lakes.
- Allows filtering places by category.
- Supports sorting places by name and category.

### 5. Traffic Management
- Admin can manage road traffic conditions.
- Roads can be marked as Normal, Heavy Traffic, or Blocked.
- Traffic conditions are displayed using different road colors.
- Blocked roads are excluded from route calculations.

### 6. Route History
- Stores recent route searches.
- Allows users to revisit previous routes.
- Provides a Back button to return to the previous route.

### 7. Route Information
Displays:
- Selected starting and destination cities.
- Total route distance.
- Estimated travel time.
- Number of roads.
- Route directions.
- Restaurants available in cities along the route.

## Data Structures Implemented

The project uses the following data structures:

| Data Structure | Purpose |
|---|---|
| Graph | Represents cities and connecting roads. |
| Linked List | Stores graph adjacency lists and route paths. |
| Stack | Manages route history and the Back operation. |
| Queue | Supports BFS and stores recent route searches. |
| Min Heap | Works as a priority queue in Dijkstra's Algorithm. |
| Binary Search Tree (BST) | Supports city searching and autocomplete suggestions. |
| Arrays | Store city data, roads, distances, and traffic information. |

## Algorithms Implemented

### 1. Dijkstra's Algorithm
Finds a shortest weighted route using a Min Heap priority queue. Traffic conditions are included in the route weights to calculate the fastest estimated route.

### 2. Breadth-First Search (BFS)
Uses a Queue to find a route with the fewest number of roads.

### 3. Depth-First Search (DFS)
Uses a Stack to explore connections and find an available route.

### 4. Merge Sort
Sorts tourist places alphabetically by name.

### 5. Quick Sort
Sorts tourist places by category and then by name.

### 6. Binary Search Tree Operations
Supports searching for cities and generating alphabetical autocomplete suggestions through in-order traversal.

## Technologies Used

- **Programming Language:** Java
- **GUI Framework:** Java Swing
- **Graphics:** Java AWT and Java 2D
- **IDE:** Any Java-compatible IDE
- **Core Concepts:** Data Structures and Algorithms

## System Requirements

- Java Development Kit (JDK 8 or above)
- Windows, Linux, or macOS
- A Java-compatible IDE (optional)

No external libraries are required.

## How to Run the Project

### Step 1: Install Java

Download and install JDK from:

https://www.oracle.com/java/technologies/downloads/

### Step 2: Download the Project

Clone the GitHub repository:

```bash
git clone https://github.com/ShabeeranJoyo/Pakistan-Navigator-System.git
```

Alternatively, download the repository as a ZIP file.

### Step 3: Open the Project

Open the downloaded project folder in your Java IDE or Command Prompt.

### Step 4: Compile the Java File

```bash
javac Navigation_System.java
```

### Step 5: Run the Application

```bash
java Navigation_System
```

The Pakistan Navigator System graphical interface will open.

## How to Use the Application

1. Launch the application.
2. Select a starting city from the From dropdown.
3. Select a destination city from the To dropdown.
4. Choose a route type: Fastest Route, Fewest Roads, or Any Route.
5. Click the Find Route button.
6. View the highlighted route, distance, estimated time, and directions.
7. Click cities on the map to explore their tourist places.
8. Use the search box to find cities.
9. Use the City & Places tab to filter and sort available places.
10. Visit the Recent tab to view previous searches.
11. Open the Admin tab to manage road traffic conditions.

## Project Limitations

- City, road, and tourist place information is stored within the source code.
- Traffic conditions are updated manually through the Admin panel.
- Estimated travel time uses an assumed average speed of 70 km/h.
- The application does not use live GPS or real-time traffic services.
- The displayed routes are based on the predefined road network.

## Project Information

**Project Name:** Pakistan Navigator System

**Project Type:** Data Structures and Algorithms Project

**Programming Language:** Java

**Course:** Data Structures and Algorithms (DSA)

**Department:** Computer Science

**University:** Sukkur IBA University

## GitHub Repository

https://github.com/ShabeeranJoyo/Pakistan-Navigator-System

## Conclusion

Pakistan Navigator System demonstrates how Data Structures and Algorithms can be applied to solve a practical navigation problem. By combining Graphs, Linked Lists, Stacks, Queues, Min Heaps, Binary Search Trees, and sorting and searching algorithms, the application provides an interactive platform for exploring cities and planning routes across Pakistan.
