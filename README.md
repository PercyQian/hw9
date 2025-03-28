## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

# Class: MinHeap
class MinHeap:
    method __init__():
        heap <- empty list   # Internal representation (e.g., array)
    
    method push(value):
        # Insert value into heap and reheapify to maintain the min-heap property.
        insert value into heap
        heapify-up to restore min-heap property
    
    method pop():
        # Remove and return the smallest element (at the root of the heap)
        if heap is not empty:
            smallest <- heap[0]
            remove heap[0] and reheapify to restore min-heap property
            return smallest
        else:
            error "Heap is empty"
    
    method isEmpty():
        return (heap is empty)

# Class: Scheduler
class Scheduler:
    method scheduleExecution(permutation, indices):
        # permutation: list of integers (a permutation of 1...n)
        # indices: sorted list of query indices (each between 1 and n)
        
        minHeap <- new MinHeap()        # To store available numbers
        result <- new list()            # To store output for each query
        curr <- 0                       # Pointer for processed permutation elements
        
        for each query_index in indices:  # Process queries in sorted order
            # Extend the available set to include elements up to the current query index
            while curr < query_index:
                curr <- curr + 1
                value <- permutation[curr]
                minHeap.push(value)
            end while
            
            # Retrieve and remove the smallest available number
            chosen <- minHeap.pop()
            
            # Append chosen number to result list (ensuring distinctness automatically)
            append chosen to result
        end for
        
        return result
