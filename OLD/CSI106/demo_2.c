#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define M 307

// Node trong linked list
typedef struct Node {
    int key;
    char name[50];
    float value;
    struct Node* next;
} Node;

// Bảng hash
Node* table[M];

// Hash function
int hash_function(int key) {
    return (key % M + 1);
}

// Insert với Chaining
void insert(int key, char* name, float value) {
    int address = hash_function(key);
    
    // Tạo node mới
    Node* new_node = (Node*)malloc(sizeof(Node));
    new_node->key = key;
    strcpy(new_node->name, name);
    new_node->value = value;
    new_node->next = NULL;
    
    // Thêm vào đầu linked list
    if (table[address] == NULL) {
        table[address] = new_node;
        printf("Insert key %d -> address %d (Home)\n", key, address);
    } else {
        new_node->next = table[address];
        table[address] = new_node;
        printf("Insert key %d -> address %d (Overflow)\n", key, address);
    }
}

// Search với Chaining
Node* search(int key) {
    int address = hash_function(key);
    Node* current = table[address];
    
    // Duyệt linked list
    while (current != NULL) {
        if (current->key == key) {
            return current;
        }
        current = current->next;
    }
    return NULL;
}

// Hiển thị bảng
void display_table() {
    printf("\n--- HASH TABLE (CHAINING) ---\n");
    for (int i = 0; i < M; i++) {
        if (table[i] != NULL) {
            printf("[%d] -> ", i);
            Node* current = table[i];
            while (current != NULL) {
                printf("[%d|%s|%.2f]", current->key, current->name, current->value);
                if (current->next != NULL) printf(" -> ");
                current = current->next;
            }
            printf("\n");
        }
    }
}

int main() {
    // Khởi tạo
    for (int i = 0; i < M; i++) {
        table[i] = NULL;
    }
    
    // Insert - giống slide
    printf("=== INSERT ===\n");
    insert(123013, "Doc Lee", 1105.45);
    insert(151564, "Rich White", 708.22);
    
    // Hiển thị
    display_table();
    
    // Search
    printf("\n=== SEARCH ===\n");
    Node* r = search(123015);
    if (r != NULL) {
        printf("Found: %s - %.2f\n", r->name, r->value);
    }
    
    r = search(151564);
    if (r != NULL) {
        printf("Found: %s - %.2f\n", r->name, r->value);
    }
    
    return 0;
}